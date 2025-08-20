package com.example.commit.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.commit.ui.request.FragmentRequest
import android.util.Log
import com.example.commit.R
import com.example.commit.fragment.FragmentBookmark
import com.example.commit.fragment.FragmentChat
import com.example.commit.fragment.FragmentHome
import com.example.commit.fragment.FragmentMypage
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.ui.post.FragmentPostScreen
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Intent
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.commit.activity.login.LoginActivity


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 show_signup_bottom_sheet 값 가져오기
        val showSignupBottomSheet = intent.getBooleanExtra("show_signup_bottom_sheet", false)
        var openFragment = intent.getStringExtra("openFragment")

        // openFragment가 비어 있어도 FCM data(type)로부터 추론
        if (openFragment == null) {
            openFragment = resolveOpenFragmentFromDataIfNeeded(intent)
        }

        // 액티비티가 처음 생성될 때만 초기 프래그먼트를 로드하고 바텀 시트 인자를 전달
        if (savedInstanceState == null) {
            if (openFragment != null) {
                // 최초 진입도 공통 라우팅으로 처리
                handleOpenFragment(openFragment, intent)
            } else {
                // 기본 홈
                val initialFragment = FragmentHome().apply {
                    arguments = Bundle().apply {
                        putBoolean("show_signup_bottom_sheet", showSignupBottomSheet)
                    }
                }
                supportFragmentManager
                    .beginTransaction()
                    .replace(binding.NavFrame.id, initialFragment)
                    .commitAllowingStateLoss()

                if (showSignupBottomSheet) {
                    intent.removeExtra("show_signup_bottom_sheet")
                }
            }
        }

        initBottomNavigation()

        // 백스택 리스너 설정 이후 아무데나: 최초 진입 시 등록 한번
        registerFcmTokenIfNeeded()

        supportFragmentManager.addOnBackStackChangedListener {
            val top = supportFragmentManager.findFragmentById(binding.NavFrame.id)
            
            // PostScreen이 표시 중인 FragmentHome도 바텀바 숨김 대상에 포함
            val shouldHideBottomNav = top is FragmentPostChatDetail || 
                                    top is FragmentPostScreen || 
                                    (top is FragmentHome && isPostScreenShowing(top))
            
            showBottomNav(!shouldHideBottomNav)
        }
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        setIntent(newIntent)

        newIntent.data?.let { uri ->
            val isKakaoCb = uri.scheme == "commit" &&
                    uri.host == "oauth2" &&
                    (uri.path?.startsWith("/callback/kakao") == true)
            if (isKakaoCb) {
                // LoginActivity에서 처리하는 게 원칙이므로 여기선 UI만 안전화
                newIntent.data = null
                // 홈 세팅 정도
                supportFragmentManager.beginTransaction()
                    .replace(binding.NavFrame.id, FragmentHome())
                    .commit()
                showBottomNav(true)
                return
            }
        }

        // 우선적으로 openFragment 키 사용
        var openFragment = newIntent.getStringExtra("openFragment")

        // ★ 없으면 FCM data(type)로부터 추론
        if (openFragment == null) {
            openFragment = resolveOpenFragmentFromDataIfNeeded(newIntent)
        }

        openFragment?.let {
            handleOpenFragment(it, newIntent)
        }
    }

    // 공통 라우팅
    private fun handleOpenFragment(openFragment: String, srcIntent: Intent) {
        when (openFragment) {
            "postChatDetail" -> {
                openPostChatDetailFromIntent(srcIntent)
                showBottomNav(false)
            }
            "chat" -> {
                supportFragmentManager.beginTransaction()
                    .replace(binding.NavFrame.id, FragmentChat())
                    .commitAllowingStateLoss()
                binding.BottomNavi.selectedItemId = R.id.nav_chat
                showBottomNav(true)
            }
            // 필요 시 케이스 추가
        }
        // 일회성이면 제거
        srcIntent.removeExtra("openFragment")
    }

    // FCM system-tray 클릭으로 진입한 경우, data(type 등)만 있는 케이스에서 openFragment를 추론
    private fun resolveOpenFragmentFromDataIfNeeded(srcIntent: Intent): String? {
        val extras = srcIntent.extras ?: return null

        // FCM에서 왔는지 간단히 식별 (우리가 넣는 fromPush 또는 서버 data의 timestamp)
        val isFromPush = extras.getBoolean("fromPush", false) || extras.containsKey("timestamp")
        if (!isFromPush) return null

        // 서버에서 내려준 타입으로 라우팅 결정
        return when (extras.getString("type").orEmpty()) {
            "chat_message" -> "postChatDetail"
            "chat_list"    -> "chat"
            else           -> null // 기타는 홈 등 기본 진입
        }
    }

    private fun initBottomNavigation() {
        bottomNavigationView = binding.BottomNavi

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            // 닉네임 화면에서 넘어온 경우에만 바텀 시트를 띄우도록 인자를 전달
            val showBottomSheetForHome = intent.getBooleanExtra("show_signup_bottom_sheet", false)

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val homeFragment = FragmentHome().apply {
                        arguments = Bundle().apply {
                            putBoolean("show_signup_bottom_sheet", showBottomSheetForHome)
                        }
                    }
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, homeFragment)
                        .commitAllowingStateLoss()
                    // 바텀 시트가 한 번 띄워진 후에는 Intent에서 해당 값 제거
                    if (showBottomSheetForHome) {
                        intent.removeExtra("show_signup_bottom_sheet")
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.nav_bookmark -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, FragmentBookmark())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_request -> {
                    Log.d("MainActivity", "신청함 눌림")
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, FragmentRequest())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_chat -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, FragmentChat())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_mypage -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, FragmentMypage())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    // 바텀바 숨기기/보이기 메서드
    fun showBottomNav(isVisible: Boolean) {
        binding.BottomNavi.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    
    // FragmentHome에서 PostScreen이 표시 중인지 확인
    private fun isPostScreenShowing(fragmentHome: FragmentHome): Boolean {
        return try {
            val binding = fragmentHome.view?.findViewById<View>(R.id.frameComposeContainer)
            binding?.visibility == View.VISIBLE
        } catch (e: Exception) {
            false
        }
    }

    private fun openPostChatDetailFromIntent(srcIntent: android.content.Intent) {
        val chatroomId = srcIntent.getIntExtra("chatroomId", -1)
        if (chatroomId == -1) {
            supportFragmentManager.beginTransaction()
                .replace(binding.NavFrame.id, FragmentChat())
                .commitAllowingStateLoss()
            binding.BottomNavi.selectedItemId = R.id.nav_chat
            return
        }

        // 기저 프래그먼트가 없으면 먼저 채팅 탭 깔기 (백스택 X)
        if (supportFragmentManager.findFragmentById(binding.NavFrame.id) == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.NavFrame.id, FragmentChat())
                .commitNowAllowingStateLoss() // 바로 깔아두기
            binding.BottomNavi.selectedItemId = R.id.nav_chat
        }

        val chatName = srcIntent.getStringExtra("chatName") ?: "채팅"
        val authorName = srcIntent.getStringExtra("authorName") ?: ""
        val commissionId = srcIntent.getIntExtra("commissionId", -1)
        val source = srcIntent.getStringExtra("sourceFragment") ?: "MainActivity"

        val frag = FragmentPostChatDetail().apply {
            arguments = Bundle().apply {
                putString("chatName", chatName)
                putString("authorName", authorName)
                putInt("chatroomId", chatroomId)
                putInt("commissionId", commissionId)
                putString("sourceFragment", source)
            }
        }

        // 채팅 상세는 백스택에 올리기
        supportFragmentManager.beginTransaction()
            .replace(binding.NavFrame.id, frag)
            .addToBackStack("postChatDetail")
            .commitAllowingStateLoss()

        // 상세 진입 시 바텀바 숨김
        showBottomNav(false)
    }

    private fun registerFcmTokenIfNeeded() {
        // 액세스 토큰이 있어야 Authorization 헤더가 붙음(인터셉터) :contentReference[oaicite:0]{index=0}
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val prev = prefs.getString("fcmToken", null)
                if (prev == token) return@addOnSuccessListener  // 중복 전송 방지

                val api = RetrofitObject.getRetrofitService(this)
                val body = RetrofitClient.FcmTokenRegisterRequest(fcmToken = token)
                api.registerFcmToken(body).enqueue(object : Callback<
                        RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>
                        > {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                        response: Response<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>
                    ) {
                        if (response.isSuccessful && response.body()?.success != null) {
                            prefs.edit().putString("fcmToken", token).apply()
                        }
                    }
                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                        t: Throwable
                    ) { /* 로깅 정도만 */ }
                })
            }
            .addOnFailureListener { /* 로깅 정도만 */ }
    }

    // 1) private 제거해 공개 함수로(로그아웃 시 사용)
    fun performLogout() {
        val ctx = this
        val api = RetrofitObject.getRetrofitService(ctx)

        //서버에 FCM 토큰 삭제 요청
        api.deleteFcmToken().enqueue(object : Callback<
                RetrofitClient.ApiResponse<RetrofitClient.SimpleMessage>
                > {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.SimpleMessage>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.SimpleMessage>>
            ) { finalizeLocalLogout() }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.SimpleMessage>>,
                t: Throwable
            ) { finalizeLocalLogout() }
        })
    }

    private fun finalizeLocalLogout() {
        // 2) 로컬 토큰/유저 캐시 정리
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit()
            .remove("accessToken")
            .remove("refreshToken")
            .remove("fcmToken") // 우리가 마지막으로 서버에 등록했던 토큰 캐시도 삭제
            .apply()

        Log.d("FCMToken", "로그아웃 후 Fcm Token=${prefs.getString("fcmToken", null)}")

        // 3) 로그인 화면으로 이동
        val i = Intent(this, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        finish()
    }
}
