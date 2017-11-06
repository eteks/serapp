package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Context;


import android.view.*;
import android.widget.Button;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.serialization.JsonEntityParser;
import com.squareup.okhttp.OkHttpClient;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.lang.*;
import static com.example.user.mahindra.R.id.clientPassword;
import static com.example.user.mahindra.R.id.clientUsername;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
import static java.util.logging.Level.parse;

//For Push Notification
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.*;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

//    private MobileServiceClient mClient;
    public static MobileServiceClient mClient;
    private EditText clientUsername;
    private EditText clientPassword;
    private EditText clientUsertype;
    public static final String SENDER_ID = "973440221227";
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private String TAG;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private ProgressBar mProgressBar;

//    private String HubEndpoint = null;
//    private String HubSasKeyName = null;
//    private String HubSasKeyValue = null;
//    private GoogleCloudMessaging gcm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.loginprogressBar);
        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        final Spinner clientUsertype =(Spinner) findViewById(R.id.clientUsertype);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        clientUsertype.setAdapter(adapter);
        clientUsertype.setOnItemSelectedListener(this);

        try {
            mClient = new MobileServiceClient("http://servicapp.azurewebsites.net",this).withFilter(new MainActivity.ProgressFilter());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
            @Override
            public OkHttpClient createOkHttpClient() {
                OkHttpClient client = new OkHttpClient();
                client.setReadTimeout(20, TimeUnit.SECONDS);
                client.setWriteTimeout(20, TimeUnit.SECONDS);
                return client;
            }
        });

//        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
//        MobileServiceTable<UsersAuth> mTable = mClient.getTable("user_auth", UsersAuth.class);
//        try {
//            final List<UsersAuth> result =  mTable.where().field("username").eq("admin").execute().get();
//            for (UsersAuth item : result) {
//                System.out.println(result);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //System.out.println(item);
        //mClient.getTable(ToDoItem.class).insert(item.get())
    }


    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected = parent.getItemAtPosition(position).toString();
        final Spinner clientUsertype = (Spinner) findViewById(R.id.clientUsertype);
        clientUsername = (EditText) findViewById(R.id.clientUsername);
        clientPassword = (EditText) findViewById(R.id.clientPassword);
        //clientUsertype = (EditText) findViewById(R.id.clientUsertype);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            int ONE_TIME = 0;
            @Override
            public void onClick(View view) {
                ONE_TIME++;
                if (ONE_TIME == 1) {
                    System.out.println(clientUsername.getText().toString());
                    System.out.println(clientPassword.getText().toString());
                    System.out.println(clientUsertype.getSelectedItem().toString());
                    final UsersAuth user = new UsersAuth();
                    user.username = clientUsername.getText().toString();
//                    user.username = "service";
                    user.password = clientPassword.getText().toString();
//                    user.password = "service";
                    user.usertype = clientUsertype.getSelectedItem().toString();
//                    user.usertype = "Service Agent";
                    final MobileServiceTable<UsersAuth> mTable = mClient.getTable("user_auth", UsersAuth.class);

                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                final List<UsersAuth> result = mTable.where().field("username").eq(val(user.username)).and().field("usertype").eq(val(user.usertype)).select("user_password").execute().get();
                                StringBuilder commaSepValueBuilder = new StringBuilder();

                                //Looping through the list
                                for (int i = 0; i < result.size(); i++) {
                                    commaSepValueBuilder.append(result.get(i));

                                    if (i != result.size() - 1) {
                                        commaSepValueBuilder.append(", ");
                                    }
                                }
                                final String Password = commaSepValueBuilder.toString();
                                System.out.println(Password);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ONE_TIME = 0;
                                        if (Password.equals("")) {
                                            createAndShowDialog("Please check your username", "Wrong");
                                        } else if (user.password.equals(Password)) {
                                            System.out.println("Success Login");
                                            Toast.makeText(MainActivity.this, "Logging in!", Toast.LENGTH_SHORT).show();
//                                            SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
//                                            prefs.edit().putString("username", user.username).commit();

                                            //To store the user data in session

                                            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("usertype", user.usertype);
                                            editor.commit();
                                            NotificationsManager.handleNotifications(MainActivity.this, SENDER_ID, MyHandler.class);
                                            registerWithNotificationHubs();
                                            if(user.usertype.equals("Service Manager")){
                                                Intent intent = new Intent(MainActivity.this, managerdashboard.class);
                                                intent.putExtra("username", user.username);
                                                finish();
                                                startActivity(intent);
                                            }else{
                                                Intent intent = new Intent(MainActivity.this, NewService.class);
                                                intent.putExtra("username", user.username);
                                                finish();
                                                startActivity(intent);
                                            }

                                        } else {
                                            createAndShowDialog("Your password is wrong! Please check it and try again!!!", "Wrong");
                                            System.out.println("Failed");
                                            //showDialogBox();
                                        }
                                    }
                                });


                            } catch (final Exception e) {
                                createAndShowDialogFromTask(e, "Error");
                            }

                            return null;
                        }
                    };

                    runAsyncTask(task);
                }
            }
        });

//        Button notification = (Button) findViewById(R.id.notification);
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                NotificationsManager.handleNotifications(MainActivity.this, SENDER_ID, MyHandler.class);
////                registerWithNotificationHubs();
//
////                EditText notificationText = (EditText) findViewById(R.id.editTextNotificationMessage);
//                String notificationText = "Notification message";
////                final String json = "{\"data\":{\"message\":\"" + notificationText.getText().toString() + "\"}}";
//                final String json = "{\"data\":{\"message\":\"" + notificationText + "\"}}";
//                new Thread() {
//                    public void run() {
//                        try {
//                            // Based on reference documentation...
//                            // http://msdn.microsoft.com/library/azure/dn223273.aspx
//                            ParseConnectionString(NotificationSetting.HubFullAccess);
////                            URL url = new URL(HubEndpoint + NotificationSettings.HubName +
////                                    "/messages/?api-version=2015-01");
//                            URL url = new URL(HubEndpoint + NotificationSetting.HubName +
//                                    "/messages/?api-version=2015-01");
//
//                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//                            try {
//                                // POST request
//                                urlConnection.setDoOutput(true);
//                                urlConnection.setDoOutput(true);
//
//                                // Authenticate the POST request with the SaS token
//                                urlConnection.setRequestProperty("Authorization",
//                                        generateSasToken(url.toString()));
//
//
//                                // Notification format should be GCM
//                                urlConnection.setRequestProperty("ServiceBusNotification-Format", "gcm");
//
//                                // Include any tags
//                                // Example below targets 3 specific tags
//                                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
////                                 urlConnection.setRequestProperty("ServiceBusNotification-Tags",
////                                        "tag1 || tag2 || tag3");
//
//                                urlConnection.setRequestProperty("ServiceBusNotification-Tags",
//                                        "Service Manager");
//
//                                // Send notification message
//                                urlConnection.setFixedLengthStreamingMode(json.length());
//                                OutputStream bodyStream = new BufferedOutputStream(urlConnection.getOutputStream());
////                                System.out.println("bodystream");
////                                System.out.println("bodystream"+bodyStream);
//                                bodyStream.write(json.getBytes());
//                                bodyStream.close();
//
//                                // Get reponse
//                                urlConnection.connect();
//                                int responseCode = urlConnection.getResponseCode();
//                                if ((responseCode != 200) && (responseCode != 201)) {
//                                    BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getErrorStream())));
//                                    String line;
//                                    StringBuilder builder = new StringBuilder("Send Notification returned " +
//                                            responseCode + " : ");
//                                    while ((line = br.readLine()) != null) {
//                                        builder.append(line);
//                                    }
//
////                                    ToastNotify(builder.toString());
//                                }
//                            } finally {
//                                urlConnection.disconnect();
//                            }
//                        } catch (Exception e) {
//                            if (isVisible) {
////                                ToastNotify("Exception Sending Notification : " + e.getMessage().toString());
//                            }
//                        }
//                    }
//                }.start();
//            }
//        });
    }

    public void registerWithNotificationHubs()
    {
        Log.i(TAG, " Registering with Notification Hubs");
//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);
        if (checkPlayServices()) {
            System.out.println("checkPlayServices called");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

//    public void ToastNotify(final String notificationMessage) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
//                TextView helloText = (TextView) findViewById(R.id.text_hello);
//                helloText.setText(notificationMessage);
//            }
//        });
//    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
//                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void showDialogBox(){
        createAndShowDialog("Your password is wrong! Please check it and try again!!!","Wrong");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

}


