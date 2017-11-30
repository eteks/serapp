package com.example.user.mahindra;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.okhttp.OkHttpClient;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.user.mahindra.MainActivity.MyPREFERENCES;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class NewService extends AppCompatActivity {
    private Button b;
    EditText etname;
    TextView vehicle_reg_no;
    TextView vehicle_engine_no;
    TextView vehicle_colour_code;
    TextView customer_name;
    TextView customer_mobile;
    TextView customer_address;
    private MobileServiceTable mTable;
    private MobileServiceClient mClient;
    private ProgressBar mProgressBar;
    int vehicle_id;
    String vehicle_reg ;
    boolean flag = false;
    private vehicleListAdapter vehicleAdapter;
    private MobileServiceTable<vehicle> vehicleTable;
    AutoCompleteTextView vehicleno;
    MultiAutoCompleteTextView vehiclelist;
    String vehicles[] = {"1","2","3","4","5","6","7","8","9","10","11","12","13"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newservice);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(ProgressBar.GONE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Car Service");
        myToolbar.setTitleTextColor(0xFFFFFFFF);
        Intent intent = getIntent();
        vehicleno = (AutoCompleteTextView) findViewById(R.id.vehicleNo);
        Button newservices = (Button) findViewById(R.id.next);
        newservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etname = (EditText) findViewById(R.id.vehicleNo);
                final String vehicle_no = etname.getText().toString();
                if(flag) {
                   Intent intent = new Intent(NewService.this, Complaints.class);
                   String username = getIntent().getStringExtra("username");
                   Bundle extras = new Bundle();
                   extras.putString("username", username);
                   extras.putString("vehicle_id", String.valueOf(vehicle_id));
                   extras.putString("vehicle_no", vehicle_reg);
                   intent.putExtras(extras);
                   System.out.println(vehicle_id);
                   startActivity(intent);
                }
                else{
                    createAndShowDialog("Please enter valid Vehicle Number", "Error");
                }
            }
        });
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewService.this, "Safely Logged out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NewService.this, MainActivity.class);
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                sharedpreferences.edit().remove("usertype").commit();
                finish();
                startActivity(intent);
            }
        });
        TextView test = (TextView)findViewById(R.id.username1);
        String username = getIntent().getStringExtra("username");
        test.setText(username);
//        lv = (ListView) findViewById(R.id.lv);
//        lv.setAdapter(new ArrayAdapter<String>(NewService.this, android.R.layout.simple_expandable_list_item_1, names));
        try {
            mClient = new MobileServiceClient("http://servicapp.azurewebsites.net", this).withFilter(new ProgressFilter());
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
        getVehicleList();
        vehicleno = (AutoCompleteTextView)findViewById(R.id.vehicleNo);
        vehiclelist =(MultiAutoCompleteTextView)findViewById(R.id.vehicleList);
        b = (Button) findViewById(R.id.btn_search);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etname = (EditText) findViewById(R.id.vehicleNo);
                vehicle_reg_no = (TextView) findViewById(R.id.display);
                vehicle_engine_no = (TextView) findViewById(R.id.engine_no);
                vehicle_colour_code = (TextView) findViewById(R.id.color);
                customer_name = (TextView) findViewById(R.id.customer_name);
                customer_mobile = (TextView) findViewById(R.id.customer_contact);
                customer_address = (TextView) findViewById(R.id.customer_address);
                final String vehicle_no = etname.getText().toString();
                System.out.println(vehicle_no);
                final MobileServiceTable<vehicle> VehicleTable = mClient.getTable("vehicle", vehicle.class);
                if(vehicle_no.equals("")){
                    createAndShowDialog("Please enter your Vehicle Number", "Invalid Number");
                }else {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                List<vehicle> vehicleData = VehicleTable
                                        .where()
                                        .field("vehicle_reg_no").eq(vehicle_no)
                                        .execute()
                                        .get();
                                System.out.println(vehicleData);
                                StringBuilder commaSepValueBuilder = new StringBuilder();
                                //Looping through the list
                                for (int i = 0; i < vehicleData.size(); i++) {
                                    commaSepValueBuilder.append(vehicleData.get(i));

                                    if (i != vehicleData.size() - 1) {
                                        commaSepValueBuilder.append(", ");
                                    }
                                }
                                final String Details = commaSepValueBuilder.toString();
                                System.out.println("Details: "+Details);
                                final String[] temp = Details.split(",");
                                        if (Details.equals("")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    createAndShowDialog("Please check your Vehicle Number", "Invalid Number");
                                                }
                                            });
                                        }else{
                                            vehicle_id = Integer.parseInt(temp[0]);
                                            final MobileServiceTable<customer> CustomerTable = mClient.getTable("customer", customer.class);
                                            List<customer> result = null;
                                            try {
                                                result = CustomerTable
                                                        .where()
                                                        .field("vehicle_id").eq(vehicle_id)
                                                        .execute()
                                                        .get();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }
                                            //Looping through the list
                                            StringBuilder CustomerValues = new StringBuilder();
                                            for (int i = 0; i < result.size(); i++) {
                                                System.out.println(result);
                                                CustomerValues.append(result.get(i));
                                                if (i != result.size() - 1) {
                                                    CustomerValues.append(",");
                                                }
                                            }
                                            String CustomerSeparatedValues = CustomerValues.toString();
                                            System.out.println(CustomerSeparatedValues);
                                            final String[] temp1 = CustomerSeparatedValues.split("\\*");
                                            System.out.println(temp1[0]);
                                            System.out.println(temp1[1]);
                                            System.out.println(temp1[2]);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    flag = true;
                                                    vehicle_reg = temp[3];
                                                    vehicle_reg_no.setText(temp[3]);
                                                    vehicle_engine_no.setText(temp[1]);
                                                    vehicle_colour_code.setText(temp[2]);
                                                    customer_name.setText(temp1[0]);
                                                    customer_mobile.setText(temp1[1]);
                                                    customer_address.setText(temp1[2]);
                                                }
                                            });
                                        }

                            } catch (final Exception e) {
                                createAndShowDialog(e, "Error");
                            }
                            return null;
                        }
                    };

                    runAsyncTask(task);
                }
            }

            ;


        });

    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
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

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
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

    public void getVehicleList (){
        vehicleTable = mClient.getTable("vehicle",vehicle.class);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<vehicle> results = vehicleTable.where().field("deleted").eq(val("false")).select("vehicle_reg_no").execute().get();
                    StringBuilder commaSepValueBuilder = new StringBuilder();
                    //Looping through the list
                    for (int i = 0; i < results.size(); i++) {
                        commaSepValueBuilder.append(results.get(i));

                        if (i != results.size() - 1) {
                            commaSepValueBuilder.append(",");
                        }
                    }
                    final String vehicle = commaSepValueBuilder.toString();
                    final String[] temp = vehicle.split(",");
                    int in =0;
                    for(int index = 3; index < temp.length; index+=4){
//                        System.out.println("Vehicle "+temp[index]);
                        vehicles[in] = temp[index];
                        in++;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vehicleList();
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

    public void vehicleList(){
//        for(int i = 0; i < vehicles.length; i++)
//            System.out.println(vehicles[i]);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,vehicles);
        vehicleno.setAdapter(adapter);
        vehicleno.setThreshold(1);
        vehiclelist.setAdapter(adapter);
        vehiclelist.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
}
