package com.example.commit.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.activity.login.AgreeFirstActivity
import com.example.commit.activity.login.LoginActivity
import com.example.commit.databinding.FragmentMypageBinding
import com.example.commit.ui.point.FragmentPoint
import com.example.commit.ui.point.FragmentPointHistory
import com.example.commit.activity.mypage.ProfileActivity
import com.example.commit.activity.mypage.MyPageCommissionActivity
import com.example.commit.activity.mypage.ReportActivity
import com.example.commit.databinding.BottomSheetProfileBinding

class FragmentMypage : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var profileSheetBinding: BottomSheetProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        //닉네임 불러오기
        updateNicknameFromPrefs()

        // 프로필 버튼 클릭 시 ProfileActivity로 이동
        binding.profileButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.dropdownIcon.setOnClickListener {
            showProfileBottomSheet()
        }
        binding.writtenReviewsLayout.setOnClickListener {
            val intent = Intent(requireContext(), WrittenReviewsActivity::class.java)
            startActivity(intent)
        }
        binding.commissionReportItemLayout.setOnClickListener {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            startActivity(intent)
        }

        binding.logoutLayout.setOnClickListener {
            // MainActivity에 정의한 performLogout() 호출
            (activity as? MainActivity)?.performLogout()
        }

        // 완료된 커미션 클릭 시 MyPageCommissionActivity로 이동
        binding.completedCommissionsLayout.setOnClickListener {
            val intent = Intent(requireContext(), MyPageCommissionActivity::class.java)
            startActivity(intent)
        }
        binding.chargePointsLayout.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(id, FragmentPoint.newInstance(hideBottomBar = true))
                .addToBackStack(null)
                .commit()
        }
        binding.chargeHistoryItemLayout.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(id, FragmentPointHistory.newInstance(hideBottomBar = true))
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun showProfileBottomSheet() {
        val prefs = requireContext().getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "로지")
        val imageUrl = prefs.getString("imageUri", null)

        val sheetBinding = BottomSheetProfileBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(sheetBinding.root)
            window?.apply {
                setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
                setDimAmount(0.6f)
            }
        }

        // 공통 메서드로 닉네임/이미지 세팅
        loadProfileIntoViews(nickname, imageUrl, sheetBinding.profileName, sheetBinding.profileImage)

        bottomSheetDialog?.show()
    }

    //닉네임 반영
    private fun updateNicknameFromPrefs() {
        val prefs = requireContext().getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "로지")
        val imageUrl = prefs.getString("imageUri", null)

        binding.userName2.text = nickname
        loadProfileIntoViews(nickname, imageUrl, binding.userName, binding.userProfile)
    }

    private fun loadProfileIntoViews(nickname: String?, imageUrl: String?, nameView: TextView, imageView: ImageView) {
        nameView.text = nickname
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_profile)
        }
    }


    //닉네임을 바꾸고 돌아왔을 때 최신 닉네임 반영
    override fun onResume() {
        super.onResume()
        updateNicknameFromPrefs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
        profileSheetBinding = null
    }

}
