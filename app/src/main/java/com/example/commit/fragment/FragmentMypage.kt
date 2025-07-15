package com.example.commit.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import com.example.commit.databinding.FragmentMypageBinding
import com.example.commit.databinding.ProfileBottomsheetBinding

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

        binding.dropdownIcon.setOnClickListener {
            showProfileBottomSheet()
        }

        return binding.root
    }

    private fun showProfileBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = ProfileBottomsheetBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // 배경 투명 & 그림자 효과
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
