package de.geek_hub.freezermanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("boot")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_kitchen_white_24px)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // TODO: use setting
                .setContentIntent(activity)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
