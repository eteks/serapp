package com.example.user.mahindra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ServiceStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_status);
        Button newservices = (Button) findViewById(R.id.re_service);
        newservices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(ServiceStatus.this,QualityDasboard.class);
                startActivity(i);
            }
        });
    }
}
