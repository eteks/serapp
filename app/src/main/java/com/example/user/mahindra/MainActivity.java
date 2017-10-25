package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MobileServiceClient mClient;
    private EditText clientUsername;
    private EditText clientPassword;
    private EditText clientUsertype;
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
                item.eng_no = "BSRT4568N";
                item.col_code = "Grey";
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


