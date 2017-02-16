package de.geek_hub.freezermanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

public class NotificationHandler {

    public static void setNextNotification(Context context, Item item) {
        int notifyBefore = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("notification_expiration", 21);
        long notificationTime = item.getExpDate().getTime() - notifyBefore * 86400000;
        // TODO: choose correct string, calculate days
        long id = item.getFreezeDate().getTime();
        int intId = (int)(id - (long)(Math.floor(id / 1000000000) * 1000000000));
        String message = context.getResources().getQuantityString(R.plurals.notification_expires, 0, item.getName(), 0);
        scheduleNotification(context, notificationTime, intId, message);
    }

    public static void deleteNextNotification() {

    }

    /**
     * @param time in ms when notification shall be shown
     */
    private static void scheduleNotification(Context context, long time, int notificationId, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_kitchen_white_24px)
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
