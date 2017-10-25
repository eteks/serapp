package com.example.user.mahindra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import android.view.*;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MobileServiceClient mClient;
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


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        try {
            mClient = new MobileServiceClient("https://mahindra.azurewebsites.net",this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final ToDoItem item = new ToDoItem();
        item.Text = "Awesome item";
        try {
            ToDoItem entity = mClient.getTable(ToDoItem.class).insert(item).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //System.out.println(item);
        //mClient.getTable(ToDoItem.class).insert(item.get())
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected=parent.getItemAtPosition(position).toString();
        Toast.makeText(this,sSelected,Toast.LENGTH_SHORT).show();

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this,ServiceDashBoard.class);
                startActivity(i);
            }
        });

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
