package com.boss.mychatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar=(ProgressBar)findViewById(R.id.progressbar) ;

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                start();
            }
        });
        thread.start();
    }


    private void doWork() {
        for(progress=20;progress<=100;progress+=20)
        {
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private void start() {
        Intent intent=new Intent(SplashScreen.this, Register.class);
        startActivity(intent);
        finish();
    }
}