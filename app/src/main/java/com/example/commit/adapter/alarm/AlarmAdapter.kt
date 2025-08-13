package com.example.commit.adapter.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.databinding.ItemAlarmBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlarmAdapter(
    private val alarmList: MutableList<RetrofitClient.NotificationItem>,
    private val onItemDelete: (Int) -> Unit,
    private val onOpenChat: (RetrofitClient.NotificationItem) -> Unit,
    private val onOpenPostDetail: (Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private var isDeleteMode = false

    class AlarmViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun getItemCount(): Int = alarmList.size

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = alarmList[position]

        // 기본 알림 내용 세팅
        holder.binding.tvHeader.text = item.title
        holder.binding.tvContent.text = item.content
        holder.binding.tvTime.text = formatTime(item.createdAt)

        val actionText = when (item.type) {
            "commission_submitted" -> ""
            "commission_approved" -> "신청서 확인하러 가기 >"
            "commission_rejected" -> "신청서 확인하러 가기 >"
            "payment_request" -> "결제하러 가기 >"
            "work_started" -> ""
            "work_completed" -> "작업물 확인하러 가기 >"
            "artist_new_post" -> "새 글 확인하러 가기 >"

            else -> ""
        }
        holder.binding.tvAction.text = actionText
        holder.binding.tvAction.visibility = if (actionText.isEmpty()) View.GONE else View.VISIBLE

        // actionText 클릭 → 타입별 이동
        holder.binding.tvAction.setOnClickListener {
            when (item.type) {
                "payment_request", "work_completed" -> {
                    onOpenChat(item) // 채팅방(채팅 탭)으로
                }
                "artist_new_post" -> {
                    // relatedData에서 commissionId 안전 파싱
                    val commissionId = anyToInt(item.relatedData["commissionId"])
                    commissionId?.let { onOpenPostDetail(it) }
                }
            }
        }

        // 아이콘 type별 변경
        val iconRes = when (item.type) {
            "commission_submitted" -> R.drawable.ic_commission_submitted
            "commission_approved" -> R.drawable.ic_commission_approved
            "commission_rejected" -> R.drawable.ic_commission_rejected
            "payment_request" -> R.drawable.ic_payment
            "work_started" -> R.drawable.ic_work_started
            "work_completed" -> R.drawable.ic_work_completed
            "artist_new_post" -> 0

            else -> R.drawable.ic_work_completed
        }
        // 아이콘 설정
        if (iconRes != 0) holder.binding.ivIcon.setImageResource(iconRes)
        else holder.binding.ivIcon.setImageDrawable(null)

        // 읽지 않음 dot 표시
        holder.binding.ivUnreadDot.visibility = if (item.isRead) View.GONE else View.VISIBLE

        // 선택 삭제 모드일 때 체크박스 보임
        holder.binding.ivSelectMode.visibility = if (isDeleteMode) View.VISIBLE else View.GONE

        // 카드 클릭 리스너 예시
        holder.itemView.setOnClickListener {
            if (!isDeleteMode) {

                holder.binding.ivUnreadDot.visibility = View.GONE
            } else {
                // 선택 삭제 모드일 때는 선택 동작만
                holder.binding.ivUnreadDot.visibility = View.GONE
            }
        }

        holder.binding.ivSelectMode.setOnClickListener {
            onItemDelete(holder.adapterPosition)
        }

    }

    fun enableDeleteMode(enabled: Boolean) {
        isDeleteMode = enabled
        notifyDataSetChanged()
    }

    private fun formatTime(isoTime: String): String {
        return try {
            // 1. ISO8601 파서 준비
            val date = sdf.parse(isoTime) ?: return isoTime

            // 2. 현재 시각과의 차이 계산
            val now = Date()
            val diffMillis = now.time - date.time

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

            // 3. 상대 시간 문자열 생성
            when {
                minutes < 1 -> "방금"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                days == 1L -> "어제"
                days < 7 -> "${days}일 전"
                else -> outputFormat.format(date)
            }
        } catch (e: Exception) {
            isoTime // 파싱 실패 시 원본 반환
        }
    }

    companion object {
        private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        private val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    }

    private fun anyToInt(v: Any?): Int? = when (v) {
        is Number -> v.toInt()
        is String -> v.toIntOrNull()
        else -> null
    }
}

