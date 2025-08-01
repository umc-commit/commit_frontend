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
            if (openFragment == "chat") {
                // 채팅 프래그먼트 바로 열기
                supportFragmentManager
                    .beginTransaction()
                    .replace(binding.NavFrame.id, FragmentChat())
                    .commitAllowingStateLoss()

                // 하단 네비게이션 채팅 탭 선택
                binding.BottomNavi.selectedItemId = R.id.nav_chat

            } else {
                // 기본 홈 프래그먼트 로드
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
    }

    private fun initBottomNavigation() {
        bottomNavigationView = binding.BottomNavi
        // onCreate에서 이미 초기 프래그먼트를 로드했으므로, 여기서 다시 R.id.nav_home을 선택할 필요는 없습니다.
        // bottomNavigationView.selectedItemId = R.id.nav_home // 이 줄은 제거하거나 주석 처리

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
}
