package com.example.user.mahindra;

/**
 * Created by ets-2 on 26/10/17.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.microsoft.windowsazure.notifications.NotificationsHandler;


public class MyHandler extends NotificationsHandler {
    public static manager manager;
    public static Complaints complaint;
    public static final int NOTIFICATION_ID = 1;
    @Override
    public void onRegistered(Context context,  final String gcmRegistrationId) {
//        System.out.println();
        System.out.println("GCMID"+gcmRegistrationId);
        System.out.println(context);
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
        System.out.println("Entered into received function");
//        String msg = bundle.getString("message");
//        final String vehicle_no = bundle.getString("vehicle_no");
//        System.out.println("vehicle detail in notification page"+vehicle_no);
        String vehicle_no = Complaints.vehicle_no;
        System.out.println("Vehicle NO :"+vehicle_no);
        String msg = "New service has been registered for this vehicle number "+vehicle_no;
        System.out.println(msg);
 //       String msg = "New service has been registered for this vehicle number";
        String vehicle = Complaints.vehicle_id;
        System.out.println("vehicle ID:"+vehicle);
        Intent intent = new Intent(context, manager.class);
        intent.putExtra("vehicle",vehicle);
//        Intent intent = new Intent(context,manager.class);
//        Bundle extras = new Bundle();
//        extras.putString("vehicle_id",vehicle_id);
//        System.out.println(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack
        stackBuilder.addParentStack(manager.class);
// Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(intent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);




//        PendingIntent contentIntent = PendingIntent.getActivity(context,
//                0, // requestCode
////                new Intent(context, MainActivity.class),
//                intent,
//                0); // flags

        Notification notification = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Vehicle Service Registration Status")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg.toString())
                .setContentIntent(resultPendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        System.out.println("notification_id"+notification);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

}

