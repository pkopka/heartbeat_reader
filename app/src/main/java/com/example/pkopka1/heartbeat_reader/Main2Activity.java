package com.example.pkopka1.heartbeat_reader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        String bmp2 = intent.getStringExtra("bpm"); //if it's a string you stored.
        Log.d("beats", "bmp: " + bmp2);
        TextView result = (TextView)findViewById(R.id.textView3);
        result.setText("Rytm Twojego serca : "+bmp2+" bmp ");

        Button yourButton = (Button) findViewById(R.id.button);

        yourButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
            }
        });

        Button btn1 = (Button) findViewById(R.id.button2);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
                System.exit(0);;
            }
        });


    }
}
