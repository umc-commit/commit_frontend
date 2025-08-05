package com.example.commit.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.data.model.CommissionDetail
import com.example.commit.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _commissionDetail = MutableStateFlow<CommissionDetail?>(null)
    val commissionDetail: StateFlow<CommissionDetail?> = _commissionDetail

    fun loadCommissionDetail(context: Context, id: Int) {
        viewModelScope.launch {
            val result = PostRepository.getCommissionDetail(context, id)
            if (result != null) {
                android.util.Log.d("PostViewModel", "API 응답 성공: ${result.title}")
            } else {
                android.util.Log.e("PostViewModel", "API 응답 실패: result == null")
            }
            _commissionDetail.value = result
        }
    }
}

