package com.example.commit.ui.point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.fragment.FragmentMypage

class FragmentPoint : Fragment() {

    companion object {
        fun newInstance(hideBottomBar: Boolean): FragmentPoint {
            return FragmentPoint().apply {
                arguments = Bundle().apply {
                    putBoolean("hideBottomBar", hideBottomBar)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navigateToMyPage = remember { mutableStateOf(false) }

                LaunchedEffect(navigateToMyPage.value) {
                    if (navigateToMyPage.value) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, FragmentMypage())
                            .addToBackStack(null)
                            .commit()
                    }
                }

                PointChargeScreen(
                    onCancelClicked = {
                        navigateToMyPage.value = true
                    },
                    onChargeSuccess = {
                        // 결제 성공 토스트 + 이전 페이지로 이동
                        Toast.makeText(
                            requireContext(),
                            "결제가 완료되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hideBottomBar = arguments?.getBoolean("hideBottomBar") ?: false
        if (hideBottomBar) {
            (requireActivity() as? MainActivity)?.showBottomNav(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.showBottomNav(true)
    }
}
