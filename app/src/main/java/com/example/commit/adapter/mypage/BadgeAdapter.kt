package com.example.commit.adapter.mypage

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient

class BadgeAdapter(
    val badgeList: List<RetrofitClient.UserBadge>,
    private val context: Context,
    private val onPopupRequested: (Dialog) -> Unit
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBadge: ImageView = itemView.findViewById(R.id.iv_badge_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val userBadge = badgeList[position]
        val badge = userBadge.badge.firstOrNull()

        Glide.with(holder.itemView.context)
            .load(badge?.badgeImage ?: R.drawable.ic_profile)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(holder.ivBadge)

        holder.ivBadge.setOnClickListener {
            badge ?: return@setOnClickListener
            val dialog = createBadgeDialog(badge)
            onPopupRequested(dialog)
        }
    }

    override fun getItemCount(): Int = badgeList.size

    private fun createBadgeDialog(badge: RetrofitClient.BadgeDetail): Dialog {  // 배지 단일 객체 받기
        val popupView = LayoutInflater.from(context).inflate(R.layout.badge_popup, null)

        val tvTitle = popupView.findViewById<TextView>(R.id.tv_badge_popup_text)
        val tvCondition = popupView.findViewById<TextView>(R.id.tv_badge_popup_text2)
        val ivBadge = popupView.findViewById<ImageView>(R.id.iv_badge_popup)

        val grade = getGrade(badge.type, badge.threshold)
        val titleText = when (badge.type) {
            "comm_finish" -> "커미션 완료 배지 ($grade)"
            "follow" -> "팔로워 배지 ($grade)"
            "comm_request" -> "커미션 신청 배지 ($grade)"
            "review" -> "후기 작성 배지 ($grade)"
            else -> "가입 1주년 배지"
        }

        val conditionText = when (badge.type) {
            "comm_finish" -> "조건 : 커미션 완료 ${badge.threshold}회 달성"
            "follow" -> "조건 : 팔로워 ${badge.threshold}명 달성"
            "comm_request" -> "조건 : 커미션 신청 ${badge.threshold}회 달성"
            "review" -> "조건 : 후기 작성 ${badge.threshold}회 달성"
            else -> "회원가입 후 1주년 달성"
        }

        tvTitle.text = titleText
        tvCondition.text = conditionText

        Glide.with(context)
            .load(badge.badgeImage)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(ivBadge)

        return Dialog(context).apply {
            setContentView(popupView)
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setDimAmount(0.6f)
                setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.CENTER)
            }
        }
    }

    // 1) 공통 등급 라벨
    private val GRADE_LABELS = listOf("동", "은", "금", "다이아")

    // 2) 타입별 threshold 구간 정의
    private val THRESHOLDS_BY_TYPE: Map<String, List<Int>> = mapOf(
        // 기본(커미션 완료/신청/후기)은 1,5,15,50
        "comm_finish" to listOf(1, 5, 15, 50),
        "comm_request" to listOf(1, 5, 15, 50),
        "review"       to listOf(1, 5, 15, 50),

        // 팔로워만 5,10,20,100
        "follow"       to listOf(5, 10, 20, 100)
    )

    // 3) 타입별 threshold에 맞춰 등급 산출
    private fun getGrade(type: String, threshold: Int): String {
        val thresholds = THRESHOLDS_BY_TYPE[type] ?: emptyList()
        val idx = thresholds.indexOf(threshold)
        return if (idx in GRADE_LABELS.indices) GRADE_LABELS[idx] else ""
    }
}
