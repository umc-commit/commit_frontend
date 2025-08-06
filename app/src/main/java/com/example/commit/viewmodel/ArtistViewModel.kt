package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.data.model.ArtistSuccessData
import com.example.commit.repository.ArtistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtistViewModel : ViewModel() {

    private val _artistInfo = MutableStateFlow<ArtistSuccessData?>(null)
    val artistInfo: StateFlow<ArtistSuccessData?> get() = _artistInfo

    companion object {
        private const val TAG = "ArtistViewModel"
    }

    fun loadArtistInfo(context: Context, commissionId: Int) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "작가 정보 요청 시작: id=$commissionId")
                val result = ArtistRepository.getArtistInfo(context, commissionId)
                _artistInfo.value = result
                if (result != null) {
                    Log.d(TAG, "작가 정보 로딩 성공: ${result.artist.nickname}")
                } else {
                    Log.e(TAG, "작가 정보 응답이 null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "작가 정보 로딩 중 오류 발생", e)
            }
        }
    }
}
