package com.andrewkoloskov.northlord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrewkoloskov.northlord.LoginWorker.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }
    @Override
    public void onStart(){
        super.onStart();

        try{
           final Intent intent=new Intent(this,LoginActivity.class);
           Thread timer=new Thread(){
               public void run(){

                   try {
                       sleep(1500);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                   finally {
                       startActivity(intent);
                       finish();
                   }
               }
           };
           timer.start();

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
