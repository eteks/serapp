package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


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
    int vehicle_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newservice);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        Button newservices = (Button) findViewById(R.id.next);
        newservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewService.this, Complaints.class);
                String username = getIntent().getStringExtra("username");
                Bundle extras = new Bundle();
                extras.putString("username", username);
                extras.putString("vehicle_id",String.valueOf(vehicle_id));
                extras.putString("vehicle_no",String.valueOf(etname.getText().toString().toUpperCase()));
                intent.putExtras(extras);
                System.out.println(vehicle_id);
                startActivity(intent);
            }
        });
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewService.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
        TextView test = (TextView)findViewById(R.id.username1);
        String username = getIntent().getStringExtra("username");
        test.setText(username);
//        lv = (ListView) findViewById(R.id.lv);
//        lv.setAdapter(new ArrayAdapter<String>(NewService.this, android.R.layout.simple_expandable_list_item_1, names));
        try {
            mClient = new MobileServiceClient("http://serapp.azurewebsites.net", this);
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
                                                    vehicle_reg_no.setText(etname.getText().toString().toUpperCase());
                                                    vehicle_engine_no.setText(temp[1]);
                                                    vehicle_colour_code.setText(temp[2]);
                                                    customer_name.setText(temp1[0]);
                                                    customer_mobile.setText(temp1[1]);
                                                    customer_address.setText(temp1[2]);
                                                }
                                            });
                                        }

                            } catch (final Exception e) {
                                createAndShowDialogFromTask(e, "Error");
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


}
