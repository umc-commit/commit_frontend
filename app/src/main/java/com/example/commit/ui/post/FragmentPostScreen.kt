package com.example.commit.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionDetailResponse
import com.example.commit.connection.dto.CommissionDetail
import com.example.commit.fragment.FragmentChat
import com.example.commit.fragment.FragmentChatDetail
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.viewmodel.ArtistViewModel
import com.example.commit.viewmodel.CommissionFormViewModel
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
    private val artistViewModel: ArtistViewModel by viewModels()
    private val commissionFormViewModel: CommissionFormViewModel by viewModels()
    
    // 커미션 상세 정보를 저장하여 썸네일 URL 접근용
    private var currentCommissionDetail: CommissionDetail? = null

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
                val artistBlock by artistViewModel.artistBlock.collectAsState()
                val artistError by artistViewModel.artistError.collectAsState()

                LaunchedEffect(commission) {
                    Log.d("FragmentPostScreen", "commission 데이터 변경됨: $commission")
                }

                commission?.let { detail ->
                    currentCommissionDetail = detail // 커미션 상세 정보 저장
                    val fallbackThumb = detail.images.firstOrNull()?.imageUrl
                    PostScreen(
                        title = detail.title,
                        tags = listOf(detail.category) + detail.tags,
                        minPrice = detail.minPrice,
                        summary = detail.summary,
                        content = detail.content,
                        images = detail.images.map { it.imageUrl },
                        isBookmarked = detail.isBookmarked,
                        imageCount = detail.images.size,
                        currentIndex = 0,
                        commissionId = detail.id,
                        onReviewListClick = { /* TODO: 리뷰 화면 이동 */ },
                        onChatClick = {
                            // 신청서 제출 여부 확인 후 → 기존 방 이동 or 생성
                            checkApplicationStatus(detail.id, detail.title, fallbackThumb)
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
        
        // BottomNavigation 숨기기
        (requireActivity() as? MainActivity)?.showBottomNav(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.showBottomNav(true)
    }

    /** 신청서 제출 여부 확인 → 기존 방 있으면 이동, 없으면 생성 */
    private fun checkApplicationStatus(commissionId: Int, commissionTitle: String,fallbackThumbnailUrl: String?) {
        commissionFormViewModel.checkApplicationStatus(
            commissionId = commissionId.toString(),
            context = requireContext()
        ) { hasSubmitted ->
            openOrCreateChatroom(commissionId, commissionTitle, hasSubmitted, fallbackThumbnailUrl)
        }
    }

    /** 목록 조회로 commissionId 매칭 방 탐색 → 이동 / 생성 */
    private fun openOrCreateChatroom(
        commissionId: Int,
        commissionTitle: String,
        hasSubmitted: Boolean,
        fallbackThumbnailUrl: String?
    ) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        api.getChatroomList().enqueue(object :
            Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
            ) {
                if (!response.isSuccessful) {
                    Log.w("FragmentPostScreen", "채팅방 목록 조회 실패 → 생성 fallback: ${response.code()}")
                    createChatroom(commissionId, commissionTitle, fallbackThumbnailUrl)
                    return
                }
                val list = response.body()?.success.orEmpty()
                val existing = list.firstOrNull { it.commissionId == commissionId.toString() }

                if (existing != null) {
                    val chatroomId = existing.chatroomId.toIntOrNull() ?: -1
                    val authorName = existing.artistNickname.ifBlank { "작가" }
                    Log.d("FragmentPostScreen", "기존 채팅방 발견 → id=$chatroomId, author=$authorName")
                    val thumb = currentCommissionDetail?.images?.firstOrNull()?.imageUrl
                        ?: fallbackThumbnailUrl
                        ?: ""

                    navigateToChatroomWithArgs(
                        commissionId = commissionId,
                        commissionTitle = commissionTitle,
                        authorName = authorName,
                        chatroomId = chatroomId,
                        hasSubmittedApplication = hasSubmitted,
                        thumbnailUrl = thumb // ✅ 저장된 커미션 상세에서 썸네일 가져오기
                    )
                } else {
                    createChatroom(commissionId, commissionTitle, fallbackThumbnailUrl)
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                t: Throwable
            ) {
                Log.w("FragmentPostScreen", "채팅방 목록 조회 네트워크 오류 → 생성 fallback", t)
                createChatroom(commissionId, commissionTitle, fallbackThumbnailUrl)
            }
        })
    }

    /** 채팅방 생성 → 목록 재조회로 artistNickname 확보 후 이동 */
    private fun createChatroom(commissionId: Int, commissionTitle: String, fallbackThumbnailUrl: String?) {
        Log.d("FragmentPostScreen", "createChatroom 호출 - commissionId=$commissionId, title=$commissionTitle")
        val api = RetrofitObject.getRetrofitService(requireContext())

        // 1) 커미션 상세에서 artistId 획득
        api.getCommissionDetail(commissionId.toString()).enqueue(object : Callback<CommissionDetailResponse> {
            override fun onResponse(
                call: Call<CommissionDetailResponse>,
                response: Response<CommissionDetailResponse>
            ) {
                if (!response.isSuccessful) {
                    Log.e("FragmentPostScreen", "커미션 상세 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "커미션 정보 조회에 실패했습니다", Toast.LENGTH_SHORT).show()
                    return
                }
                val commission = response.body()?.success ?: run {
                    Toast.makeText(requireContext(), "커미션 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                    return
                }

                val artistId = commission.artistId.takeIf { it != 0 } ?: run {
                    val temp = if (commissionId % 2 == 0) 2 else 1
                    Log.w("FragmentPostScreen", "artistId=0 → 임시 사용: $temp")
                    temp
                }

                // 2) 채팅방 생성 (JWT에서 userId 추출 → 바디는 artistId/commissionId만)
                        val req = RetrofitClient.CreateChatroomRequest(
            artistId = artistId,
            commissionId = commissionId.toString()
        )
                api.createChatroom(req).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                        resp: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
                    ) {
                        if (!resp.isSuccessful) {
                            Log.e("FragmentPostScreen", "채팅방 생성 실패: ${resp.errorBody()?.string()}")
                            Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                            return
                        }
                        val created = resp.body()?.success ?: run {
                            Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                            return
                        }
                        val createdId = created.id.toIntOrNull() ?: -1
                        Log.d("FragmentPostScreen", "채팅방 생성 성공: id=${created.id}(int=$createdId)")

                        // 3) 목록 재조회로 artistNickname 확보 후 이동
                        api.getChatroomList().enqueue(object :
                            Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
                            override fun onResponse(
                                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                                listResp: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
                            ) {
                                val list = listResp.body()?.success.orEmpty()
                                val item = list.firstOrNull {
                                    it.chatroomId == created.id || it.commissionId == commissionId.toString()
                                }
                                val authorName = item?.artistNickname?.ifBlank { "작가" } ?: "작가"

                                val thumb = commission.images.firstOrNull()?.imageUrl
                                    ?: fallbackThumbnailUrl
                                    ?: ""

                                navigateToChatroomWithArgs(
                                    commissionId = commissionId,
                                    commissionTitle = commissionTitle,
                                    authorName = authorName,
                                    chatroomId = createdId,
                                    hasSubmittedApplication = false,
                                    thumbnailUrl = thumb // ✅ 첫 번째 이미지를 썸네일로 사용
                                )

                                // (옵션) 채팅 목록 새로고침
                                try {
                                    val main = requireActivity() as MainActivity
                                    val chatFrag = main.supportFragmentManager.fragments
                                        .find { it is FragmentChat } as? FragmentChat
                                    chatFrag?.refreshChatroomList()
                                } catch (_: Exception) { /* ignore */ }

                                Toast.makeText(requireContext(), "채팅방이 생성되었습니다", Toast.LENGTH_SHORT).show()
                            }

                            // (createChatroom 내부) 목록 재조회 실패 → 닉네임 기본값으로 이동 분기
                            override fun onFailure(
                                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                                t: Throwable
                            ) {
                                Log.w("FragmentPostScreen", "목록 재조회 실패 → 닉네임 기본값으로 이동", t)

                                // 실패 시에도 썸네일 계산 후 함께 전달
                                val thumb = commission.images.firstOrNull()?.imageUrl
                                    ?: fallbackThumbnailUrl
                                    ?: ""

                                navigateToChatroomWithArgs(
                                    commissionId = commissionId,
                                    commissionTitle = commissionTitle,
                                    authorName = "작가",
                                    chatroomId = createdId,
                                    hasSubmittedApplication = false,
                                    thumbnailUrl = thumb          // ★ 추가
                                )
                            }

                        })
                    }

                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                        t: Throwable
                    ) {
                        Log.e("FragmentPostScreen", "채팅 생성 네트워크 오류", t)
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(call: Call<CommissionDetailResponse>, t: Throwable) {
                Log.e("FragmentPostScreen", "커미션 상세 네트워크 오류", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** 실제 이동: 작가 닉네임/신청서 제출 여부까지 전달 */
    private fun navigateToChatroomWithArgs(
        commissionId: Int,
        commissionTitle: String,
        authorName: String,
        chatroomId: Int,
        hasSubmittedApplication: Boolean,
        thumbnailUrl: String? = null
    ) {
        val fragment = FragmentChatDetail().apply {
            arguments = Bundle().apply {
                putString("chatName", commissionTitle)
                putString("authorName", authorName)
                putInt("chatroomId", chatroomId)
                putString("thumbnailUrl", thumbnailUrl ?: "")
                putInt("artistId", currentCommissionDetail?.artistId ?: -1)
                putInt("requestId", -1)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
