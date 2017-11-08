package com.example.user.mahindra;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.user.mahindra.MainActivity.MyPREFERENCES;


/**
 * Created by ets-prabu on 2/11/17.
 */

public class managerdashboard extends AppCompatActivity {
    private MobileServiceClient mClient;
    String vehicle_id ;
    private MobileServiceTable<vehicle> vehicleTable;
    private vehicleListAdapter vehicleAdapter;
    private ProgressBar mProgressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managerdashboard);
        mProgressBar = (ProgressBar) findViewById(R.id.managerDBprogressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Car Service");
        myToolbar.setTitleTextColor(0xFFFFFFFF);
        //setSupportActionBar(myToolbar);
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(managerdashboard.this, "Safely Logged out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(managerdashboard.this, MainActivity.class);
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                sharedpreferences.edit().remove("usertype").commit();
                finish();
                startActivity(intent);
            }
        });
        TextView test = (TextView)findViewById(R.id.username1);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String username = extras.getString("username");
        test.setText(username);
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://servicapp.azurewebsites.net",
                    this).withFilter(new managerdashboard.ProgressFilter());

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
            vehicleAdapter = new vehicleListAdapter(this, R.layout.vehicle_list);
            ListView vehicleList = (ListView) findViewById(R.id.listViewToDo);
            vehicleList.setAdapter(vehicleAdapter);
            vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    vehicle_id = vehicleListAdapter.vehicle_id[position];
                    Intent intent = new Intent(managerdashboard.this,manager.class);
                    Bundle extras = new Bundle();
                    extras.putString("vehicle",vehicle_id);
                    extras.putString("username",username);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
        getVehicleList();
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

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    public void getVehicleList (){
        vehicleTable = mClient.getTable("vehicle",vehicle.class);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<vehicle> results = vehicleTable.where().execute().get();
                    StringBuilder commaSepValueBuilder = new StringBuilder();
                    //Looping through the list
                    for (int i = 0; i < results.size(); i++) {
                        commaSepValueBuilder.append(results.get(i));

                        if (i != results.size() - 1) {
                            commaSepValueBuilder.append(", ");
                        }
                    }
                    final String vehicle = commaSepValueBuilder.toString();
                    final String[] temp = vehicle.split(",");
                    vehicle_id = temp[0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vehicleAdapter.clear();
                                for (vehicle item : results) {
                                    vehicleAdapter.add(item);
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
