package com.example.user.mahindra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ServiceDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_dashboard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);
        Button newservices = (Button) findViewById(R.id.re_service);
        newservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDashBoard.this, NewService.class);
                startActivity(i);
            }
        });
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView test = (TextView)findViewById(R.id.username);
        String username = getIntent().getStringExtra("username");
        test.setText(username);
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.logout) {
//            finish();
//        }
//        return true;
//    }


}

