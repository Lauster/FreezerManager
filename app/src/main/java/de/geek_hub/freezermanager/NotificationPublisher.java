package de.geek_hub.freezermanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ItemList itemList = new ItemList(context);
            itemList.resetLastNotification();
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("expires",
                        context.getString(R.string.notification_channel),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            if (!PreferenceManager.getDefaultSharedPreferences(context).getString("notification_expiration", "21").equals("-1")) {
                Notification notification = intent.getParcelableExtra("notification");
                int notificationId = intent.getIntExtra("notification_id", 0);
                notificationManager.notify(notificationId, notification);
            }

            ItemList itemList = new ItemList(context);
            itemList.showedNotification();
        }
    }
}
