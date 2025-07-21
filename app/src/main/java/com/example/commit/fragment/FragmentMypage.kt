package com.example.commit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import com.example.commit.databinding.FragmentMypageBinding
import com.example.commit.databinding.ProfileBottomsheetBinding
import com.example.commit.ui.point.FragmentPoint
import com.example.commit.ui.point.FragmentPointHistory
import com.example.commit.activity.ProfileActivity

class FragmentMypage : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var profileSheetBinding: ProfileBottomsheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 1. 프로필 버튼 클릭 시 ProfileActivity로 이동
        binding.profileButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.dropdownIcon.setOnClickListener {
            showProfileBottomSheet()
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
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = ProfileBottomsheetBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // 배경 투명 & 그림자 효과
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f)
        }

        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
        profileSheetBinding = null
    }
}
