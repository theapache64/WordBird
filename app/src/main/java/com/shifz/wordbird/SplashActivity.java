package com.shifz.wordbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.shifz.wordbird.utils.App;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
