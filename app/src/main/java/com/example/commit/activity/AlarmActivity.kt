package com.example.commit.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.AlarmAdapter
import com.example.commit.AlarmItem
import com.example.commit.HomeCardAdapter
import com.example.commit.R

class AlarmActivity : AppCompatActivity() {

    private lateinit var settingButton: ImageView
    private var isDeleteMode = false

    private lateinit var backButton: ImageView
    private lateinit var alarmDialog: Dialog
    private lateinit var deleteAllDialog: Dialog

    private val alarmList = mutableListOf<AlarmItem>()
    private lateinit var alarmAdapter: AlarmAdapter

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


        alarmList.addAll(listOf(
            AlarmItem(1, "payment_request", "결제 요청이 도착했어요", "작가님이 결제를 요청했어요.", "방금", false),
            AlarmItem(2, "commission_approved", "커미션 신청서가 수락됐어요", "신청하신 커미션이 수락됐어요.", "5분 전", true),
            AlarmItem(3, "commission_submitted", "커미션 신청 완료!", "커미션 신청이 정상 접수되었습니다.", "10분 전", false)
        ))

        alarmAdapter = AlarmAdapter(alarmList) { position ->
            alarmList.removeAt(position)
            alarmAdapter.notifyItemRemoved(position)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm_list)
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun showAlarmDialog() {
        alarmDialog = Dialog(this)
        alarmDialog.setContentView(R.layout.dialog_alarm)
        alarmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alarmDialog.window?.setDimAmount(0.6f)


        alarmDialog = Dialog(this)
        alarmDialog.setContentView(R.layout.dialog_alarm)
        alarmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alarmDialog.findViewById<TextView>(R.id.tv_delete_all).setOnClickListener {
            alarmDialog.dismiss()
            showDeleteAllDialog()
        }

        alarmDialog.findViewById<TextView>(R.id.tv_delete_selected).setOnClickListener {
            alarmDialog.dismiss()
            enterDeleteMode()
        }

        alarmDialog.findViewById<TextView>(R.id.tv_mark_all_read).setOnClickListener {
            alarmList.forEach { it.isRead = true }
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


        deleteAllDialog = Dialog(this)
        deleteAllDialog.setContentView(R.layout.dialog_alarm_alldelete)
        deleteAllDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
}
