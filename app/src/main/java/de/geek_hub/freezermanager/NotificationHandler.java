package de.geek_hub.freezermanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationHandler {

    public static void setNextNotification(Context context, Item item) {
        int notifyBefore = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString("notification_expiration", "21"));
        long notificationTime = item.getExpDate().getTime() - notifyBefore * 86400000;

        int id = calculateNotificationId(item.getFreezeDate().getTime());

        String message;
        long difference = item.getExpDate().getTime() - new Date().getTime();
        long differenceInDays = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        int differenceInDaysInt = (int) Math.abs(Math.max(Math.min(Integer.MAX_VALUE, differenceInDays), Integer.MIN_VALUE));
        if (difference < 0) {
            if (difference > -86400000) {
                message = String.format(context.getString(R.string.notification_has_expired_today), item.getName());
            } else {
                message = context.getResources().getQuantityString(R.plurals.notification_has_expired,
                        differenceInDaysInt, item.getName(), differenceInDaysInt);
            }
        } else {
            if (difference < 86400000) {
                message = String.format(context.getString(R.string.notification_expires_today), item.getName());
            } else {
                message = context.getResources().getQuantityString(R.plurals.notification_expires,
                        differenceInDaysInt, item.getName(), differenceInDaysInt);
            }
        }

        scheduleNotification(context, notificationTime, id, message);
    }

    public static void deleteNextNotification(Context context, Item item) {
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        int notificationId = calculateNotificationId(item.getFreezeDate().getTime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static int calculateNotificationId(long id) {
        return (int)(id - (long)(Math.floor(id / 1000000000) * 1000000000));
    }

    /**
     * @param time in ms when notification shall be shown
     */
    private static void scheduleNotification(Context context, long time, int notificationId, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}
