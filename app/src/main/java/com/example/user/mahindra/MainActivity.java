package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;


import android.view.*;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.user.mahindra.R.id.clientPassword;
import static com.example.user.mahindra.R.id.clientUsername;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
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
    private String HubEndpoint = null;
    private String HubSasKeyName = null;
    private String HubSasKeyValue = null;
//    public boolean isVisible;
    private String TAG;
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private GoogleCloudMessaging gcm;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //private MobileServiceTable mTable;

//    ToDoItem item = new ToDoItem();
//    item.Text = "Awesome item";
//        mClient.getTable(ToDoItem.class).insert(item, new TableOperationCallback<item>() {
//        public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
//            if (exception == null) {
//                // Insert succeeded
//                System.out.println('Insert success');
//            } else {
//                // Insert failed
//                System.out.println("Insert failed");
//            }
//        }
//    })

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            mClient = new MobileServiceClient("http://serapp.azurewebsites.net",this);
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
        String sSelected=parent.getItemAtPosition(position).toString();
//        final Spinner clientUsertype =(Spinner) findViewById(R.id.clientUsertype);
//        clientUsername = (EditText) findViewById(R.id.clientUsername);
//        clientPassword = (EditText) findViewById(R.id.clientPassword);
        //clientUsertype = (EditText) findViewById(R.id.clientUsertype);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
//                System.out.println(clientUsername.getText().toString());
//                System.out.println(clientPassword.getText().toString());
//                System.out.println(clientUsertype.getSelectedItem().toString());
//                final UsersAuth user = new UsersAuth();
//                //user.username = clientUsername.getText().toString();
//                user.username = "admin";
//                user.password = clientPassword.getText().toString();
//                //user.usertype = clientUsertype.getSelectedItem().toString();
//                user.usertype = "Service Agent";
//                final MobileServiceTable<UsersAuth> mTable = mClient.getTable("user_auth", UsersAuth.class);
//
//                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            final List<UsersAuth> result = getDataFromDB();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    for (UsersAuth item : result) {
//                                        System.out.println(result);
//                                    }
//                                }
//                            });
//                        } catch (final Exception e) {
//                            createAndShowDialogFromTask(e, "Error");
//                        }
//
//                        return null;
//                    }
//                };
//
//                runAsyncTask(task);


                //Intent i = new Intent(MainActivity.this,ServiceDashBoard.class);
                //startActivity(i);
                MobileServiceTable<vehicle> mTable = mClient.getTable("vehicle", vehicle.class);
                final vehicle item = new vehicle();
                item.reg_no = "PY 01 C 1234";
                item.eng_no = "BSRT1234N";
                item.col_code = "Blue";
////        mClient.getTable(vehicle.class).insert(item, new TableOperationCallback<item>() {
////            public void onCompleted(vehicle entity, Exception exception, ServiceFilterResponse response) {
////                if (exception == null) {
////                    // Insert succeeded
////                } else {
////                    // Insert failed
////                }
////            }
////        });
                try {
                    vehicle entity = mClient.getTable(vehicle.class).insert(item).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        Button notification = (Button) findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                NotificationsManager.handleNotifications(MainActivity.this, SENDER_ID, MyHandler.class);
                registerWithNotificationHubs();

//                EditText notificationText = (EditText) findViewById(R.id.editTextNotificationMessage);
                String notificationText = "Notification message";
//                final String json = "{\"data\":{\"message\":\"" + notificationText.getText().toString() + "\"}}";
                final String json = "{\"data\":{\"message\":\"" + notificationText + "\"}}";
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            // Based on reference documentation...
                            // http://msdn.microsoft.com/library/azure/dn223273.aspx
                            ParseConnectionString(NotificationSetting.HubFullAccess);
//                            URL url = new URL(HubEndpoint + NotificationSettings.HubName +
//                                    "/messages/?api-version=2015-01");
                            URL url = new URL(HubEndpoint + NotificationSetting.HubName +
                                    "/messages/?api-version=2015-01");

                            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                            try {
                                // POST request
                                urlConnection.setDoOutput(true);

                                // Authenticate the POST request with the SaS token
                                urlConnection.setRequestProperty("Authorization",
                                        generateSasToken(url.toString()));

                                // Notification format should be GCM
                                urlConnection.setRequestProperty("ServiceBusNotification-Format", "gcm");

                                // Include any tags
                                // Example below targets 3 specific tags
                                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                                // urlConnection.setRequestProperty("ServiceBusNotification-Tags",
                                //        "tag1 || tag2 || tag3");

                                // Send notification message
                                urlConnection.setFixedLengthStreamingMode(json.length());
                                OutputStream bodyStream = new BufferedOutputStream(urlConnection.getOutputStream());
                                bodyStream.write(json.getBytes());
                                bodyStream.close();

                                // Get reponse
                                urlConnection.connect();
                                int responseCode = urlConnection.getResponseCode();
                                if ((responseCode != 200) && (responseCode != 201)) {
                                    BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getErrorStream())));
                                    String line;
                                    StringBuilder builder = new StringBuilder("Send Notification returned " +
                                            responseCode + " : ")  ;
                                    while ((line = br.readLine()) != null) {
                                        builder.append(line);
                                    }

//                                    ToastNotify(builder.toString());
                                }
                            } finally {
                                urlConnection.disconnect();
                            }
                        }
                        catch(Exception e)
                        {
                            if (isVisible) {
//                                ToastNotify("Exception Sending Notification : " + e.getMessage().toString());
                            }
                        }
                    }
                }.start();
            }
        });
    }

    public void registerWithNotificationHubs()
    {
        Log.i(TAG, " Registering with Notification Hubs");

        if (checkPlayServices()) {
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

    /**
     * Example code from http://msdn.microsoft.com/library/azure/dn495627.aspx
     * to parse the connection string so a SaS authentication token can be
     * constructed.
     *
     * @param connectionString This must be the DefaultFullSharedAccess connection
     *                         string for this example.
     */
    private void ParseConnectionString(String connectionString)
    {
        String[] parts = connectionString.split(";");
        if (parts.length != 3)
            throw new RuntimeException("Error parsing connection string: "
                    + connectionString);

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("Endpoint")) {
                this.HubEndpoint = "https" + parts[i].substring(11);
            } else if (parts[i].startsWith("SharedAccessKeyName")) {
                this.HubSasKeyName = parts[i].substring(20);
            } else if (parts[i].startsWith("SharedAccessKey")) {
                this.HubSasKeyValue = parts[i].substring(16);
            }
        }
    }

    /**
     * Example code from http://msdn.microsoft.com/library/azure/dn495627.aspx to
     * construct a SaS token from the access key to authenticate a request.
     *
     * @param uri The unencoded resource URI string for this operation. The resource
     *            URI is the full URI of the Service Bus resource to which access is
     *            claimed. For example,
     *            "http://<namespace>.servicebus.windows.net/<hubName>"
     */
    private String generateSasToken(String uri) {

        String targetUri;
        String token = null;
        try {
            targetUri = URLEncoder
                    .encode(uri.toString().toLowerCase(), "UTF-8")
                    .toLowerCase();

            long expiresOnDate = System.currentTimeMillis();
            int expiresInMins = 60; // 1 hour
            expiresOnDate += expiresInMins * 60 * 1000;
            long expires = expiresOnDate / 1000;
            String toSign = targetUri + "\n" + expires;

            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = HubSasKeyValue.getBytes("UTF-8");
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(toSign.getBytes("UTF-8"));

            // Using android.util.Base64 for Android Studio instead of
            // Apache commons codec
            String signature = URLEncoder.encode(
                    Base64.encodeToString(rawHmac, Base64.NO_WRAP).toString(), "UTF-8");

            // Construct authorization string
            token = "SharedAccessSignature sr=" + targetUri + "&sig="
                    + signature + "&se=" + expires + "&skn=" + HubSasKeyName;
        } catch (Exception e) {
            if (isVisible) {
//                ToastNotify("Exception Generating SaS : " + e.getMessage().toString());
            }
        }

        return token;
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

//    private List<UsersAuth> getDataFromDB() throws ExecutionException, InterruptedException
//    {
//        String table = "complaint_id";
//        String user = "admin";
//        final MobileServiceTable<UsersAuth> mTable = mClient.getTable("complaint", UsersAuth.class);
//        return mTable.where().field(table).eq(1).execute().get();
//    }
//    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            return task.execute();
//        }
//    }


}


