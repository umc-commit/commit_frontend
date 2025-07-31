package com.example.commit.adapter.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.data.model.entities.CommissionListItem

class CommissionListAdapter(private var commissionItems: List<CommissionListItem>) : // var로 변경
    RecyclerView.Adapter<CommissionListAdapter.CommissionItemViewHolder>() {

    class CommissionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val ivCommissionThumbnail: ImageView = itemView.findViewById(R.id.iv_commission_thumbnail)
        val tvCommissionTitle: TextView = itemView.findViewById(R.id.tv_commission_title)
        val tvCommissionDescription: TextView = itemView.findViewById(R.id.tv_commission_description)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommissionItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_commission_list, parent, false)
        return CommissionItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommissionItemViewHolder, position: Int) {
        val item = commissionItems[position]
        holder.tvDate.text = item.getFormattedDate() // 포맷팅된 날짜 사용
        holder.ivCommissionThumbnail.setImageResource(item.thumbnailResId)
        holder.tvCommissionTitle.text = item.title
        holder.tvCommissionDescription.text = item.description
        holder.tvPrice.text = item.getFormattedPrice() // 포맷팅된 가격 사용
        holder.tvStatus.text = item.status

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${item.title} 커미션 클릭됨", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = commissionItems.size

    // 리스트를 업데이트하고 RecyclerView에 변경을 알리는 함수
    fun updateList(newList: List<CommissionListItem>) {
        commissionItems = newList
        notifyDataSetChanged() // 데이터 변경을 어댑터에 알림
    }
}
