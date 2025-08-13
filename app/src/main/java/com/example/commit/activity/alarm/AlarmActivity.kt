package com.example.commit.activity.alarm

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.adapter.alarm.AlarmAdapter
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import retrofit2.Call
import retrofit2.Response
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import com.example.commit.ui.post.PostScreen
import com.example.commit.viewmodel.PostViewModel
import androidx.core.os.bundleOf
import retrofit2.Callback

class AlarmActivity : AppCompatActivity() {

    private lateinit var settingButton: ImageView
    private var isDeleteMode = false

    private lateinit var backButton: ImageView
    private lateinit var alarmDialog: Dialog
    private lateinit var deleteAllDialog: Dialog

    private val alarmList = mutableListOf<RetrofitClient.NotificationItem>()
    private lateinit var alarmAdapter: AlarmAdapter

    private lateinit var postViewModel: PostViewModel
    private var composeOverlay: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()  // 홈화면으로 돌아감
        }

        settingButton = findViewById(R.id.setting_button)

        settingButton.setOnClickListener {
            if (isDeleteMode) {
                exitDeleteMode()
            } else {
                showAlarmDialog()
            }
        }

        alarmAdapter = AlarmAdapter(
            alarmList,
            onItemDelete = { position ->
                alarmList.removeAt(position)
                alarmAdapter.notifyItemRemoved(position)
            },
            onOpenChat = { item ->
                openDirectChatFromAlarm(item)
            },
            onOpenPostDetail = { commissionId ->
                showPostScreen(commissionId) // ↓ 아래 구현
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm_list)
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // API 호출
        loadNotifications()

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (composeOverlay != null && composeOverlay!!.parent != null) {
                    (composeOverlay!!.parent as ViewGroup).removeView(composeOverlay)
                    composeOverlay = null
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun loadNotifications() {
        val api = RetrofitObject.getRetrofitService(this)
        api.getNotifications()
            .enqueue(object : retrofit2.Callback<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.resultType == "SUCCESS") {
                            alarmList.clear()
                            alarmList.addAll(body.success?.items ?: emptyList())
                            alarmAdapter.notifyDataSetChanged()
                        } else {
                            Log.d("AlarmAPI", body?.error?.reason ?: "알림 불러오기 실패")
                        }
                    } else {
                        Log.d("AlarmAPI", "서버 오류: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>,
                    t: Throwable
                ) {
                    Log.d("AlarmAPI", "네트워크 오류: ${t.message}")
                }
            })
    }

    private fun showAlarmDialog() {
        alarmDialog = Dialog(this)
        alarmDialog.setContentView(R.layout.dialog_alarm)
        alarmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alarmDialog.window?.setDimAmount(0.6f)

        alarmDialog.findViewById<TextView>(R.id.tv_delete_all).setOnClickListener {
            alarmDialog.dismiss()
            showDeleteAllDialog()
        }

        alarmDialog.findViewById<TextView>(R.id.tv_delete_selected).setOnClickListener {
            alarmDialog.dismiss()
            enterDeleteMode()
        }

        alarmDialog.findViewById<TextView>(R.id.tv_mark_all_read).setOnClickListener {
            alarmList.forEachIndexed { index, item ->
                alarmList[index] = item.copy(isRead = true)
            }
            alarmAdapter.notifyDataSetChanged()
            alarmDialog.dismiss()
        }

        alarmDialog.setCanceledOnTouchOutside(true)
        alarmDialog.show()
    }

    private fun showDeleteAllDialog() {
        deleteAllDialog = Dialog(this)
        deleteAllDialog.setContentView(R.layout.dialog_alarm_alldelete)
        deleteAllDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteAllDialog.window?.setDimAmount(0.6f)

        deleteAllDialog.findViewById<Button>(R.id.btn_confirm_delete).setOnClickListener {
            alarmList.clear()
            alarmAdapter.notifyDataSetChanged()
            deleteAllDialog.dismiss()
        }

        deleteAllDialog.setCanceledOnTouchOutside(true)
        deleteAllDialog.show()
    }


    private fun enterDeleteMode() {
        isDeleteMode = true
        settingButton.setImageResource(R.drawable.ic_alarm_x)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm_list)
        (recyclerView.adapter as? AlarmAdapter)?.enableDeleteMode(true)
    }

    private fun exitDeleteMode() {
        isDeleteMode = false
        settingButton.setImageResource(R.drawable.ic_setting)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm_list)
        (recyclerView.adapter as? AlarmAdapter)?.enableDeleteMode(false)
    }

    private fun showPostScreen(commissionId: Int) {
        if (composeOverlay == null) {
            composeOverlay = FrameLayout(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.WHITE)
            }
        } else {
            (composeOverlay!!.parent as? ViewGroup)?.removeView(composeOverlay)
        }

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                val commission by postViewModel.commissionDetail.collectAsState()

                LaunchedEffect(commissionId) {
                    postViewModel.loadCommissionDetail(this@AlarmActivity, commissionId)
                }

                commission?.let { itDetail ->
                    PostScreen(
                        title = itDetail.title,
                        tags = listOf(itDetail.category) + itDetail.tags,
                        minPrice = itDetail.minPrice,
                        summary = itDetail.summary,
                        content = itDetail.content,
                        images = itDetail.images.map { img -> img.imageUrl },
                        isBookmarked = itDetail.isBookmarked,
                        imageCount = itDetail.images.size,
                        currentIndex = 0,
                        commissionId = itDetail.id,
                        onReviewListClick = { /* 필요 시 리뷰 화면 이동 */ },
                        onChatClick = {
                            // 알림 화면에서는 채팅 탭으로 이동(간단 버전)
                            val intent = Intent(this@AlarmActivity, MainActivity::class.java).apply {
                                putExtra("openFragment", "chat")
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }

        composeOverlay!!.removeAllViews()
        composeOverlay!!.addView(composeView)
        (findViewById<ViewGroup>(android.R.id.content)).addView(composeOverlay)
    }

    private fun openPostChatDetail(chatroomId: Int, chatName: String?, authorName: String?) {
        val intent = Intent(this, com.example.commit.activity.MainActivity::class.java).apply {
            putExtra("openFragment", "postChatDetail")
            putExtra("chatName", chatName ?: "채팅")
            putExtra("authorName", authorName ?: "")
            putExtra("chatroomId", chatroomId)
            putExtra("sourceFragment", "AlarmActivity")
        }
        startActivity(intent)
    }

    // 2) 알림 아이템 기반으로 채팅방 생성(또는 확인) 후 상세로 이동
    private fun openDirectChatFromAlarm(item: RetrofitClient.NotificationItem) {
        val api = RetrofitObject.getRetrofitService(this)

        // relatedData 안전 파싱
        fun anyToInt(v: Any?): Int? = when (v) {
            is Number -> v.toInt()
            is String -> v.toIntOrNull()
            else -> null
        }

        val artistId = anyToInt(item.relatedData["artistId"])
        val requestId = anyToInt(item.relatedData["requestId"])
        val chatName = (item.relatedData["commissionTitle"] as? String) ?: item.title
        val authorName = (item.relatedData["nickname"] as? String)
            ?: (item.relatedData["creatorName"] as? String) ?: ""

        // 서버가 알림에 chatroomId를 실어주는 경우가 있으면 바로 진입
        val prebuiltChatroomId = anyToInt(item.relatedData["chatroomId"])
        if (prebuiltChatroomId != null) {
            openPostChatDetail(prebuiltChatroomId, chatName, authorName)
            return
        }

        // 없으면 생성: consumerId는 토큰 기반 식별을 쓰는 게 이상적이나,
        // 현재 DTO가 consumerId를 요구하므로 임시값/저장값 사용 (TODO 교체)
        val currentUserId = 1 // TODO: /api/users/me or SharedPreferences에서 실제 userId로 교체

        // 필수 키가 없으면 안전 종료
        if (artistId == null || requestId == null) {
            Log.d("AlarmActivity", "알림 relatedData에 artistId/requestId 없음 → 채팅 상세로 바로 이동 불가")
            // 차선책: 채팅 탭으로 이동하거나 토스트/로그 처리
            val intent = Intent(this, com.example.commit.activity.MainActivity::class.java).apply {
                putExtra("openFragment", "chat")
            }
            startActivity(intent)
            return
        }

        val req = RetrofitClient.CreateChatroomRequest(
            consumerId = currentUserId,
            artistId = artistId,
            requestId = requestId
        )

        api.createChatroom(req).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (!response.isSuccessful) {
                    Log.d("AlarmActivity", "채팅방 생성 실패(${response.code()})")
                    return
                }
                val data = response.body()?.success ?: run {
                    Log.d("AlarmActivity", "채팅방 생성 실패(응답 없음)")
                    return
                }
                Log.d("AlarmActivity", "채팅방 생성 성공: ${data.id}")
                openPostChatDetail(data.id, chatName, authorName)
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.d("AlarmActivity", "채팅방 생성 네트워크 오류: ${t.message}")
            }
        })
    }
}
