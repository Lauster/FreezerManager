package de.geek_hub.freezermanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;

public class NotificationHandler {

    public void setNextNotification() {

    }

    /**
     * @param time in ms when notification shall be shown
     */
    public void scheduleNotification(Context context, long time, int notificationId, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_kitchen_white_24px)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // TODO: use setting
                .setContentIntent(activity)
                .build();

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("notification_id", notificationId);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}
