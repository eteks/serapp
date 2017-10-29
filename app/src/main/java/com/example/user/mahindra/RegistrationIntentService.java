package com.example.user.mahindra;

/**
 * Created by root on 28/10/17.
 */
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    public static final String MyPREFERENCES = "MyPrefs" ;

    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Onhandleintent called");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(NotificationSetting.SenderId,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.i(TAG, "Got GCM Registration Token: " + token);
            System.out.println("got gcm token");
            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
//            NotificationHub hub = new NotificationHub(NotificationSetting.HubName,
//                    NotificationSetting.HubListenConnectionString, this);
//            System.out.println("unregister process");
//            hub.unregister();
            if ((regID=sharedPreferences.getString("registrationID", null)) == null) {
                NotificationHub hub = new NotificationHub(NotificationSetting.HubName,
                        NotificationSetting.HubListenConnectionString, this);
                Log.i(TAG, "Attempting to register with NH using token : " + token);

//                hub.unregister();

                //get data from session
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String usertype_data = sharedpreferences.getString("usertype",null);
                System.out.println("usertype_data"+usertype_data);

                regID = hub.register(token,usertype_data).getRegistrationId();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1", "tag2").getRegistrationId();

                resultString = "Registered Successfully - RegId : " + regID;
                Log.i(TAG, resultString);
                sharedPreferences.edit().putString("registrationID", regID ).apply();
            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
            }
        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (MainActivity.isVisible) {
//            MainActivity.mainActivity.ToastNotify(resultString);
        }
    }
}
