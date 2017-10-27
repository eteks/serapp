package com.example.user.mahindra;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MobileServiceClient mClient;
    private EditText clientUsername;
    private EditText clientPassword;
    private EditText clientUsertype;

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
        final Spinner clientUsertype =(Spinner) findViewById(R.id.clientUsertype);
        clientUsername = (EditText) findViewById(R.id.clientUsername);
        clientPassword = (EditText) findViewById(R.id.clientPassword);
        //clientUsertype = (EditText) findViewById(R.id.clientUsertype);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
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
                    //user.username = "admin";
                    user.password = clientPassword.getText().toString();
                    //user.password = "password";
                    user.usertype = clientUsertype.getSelectedItem().toString();
                    //user.usertype = "Quality Agent";
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
                                        if(Password.equals("")){
                                            createAndShowDialog("Please check your username", "Wrong");
                                        }
                                        else if (user.password.equals(Password)) {
                                            System.out.println("Success Login");
                                            Toast.makeText(MainActivity.this, "Logging in!", Toast.LENGTH_SHORT).show();
//                                            SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
//                                            prefs.edit().putString("username", user.username).commit();
                                            Intent intent = new Intent(MainActivity.this, ServiceDashBoard.class);
                                            intent.putExtra("username", user.username);
                                            startActivity(intent);
                                        }

                                        else {
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


}


