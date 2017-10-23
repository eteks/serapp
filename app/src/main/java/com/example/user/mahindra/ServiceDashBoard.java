package com.example.user.mahindra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;

public class ServiceDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_dashboard);
        Button newservices = (Button) findViewById(R.id.newservice);
        newservices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(ServiceDashBoard.this,NewService.class);
                startActivity(i);
            }
        });
    }
}

