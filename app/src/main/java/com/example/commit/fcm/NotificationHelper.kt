package com.example.commit.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.commit.R
import com.example.commit.activity.MainActivity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object NotificationHelper {
    const val CHANNEL_ID_GENERAL = "commit_general"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID_GENERAL) == null) {
                val ch = NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "일반 알림",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "앱 일반 알림 채널"
                    enableLights(true)
                    lightColor = Color.CYAN
                    enableVibration(true)
                }
                nm.createNotificationChannel(ch)
            }
        }
    }

    fun show(
        context: Context,
        title: String,
        body: String,
        extras: Map<String, String> = emptyMap(),
        notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    ) {
        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            // FCM 알림 클릭임을 식별할 플래그
            putExtra("fromPush", true)
            // 라우팅용 데이터 전달
            extras.forEach { (k, v) -> putExtra(k, v) }
        }

        val pi = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 작은 상단 배너 - 본문을 미리 짧게 자르고(대략 1~2줄 분량), 시스템이 줄바꿈/말줄임 처리
        val trimmedBody = body
            .replace("\n", " ")
            .let { txt ->
                val max = 80   // 기기/글꼴에 따라 1~2줄 정도로 보이는 안전 길이
                if (txt.length > max) txt.take(max - 1) + "…" else txt
            }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title.ifBlank { "알림" })
            .setContentText(trimmedBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setOnlyAlertOnce(true)

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } else {
            // 권한이 없는 경우: 로그만 찍거나, 사용자에게 권한 필요 안내
            android.util.Log.w("NotificationHelper", "알림 권한 없음, notify() 실행 안 함")
        }
    }
}

