package de.geek_hub.freezermanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Date;

class NotificationHandler {

    static void setNextNotification(Context context, Item item, int itemId) {
        int notifyBefore = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString("notification_expiration", "21"));
        long notificationTime = item.getExpDate().getTime() - notifyBefore * 86400000;

        int id = calculateNotificationId(item.getFreezeDate().getTime());

        String message;
        JodaTimeAndroid.init(context);
        long difference = item.getExpDate().getTime() - new Date().getTime();
        int differenceInDays = Math.abs(Days.daysBetween(new LocalDate(), new LocalDate(item.getExpDate().getTime())).getDays());
        if (difference < 0) {
            if (differenceInDays == 0) {
                message = String.format(context.getString(R.string.notification_has_expired_today), item.getName());
            } else {
                message = context.getResources().getQuantityString(R.plurals.notification_has_expired,
                        differenceInDays, item.getName(), differenceInDays);
            }
        } else {
            if (differenceInDays == 0) {
                message = String.format(context.getString(R.string.notification_expires_today), item.getName());
            } else {
                message = context.getResources().getQuantityString(R.plurals.notification_expires,
                        differenceInDays, item.getName(), differenceInDays);
            }
        }

        scheduleNotification(context, notificationTime, id, message, itemId);
    }

    static void deleteNextNotification(Context context, Item item) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
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
    private static void scheduleNotification(Context context, long time, int notificationId, String text, int itemId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("action", "itemDetail");
        intent.putExtra("id", itemId);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, "expires")
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_kitchen_white_24px)
                    .setSound(Uri.parse(sp.getString("notification_expiration_ringtone", "content://settings/system/notification_sound")))
                    .setVibrate(new long[]{0, sp.getBoolean("notification_expiration_vibrate", false) ? 200 : 0})
                    .setContentIntent(activity)
                    .build();
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_kitchen_white_24px)
                    .setSound(Uri.parse(sp.getString("notification_expiration_ringtone", "content://settings/system/notification_sound")))
                    .setVibrate(new long[]{0, sp.getBoolean("notification_expiration_vibrate", false) ? 200 : 0})
                    .setContentIntent(activity)
                    .build();
        } else {
            notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setSound(Uri.parse(sp.getString("notification_expiration_ringtone", "content://settings/system/notification_sound")))
                    .setVibrate(new long[]{0, sp.getBoolean("notification_expiration_vibrate", false) ? 200 : 0})
                    .setContentIntent(activity)
                    .build();
        }

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra("notification_id", notificationId);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}
