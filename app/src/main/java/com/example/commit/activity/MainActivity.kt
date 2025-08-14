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
import android.content.Intent



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 show_signup_bottom_sheet 값 가져오기
        val showSignupBottomSheet = intent.getBooleanExtra("show_signup_bottom_sheet", false)
        val openFragment = intent.getStringExtra("openFragment")

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

        supportFragmentManager.addOnBackStackChangedListener {
            val top = supportFragmentManager.findFragmentById(binding.NavFrame.id)
            showBottomNav(top !is FragmentPostChatDetail && top !is FragmentPostScreen)
        }
    }

    // SINGLE_TOP/CLEAR_TOP로 기존 MainActivity가 재사용될 때 여기로 옴
    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        setIntent(newIntent) // getIntent() 갱신
        val openFragment = newIntent.getStringExtra("openFragment")
        if (openFragment != null) {
            handleOpenFragment(openFragment, newIntent)
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
}
