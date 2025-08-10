package com.example.commit.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.request.form.CommissionFormScreen
import com.example.commit.viewmodel.CommissionFormViewModel

class CommissionFormActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Intent에서 commissionId를 가져오거나 기본값 사용
        val commissionId = intent.getStringExtra("commissionId") ?: "1"
        
        setContent {
            CommitTheme {
                val viewModel: CommissionFormViewModel = viewModel()
                CommissionFormScreen(
                    commissionId = commissionId,
                    viewModel = viewModel,
                    onNavigateToSuccess = { /* 성공 시 처리 */ }
                )
            }
        }
    }
}