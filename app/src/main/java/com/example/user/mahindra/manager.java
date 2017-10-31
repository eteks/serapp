package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class manager extends AppCompatActivity {

    private String vehicle_id;
    private MobileServiceClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        vehicle_id = extras.getString("vehicle_id");
        System.out.println("vehicle NO"+vehicle_id);
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
                    //System.out.println(Details);
                    final String[] temp = Details.split(",");
                }
                catch (final Exception e) {
                    createAndShowDialog(e, "Error");
                }
                return null;

            }
        };

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
}
