package com.example.commit.ui.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.widget.Toast
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.dto.CommissionDetailResponse
import com.example.commit.fragment.FragmentChat
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.viewmodel.PostViewModel
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ArtistViewModel
import com.example.commit.ui.post.components.TabType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentPostScreen : Fragment() {

    companion object {
        private const val ARG_COMMISSION_ID = "commission_id"
        fun newInstance(commissionId: Int): FragmentPostScreen {
            return FragmentPostScreen().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COMMISSION_ID, commissionId)
                    putBoolean("hideBottomBar", true)
                }
            }
        }
    }

    private val viewModel: PostViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()
    private val commissionFormViewModel: CommissionFormViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val commissionId = arguments?.getInt(ARG_COMMISSION_ID) ?: -1
        Log.d("FragmentPostScreen", "넘겨받은 commissionId: $commissionId")

        viewModel.loadCommissionDetail(requireContext(), commissionId)

        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                val commission by viewModel.commissionDetail.collectAsState()
                val artistBlock by artistViewModel.artistBlock.collectAsState()  // ▼ 추가
                val artistError by artistViewModel.artistError.collectAsState()  // ▼ 추가

                LaunchedEffect(commission) {
                    Log.d("FragmentPostScreen", "commission 데이터 변경됨: $commission")
                }

                commission?.let { detail ->
                    PostScreen(
                        title = detail.title,
                        tags = listOf(detail.category) + detail.tags,
                        minPrice = detail.minPrice,
                        summary = detail.summary,
                        content = detail.content,
                        images = detail.images.map { img -> img.imageUrl },
                        isBookmarked = detail.isBookmarked,
                        imageCount = detail.images.size,
                        currentIndex = 0,
                        commissionId = detail.id,
                        onReviewListClick = { /* 리뷰 화면 이동 처리 */ },
                        onChatClick = {
                            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                                putExtra("openFragment", "chat")
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            }
                            requireContext().startActivity(intent)
                        },
                        onBookmarkToggle = { newState ->
                            viewModel.toggleBookmark(requireContext(), detail.id, newState)
                        }
                    )


                }
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
    
    private fun checkApplicationStatus(commissionId: Int, commissionTitle: String) {
        // CommissionFormViewModel을 통해 신청서 제출 상태 확인
        commissionFormViewModel.checkApplicationStatus(
            commissionId = commissionId.toString(),
            context = requireContext()
        ) { hasSubmitted ->
            if (hasSubmitted) {
                // 신청서가 제출된 경우: 채팅방으로 이동 (CommissionRequestBubble 표시)
                navigateToChatroom(commissionId, commissionTitle)
            } else {
                // 신청서가 제출되지 않은 경우: 채팅방 생성
                createChatroom(commissionId, commissionTitle)
            }
        }
    }
    
    private fun navigateToChatroom(commissionId: Int, commissionTitle: String) {
        // 신청서가 제출된 커미션의 경우 CommissionRequestBubble이 표시되는 채팅방으로 이동
        Log.d("FragmentPostScreen", "신청서 제출된 커미션 - 채팅방으로 이동")
        
        // 임시 값들 (실제로는 API에서 가져와야 함)
        val artistName = "키르"
        val tempChatroomId = 999 // 테스트용 고정값 (실제로는 기존 채팅방 ID 사용)
        
        // CommissionRequestBubble이 표시되는 채팅방으로 이동
        val fragment = FragmentPostChatDetail().apply {
            arguments = bundleOf(
                "chatName" to commissionTitle,
                "authorName" to artistName,
                "chatroomId" to tempChatroomId,
                "sourceFragment" to "FragmentPostScreen",
                "commissionId" to commissionId,
                "hasSubmittedApplication" to true // 신청서 제출됨 표시
            )
        }
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun createChatroom(commissionId: Int, commissionTitle: String) {
        Log.d("FragmentPostScreen", "createChatroom 메서드 호출됨 - commissionId: $commissionId, title: $commissionTitle")
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        // 먼저 커미션 상세 정보를 조회해서 artistId를 가져옴
        api.getCommissionDetail(commissionId).enqueue(object : Callback<CommissionDetailResponse> {
            override fun onResponse(
                call: Call<CommissionDetailResponse>,
                response: Response<CommissionDetailResponse>
            ) {
                Log.d("FragmentPostScreen", "커미션 상세 조회 응답: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val commissionData = response.body()?.success
                    Log.d("FragmentPostScreen", "커미션 응답 전체: ${response.body()}")
                    Log.d("FragmentPostScreen", "커미션 success 데이터: $commissionData")
                    
                    if (commissionData != null) {
                        val artistId = commissionData.artistId
                        Log.d("FragmentPostScreen", "커미션에서 가져온 artistId: $artistId")
                        Log.d("FragmentPostScreen", "커미션 전체 데이터: $commissionData")
                        
                        // artistId가 0이면 다른 방법으로 시도
                        if (artistId == 0) {
                            Log.w("FragmentPostScreen", "artistId가 0입니다. 다른 방법으로 시도합니다.")
                            // 임시로 commissionId를 artistId로 사용 (테스트용)
                            val tempArtistId = if (commissionId % 2 == 0) 2 else 1
                            Log.d("FragmentPostScreen", "임시 artistId 사용: $tempArtistId")
                            getUserProfileAndCreateChatroom(api, tempArtistId, commissionId, commissionTitle)
                        } else {
                            // 정상적인 artistId 사용
                            getUserProfileAndCreateChatroom(api, artistId, commissionId, commissionTitle)
                        }
                    } else {
                        Log.e("FragmentPostScreen", "커미션 데이터가 없음")
                        Toast.makeText(requireContext(), "커미션 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentPostScreen", "커미션 상세 조회 실패: ${response.code()}")
                    Log.e("FragmentPostScreen", "커미션 에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "커미션 정보 조회에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<CommissionDetailResponse>,
                t: Throwable
            ) {
                Log.e("FragmentPostScreen", "커미션 상세 조회 네트워크 오류", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfileAndCreateChatroom(
        api: RetrofitAPI,
        artistId: Int,
        commissionId: Int,
        commissionTitle: String
    ) {
        // 사용자 프로필을 조회해서 실제 userId 사용
        api.getMyProfile().enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>
            ) {
                Log.d("FragmentPostScreen", "프로필 조회 응답: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val profileData = response.body()?.success
                    if (profileData != null) {
                        val currentUserId = profileData.user.userId?.toIntOrNull() ?: 1
                        Log.d("FragmentPostScreen", "현재 사용자 ID: $currentUserId")
                        
                        // 실제 사용자 ID와 아티스트 ID로 채팅방 생성
                        createChatroomWithCorrectData(api, currentUserId, artistId, commissionId, commissionTitle)
                    } else {
                        Log.e("FragmentPostScreen", "프로필 데이터가 없음")
                        Toast.makeText(requireContext(), "사용자 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentPostScreen", "프로필 조회 실패: ${response.code()}")
                    Toast.makeText(requireContext(), "사용자 정보 조회에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                t: Throwable
            ) {
                Log.e("FragmentPostScreen", "프로필 조회 네트워크 오류", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createChatroomWithCorrectData(
        api: RetrofitAPI,
        consumerId: Int,
        artistId: Int,
        commissionId: Int,
        commissionTitle: String
    ) {
        // commissionId를 requestId로 사용 (또는 별도로 생성)
        val requestId = commissionId
        
        Log.d("FragmentPostScreen", "올바른 데이터로 채팅방 생성 시도")
        Log.d("FragmentPostScreen", "consumerId: $consumerId, artistId: $artistId, requestId: $requestId")
        
        val request = RetrofitClient.CreateChatroomRequest(
            consumerId = consumerId,
            artistId = artistId,
            requestId = requestId
        )
        Log.d("FragmentPostScreen", "API 호출 시작 - request: $request")
        
        api.createChatroom(request).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                Log.d("FragmentPostScreen", "API 응답 받음: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentPostScreen", "채팅방 생성 성공: ${data.id}")
                        
                        // 생성된 채팅방으로 이동
                        val fragment = FragmentPostChatDetail().apply {
                            arguments = bundleOf(
                                "chatName" to commissionTitle,
                                "authorName" to "작가", // 실제 아티스트 이름을 사용할 수 있음
                                "chatroomId" to data.id,
                                "sourceFragment" to "FragmentPostScreen",
                                "commissionId" to commissionId
                            )
                        }
                        
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()
                        
                        // 채팅방 목록 새로고침
                        try {
                            val mainActivity = requireActivity() as MainActivity
                            val fragmentChat = mainActivity.supportFragmentManager.fragments
                                .find { it is FragmentChat } as? FragmentChat
                            fragmentChat?.refreshChatroomList()
                        } catch (e: Exception) {
                            Log.w("FragmentPostScreen", "채팅방 목록 새로고침 실패", e)
                        }
                        
                        Toast.makeText(
                            requireContext(),
                            "채팅방이 생성되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("FragmentPostScreen", "채팅방 생성 실패: success 데이터가 없음")
                        Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentPostScreen", "채팅방 생성 실패: ${response.code()}")
                    Log.e("FragmentPostScreen", "에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("FragmentPostScreen", "채팅방 생성 네트워크 오류: ${t.message}", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}