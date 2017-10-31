package com.example.user.mahindra;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.example.user.mahindra.MainActivity.MyPREFERENCES;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

public class Complaints extends Activity {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<complaint> mToDoTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
    private ListAdapter mAdapter;
    private String HubEndpoint = null;
    private String HubSasKeyName = null;
    private String HubSasKeyValue = null;
    //    public boolean isVisible;
    public static MainActivity mainActivity;
    public static String vehicle_id;
    public static String vehicle_no;
    public static Boolean isVisible = false;
    private GoogleCloudMessaging gcm;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * EditText containing the "New To Do" text
     */
    // private EditText mTextNewToDo;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaints);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        //setSupportActionBar(myToolbar);
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Complaints.this, MainActivity.class);
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                sharedpreferences.edit().remove("usertype").commit();
                finish();
                startActivity(intent);
            }
        });
        TextView test = (TextView)findViewById(R.id.username1);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String username = extras.getString("username");
        test.setText(username);
        vehicle_id = extras.getString("vehicle_id");
        System.out.println("vehicle_id"+vehicle_id);
        vehicle_no = extras.getString("vehicle_no");
        System.out.println("vehicle_no"+vehicle_no);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://serapp.azurewebsites.net",
                    this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use
            mToDoTable = mClient.getTable(complaint.class);

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            initLocalStore().get();
            // Create an adapter to bind the items with the view
            mAdapter = new ListAdapter(this, R.layout.checkable_list_layout);
            ListView listViewTodo = (ListView) findViewById(R.id.listViewToDo);
            listViewTodo.setAdapter(mAdapter);
            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }

    public void insertItem(int item){
        final vehicle_complaint record = new vehicle_complaint();
        int vehicle = Integer.parseInt(vehicle_id);
        record.setVehicle(vehicle);
        record.setComplaint(item+1);
        addItem(record);
        //System.out.println("item"+item);
        Button submit = (Button) findViewById(R.id.re_service);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    Toast.makeText(Complaints.this, "Complaint Registered!", Toast.LENGTH_SHORT).show();
                    //Code To send Notification
                    String vehicle_det = vehicle_no;
                    System.out.println("vehicle detail in complaint page"+vehicle_det);
                    String notificationText = "New service has been registered for this vehicle number"+vehicle_det;
    //                final String json = "{\"data\":{\"message\":\"" + notificationText.getText().toString() + "\"}}";
                    final String json = "{\"data\":{\"message\":\"" + notificationText.toString() + "\"}}";
//                    final String json = "{\"data\":{\"message\":\"" + notificationText + "\"" + "\"vehicle_no\":\"" + vehicle_det + "\"}}";

                    new Thread() {
                        public void run() {
                            try {
                                // Based on reference documentation...
                                // http://msdn.microsoft.com/library/azure/dn223273.aspx
                                ParseConnectionString(NotificationSetting.HubFullAccess);
    //                            URL url = new URL(HubEndpoint + NotificationSettings.HubName +
    //                                    "/messages/?api-version=2015-01");
                                URL url = new URL(HubEndpoint + NotificationSetting.HubName +
                                        "/messages/?api-version=2015-01");

                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                try {
                                    System.out.println("notification try");
                                    // POST request
                                    urlConnection.setDoOutput(true);
                                    urlConnection.setDoOutput(true);

                                    // Authenticate the POST request with the SaS token
                                    urlConnection.setRequestProperty("Authorization",
                                            generateSasToken(url.toString()));


                                    // Notification format should be GCM
                                    urlConnection.setRequestProperty("ServiceBusNotification-Format", "gcm");

                                    // Include any tags
                                    // Example below targets 3 specific tags
                                    // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
    //                                 urlConnection.setRequestProperty("ServiceBusNotification-Tags",
    //                                        "tag1 || tag2 || tag3");

                                    urlConnection.setRequestProperty("ServiceBusNotification-Tags",
                                            "servicemanager".toString());

                                    System.out.println("before start sending message1");
                                    // Send notification message
                                    urlConnection.setFixedLengthStreamingMode(json.length());
                                    System.out.println("before start sending message2");
                                    OutputStream bodyStream = new BufferedOutputStream(urlConnection.getOutputStream());
    //                                System.out.println("bodystream");
    //                                System.out.println("bodystream"+bodyStream);
                                    System.out.println("before start sending message3");
                                    bodyStream.write(json.getBytes());
                                    System.out.println("before start sending message4");
                                    bodyStream.close();
                                    // Get reponse
                                    urlConnection.connect();
                                    int responseCode = urlConnection.getResponseCode();
                                    if ((responseCode != 200) && (responseCode != 201)) {
                                        BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getErrorStream())));
                                        String line;
                                        StringBuilder builder = new StringBuilder("Send Notification returned " +
                                                responseCode + " : ");
                                        while ((line = br.readLine()) != null) {
                                            builder.append(line);
                                        }
    //                                    Toast.makeText(Complaints.this, "Notification sent to service manager", Toast.LENGTH_LONG).show();
    //                                    ToastNotify(builder.toString())
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Complaints.this, "Notification sent to service manager", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } finally {
                                    urlConnection.disconnect();
                                }
                            } catch (Exception e) {
                                if (isVisible) {
    //                                ToastNotify("Exception Sending Notification : " + e.getMessage().toString());
                                }
                            }
                        }
                    }.start();
            }
        });
    }

    public void deleteItem(int item) {
        int vehicle = Integer.parseInt(vehicle_id);
        item++;
        deleteRecord(vehicle,item);
    }


    public void deleteRecord(final int vehicle_id, final int complaint_id){
        final MobileServiceTable<vehicle_complaint> FaultTable = mClient.getTable("vehicle_complaint", vehicle_complaint.class);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<vehicle_complaint> result = mClient.getTable("vehicle_complaint", vehicle_complaint.class).where().field("vehicle_id").eq(val(vehicle_id)).and().field("complaint_id").eq(val(complaint_id)).select("id").execute().get();
                    System.out.println("vehicle_id:"+result);
                    StringBuilder commaSepValueBuilder = new StringBuilder();
                    //Looping through the list
                    for (int i = 0; i < result.size(); i++) {
                        commaSepValueBuilder.append(result.get(i));

                        if (i != result.size() - 1) {
                            commaSepValueBuilder.append(", ");
                        }
                    }

                    final String id = commaSepValueBuilder.toString();
                    System.out.println("vehicle_id:"+id);
                    mClient.getTable("vehicle_complaint", vehicle_complaint.class).delete(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Record deleted");
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

    public void addItem(final vehicle_complaint record){
        final MobileServiceTable<vehicle_complaint> FaultTable = mClient.getTable("vehicle_complaint", vehicle_complaint.class);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final vehicle_complaint entity = mClient.getTable("vehicle_complaint", vehicle_complaint.class).insert(record).get();
                    System.out.println();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                System.out.println("Record inserted");
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

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     // * @param item
     *            The item to mark
     */
//    public void checkItemInTable(complaint item) throws ExecutionException, InterruptedException {
//        mToDoTable.update(item).get();
//    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<complaint> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (complaint item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<complaint> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mToDoTable.select("complaint_name","complaint_id").execute().get();
    }


    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
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
            System.out.println("token"+token);
        } catch (Exception e) {
            if (isVisible) {
//                ToastNotify("Exception Generating SaS : " + e.getMessage().toString());
            }
        }

        return token;
    }

    private class ProgressFilter implements ServiceFilter {

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

    public String getVehicle(){
        return vehicle_id;
    }

}