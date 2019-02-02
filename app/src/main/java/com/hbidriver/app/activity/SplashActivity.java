package com.hbidriver.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.hbidriver.app.R;
import com.hbidriver.app.utils.NextActivity;

public class SplashActivity extends AppCompatActivity {
    private Activity activity = SplashActivity.this;
    private static int SPLASH_TIME_OUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                NextActivity.goActivity(activity, new LoginActivity());
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
