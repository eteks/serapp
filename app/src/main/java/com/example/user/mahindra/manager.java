package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class manager extends AppCompatActivity {

    private String vehicle_id;
    private MobileServiceClient mClient;
    EditText etname;
    TextView vehicle_reg_no;
    TextView vehicle_engine_no;
    TextView vehicle_colour_code;
    TextView customer_name;
    TextView customer_mobile;
    TextView customer_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        Intent intent = getIntent();
        vehicle_id = intent.getStringExtra("vehicle");
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
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
        vehicle_reg_no = (TextView) findViewById(R.id.display);
        vehicle_engine_no = (TextView) findViewById(R.id.engine_no);
        vehicle_colour_code = (TextView) findViewById(R.id.color);
        customer_name = (TextView) findViewById(R.id.customer_name);
        customer_mobile = (TextView) findViewById(R.id.customer_contact);
        customer_address = (TextView) findViewById(R.id.customer_address);
        final String[][] temp = new String[1][3];
        final String[][] temp1 = new String[1][3];
        final MobileServiceTable<vehicle> VehicleTable = mClient.getTable("vehicle", vehicle.class);
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        List<vehicle> vehicleData = VehicleTable
                                .where()
                                .field("vehicle_id").eq(vehicle_id)
                                .execute()
                                .get();
                        StringBuilder commaSepValueBuilder = new StringBuilder();
                        //Looping through the list
                        for (int i = 0; i < vehicleData.size(); i++) {
                            commaSepValueBuilder.append(vehicleData.get(i));

                            if (i != vehicleData.size() - 1) {
                                commaSepValueBuilder.append(", ");
                            }
                        }
                        final String Details = commaSepValueBuilder.toString();
                        System.out.println(Details);
                        temp[0] = Details.split(",");
                    } catch (final Exception e) {
                        createAndShowDialog(e, "Error");
                    }
                    final MobileServiceTable<customer> CustomerTable = mClient.getTable("customer", customer.class);
                    try {
                        List<customer> customerData = CustomerTable
                                .where()
                                .field("vehicle_id").eq(vehicle_id)
                                .execute()
                                .get();
                        StringBuilder commaSepValueBuilder = new StringBuilder();
                        //Looping through the list
                        for (int i = 0; i < customerData.size(); i++) {
                            commaSepValueBuilder.append(customerData.get(i));

                            if (i != customerData.size() - 1) {
                                commaSepValueBuilder.append(", ");
                            }
                        }
                        final String Details = commaSepValueBuilder.toString();
                        System.out.println(Details);
                        temp1[0] = Details.split("\\*");
                    } catch (final Exception e) {
                        createAndShowDialog(e, "Error");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //vehicle_reg_no.setText();
                            vehicle_engine_no.setText(temp[0][1]);
                            vehicle_colour_code.setText(temp[0][2]);
                            vehicle_reg_no.setText(temp[0][3]);
                            customer_name.setText(temp1[0][0]);
                            customer_mobile.setText(temp1[0][1]);
                            customer_address.setText(temp1[0][2]);
                        }
                    });

                    return null;
                }
            };

            runAsyncTask(task);

    }

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
}
