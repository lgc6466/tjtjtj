package com.example.todoido;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelName = intent.getStringExtra("channel_name");
        String channelDescription = intent.getStringExtra("channel_description");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);

                // 알림 생성
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.logo)  // 알림 아이콘 설정
                        .setContentTitle(channelName)  // 알림 제목 설정
                        .setContentText(channelDescription)  // 알림 내용 설정
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);  // 알림 우선순위 설정

                // 알림 표시
                notificationManager.notify(0, builder.build());
            }
        }
    }
}