package com.example.commit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemAlarmBinding

class AlarmAdapter(
    private val alarmList: List<AlarmItem>,
    private val onItemDelete: (Int) -> Unit
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
        holder.binding.tvTime.text = item.time

        val actionText = when (item.type) {
            "commission_submitted" -> ""
            "commission_approved" -> "신청서 확인하러 가기 >"
            "commission_rejected" -> "신청서 확인하러 가기 >"
            "payment_request" -> "결제하러 가기 >"
            "work_started" -> ""
            "work_completed" -> "작업물 확인하러 가기 >"

            else -> ""
        }
        holder.binding.tvAction.text = actionText
        holder.binding.tvAction.visibility = if (actionText.isEmpty()) View.GONE else View.VISIBLE

        // 아이콘 type별 변경
        val iconRes = when (item.type) {
            "commission_submitted" -> R.drawable.ic_commission_submitted
            "commission_approved" -> R.drawable.ic_commission_approved
            "commission_rejected" -> R.drawable.ic_commission_rejected
            "payment_request" -> R.drawable.ic_payment
            "work_started" -> R.drawable.ic_work_started
            "work_completed" -> R.drawable.ic_work_completed

            else -> R.drawable.ic_payment
        }

        holder.binding.ivIcon.setImageResource(iconRes)

        // 읽지 않음 dot 표시
        holder.binding.ivUnreadDot.visibility = if (item.isRead) View.GONE else View.VISIBLE

        // 선택 삭제 모드일 때 체크박스 보임
        holder.binding.ivSelectMode.visibility = if (isDeleteMode) View.VISIBLE else View.GONE

        // 삭제 체크박스 클릭 리스너 (추가 선택 기능 가능)
        holder.binding.ivSelectMode.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${item.title} 선택됨", Toast.LENGTH_SHORT).show()
        }

        // 카드 클릭 리스너 예시
        holder.itemView.setOnClickListener {
            if (!isDeleteMode) {
                Toast.makeText(holder.itemView.context, "${item.title} 클릭됨", Toast.LENGTH_SHORT).show()

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
}

