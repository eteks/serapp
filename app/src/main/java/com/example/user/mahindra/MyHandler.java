package com.example.user.mahindra;

/**
 * Created by ets-2 on 26/10/17.
 */
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    @Override
    public void onRegistered(Context context,  final String gcmRegistrationId) {
        System.out.println("GCMID");
        System.out.println(gcmRegistrationId);
        super.onRegistered(context, gcmRegistrationId);
//        MainActivity.mClient.getPush().register(gcmRegistrationId);
        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                try {
                    System.out.println("enter to register");
                    MainActivity.mClient.getPush().register(gcmRegistrationId);
                    System.out.println("after register");
                    return null;
                }
                catch(Exception e) {
                    // handle error
                    System.out.println(e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        System.out.println("Entered into onreceive function");
        String msg = bundle.getString("message");

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, // requestCode
                new Intent(context, MainActivity.class),
                0); // flags

        Notification notification = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Hub Demo")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
