package com.example.user.mahindra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
