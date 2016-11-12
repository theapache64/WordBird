package com.shifz.wordbird;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shifz.wordbird.utils.App;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3500;
    private static final int RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)) {
                    requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE}, RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE}, RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE);
                }

            } else {
                doNormalSplashWork();
            }

        } else {
            doNormalSplashWork();
        }


    }

    private void doNormalSplashWork() {

        ((TextView) findViewById(R.id.tvAppVersion)).setText(String.format("v%s", BuildConfig.VERSION_NAME));

        //Checking if the api key exists
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent nextActivityIntent;

                //TODO : Debug purpose
                //App.apiKey = "test";

                if (App.apiKey == null) {
                    //Moving to INACTIVITY to get an ApiKey
                    nextActivityIntent = new Intent(SplashActivity.this, InActivity.class);
                } else {
                    //Moving to Menu activity
                    nextActivityIntent = new Intent(SplashActivity.this, MenuActivity.class);
                }

                startActivity(nextActivityIntent);
            }
        }, SPLASH_DELAY);


    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
