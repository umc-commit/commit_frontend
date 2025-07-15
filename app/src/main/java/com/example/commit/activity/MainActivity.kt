package com.example.commit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.commit.ui.request.FragmentRequest
import android.util.Log
import com.example.commit.R
import com.example.commit.fragment.FragmentBookmark
import com.example.commit.fragment.FragmentHome
import com.example.commit.fragment.FragmentMypage


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()

        val startFragmentId = intent.getIntExtra("start_fragment", R.id.nav_home)
        bottomNavigationView.selectedItemId = startFragmentId

    }

    private fun initBottomNavigation() {

        bottomNavigationView = binding.BottomNavi

        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.NavFrame.id, FragmentHome())
                        .commitAllowingStateLoss()
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
                        .replace(binding.NavFrame.id, FragmentMypage())
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
}