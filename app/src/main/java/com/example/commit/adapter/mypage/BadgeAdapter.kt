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

        Glide.with(holder.itemView.context)
            .load(userBadge.badge.badgeImage)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(holder.ivBadge)

        holder.ivBadge.setOnClickListener {
            val dialog = createBadgeDialog(userBadge)
            onPopupRequested(dialog)
        }
    }

    override fun getItemCount(): Int = badgeList.size

    private fun createBadgeDialog(userBadge: RetrofitClient.UserBadge): Dialog {
        val popupView = LayoutInflater.from(context).inflate(R.layout.badge_popup, null)


        val tvTitle = popupView.findViewById<TextView>(R.id.tv_badge_popup_text)
        val tvCondition = popupView.findViewById<TextView>(R.id.tv_badge_popup_text2)
        val ivBadge = popupView.findViewById<ImageView>(R.id.iv_badge_popup)

        fun getGrade(threshold: Int): String = when (threshold) {
            1 -> "동"
            5 -> "은"
            15 -> "금"
            50 -> "다이아"
            else -> ""
        }

        val grade = getGrade(userBadge.badge.threshold)
        val titleText = when (userBadge.badge.type) {
            "comm_finish" -> "커미션 완료 배지 ($grade)"
            "follow" -> "팔로워 배지 ($grade)"
            "comm_request" -> "커미션 신청 배지 ($grade)"
            "review" -> "후기 작성 배지 ($grade)"
            else -> "가입 1주년 배지"
        }

        val conditionText = when (userBadge.badge.type) {
            "comm_finish" -> "조건 : 커미션 완료 ${userBadge.badge.threshold}회 달성"
            "follow" -> "조건 : 팔로워 ${userBadge.badge.threshold}명 달성"
            "comm_request" -> "조건 : 커미션 신청 ${userBadge.badge.threshold}회 달성"
            "review" -> "조건 : 후기 작성 ${userBadge.badge.threshold}회 달성"
            else -> "회원가입 후 1주년 달성"
        }

        tvTitle.text = titleText
        tvCondition.text = conditionText

        Glide.with(context)
            .load(userBadge.badge.badgeImage)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(ivBadge)

        return Dialog(context).apply {
            setContentView(popupView)
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setDimAmount(0.6f)
                setLayout(
                    (context.resources.displayMetrics.widthPixels - (92 * context.resources.displayMetrics.density).toInt() * 2),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }
        }
    }
}
