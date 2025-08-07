package com.example.commit.activity.alarm

import android.app.Dialog
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
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import retrofit2.Call
import retrofit2.Response

class AlarmActivity : AppCompatActivity() {

    private lateinit var settingButton: ImageView
    private var isDeleteMode = false

    private lateinit var backButton: ImageView
    private lateinit var alarmDialog: Dialog
    private lateinit var deleteAllDialog: Dialog

    private val alarmList = mutableListOf<RetrofitClient.NotificationItem>()
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

        alarmAdapter = AlarmAdapter(alarmList) { position ->
            alarmList.removeAt(position)
            alarmAdapter.notifyItemRemoved(position)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_alarm_list)
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // API 호출
        loadNotifications()

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
}
