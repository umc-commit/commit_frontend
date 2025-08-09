package com.example.commit.ui.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import com.example.commit.fragment.FragmentChat
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.viewmodel.PostViewModel
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

                LaunchedEffect(commission) {
                    Log.d("FragmentPostScreen", "commission 데이터 변경됨: $commission")
                }
                commission?.let {
                    PostScreen(
                        title = it.title,
                        tags = listOf(it.category) + it.tags,
                        minPrice = it.minPrice,
                        summary = it.summary,
                        content = it.content,
                        images = it.images.map { image -> image.imageUrl },
                        isBookmarked = it.isBookmarked,
                        imageCount = it.images.size,
                        currentIndex = 0,
                        commissionId = it.id,
                        onReviewListClick = {
                            val intent = Intent(context, WrittenReviewsActivity::class.java)
                            context.startActivity(intent)
                        },
                        onChatClick = {
                            Log.d("FragmentPostScreen", "채팅하기 버튼 클릭 - 커미션 ID: ${it.id}, 제목: ${it.title}")
                            createChatroom(it.id, it.title)
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
    
    private fun createChatroom(commissionId: Int, commissionTitle: String) {
        Log.d("FragmentPostScreen", "createChatroom 메서드 호출됨 - commissionId: $commissionId, title: $commissionTitle")
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        // 임시 값들 (실제로는 SharedPreferences나 다른 방법으로 가져와야 함)
        val currentUserId = 1
        val artistId = 1 // 임시 - 실제로는 커미션 상세 정보에서 가져와야 함
        val artistName = "키르" // 임시 - 실제로는 커미션 상세 정보에서 가져와야 함
        
        // 임시: 실제로는 커미션 신청 후 생성되는 requestId를 사용해야 함
        val tempRequestId = 3 // 테스트용 고정값
        val request = RetrofitClient.CreateChatroomRequest(
            consumerId = currentUserId,
            artistId = artistId,
            requestId = tempRequestId
        )
        Log.d("FragmentPostScreen", "임시 requestId 사용: $tempRequestId (원래 커미션ID: $commissionId)")

        Log.d("FragmentPostScreen", "API 호출 시작 - request: $request")
        api.createChatroom(request).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                Log.d("FragmentPostScreen", "API 응답 받음: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                Log.d("FragmentPostScreen", "응답 바디: ${response.body()}")
                
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentPostScreen", "채팅방 생성 성공: ${data.id}")
                        
                        // 생성된 채팅방으로 이동
                        val fragment = FragmentPostChatDetail().apply {
                            arguments = bundleOf(
                                "chatName" to commissionTitle,
                                "authorName" to artistName,
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
                        Toast.makeText(
                            requireContext(),
                            "채팅방 생성에 실패했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("FragmentPostScreen", "채팅방 생성 실패: ${response.code()}")
                    Log.e("FragmentPostScreen", "에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        requireContext(),
                        "채팅방 생성에 실패했습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("FragmentPostScreen", "채팅방 생성 네트워크 오류: ${t.message}", t)
                Toast.makeText(
                    requireContext(),
                    "네트워크 오류가 발생했습니다: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}