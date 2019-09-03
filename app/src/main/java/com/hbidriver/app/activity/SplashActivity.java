package com.hbidriver.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.hbidriver.app.R;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.AdminUser;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {
    private Activity activity = SplashActivity.this;
    private static int SPLASH_TIME_OUT = 1000;
    public static AdminUser adminUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                RetrofitClient.getService().logInAdmin("sothearak@gmail.com","12345678").enqueue(new Callback<AdminUser>() {
                    @Override
                    public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                        adminUser=response.body();
                        if(adminUser.getMsg().equals("true")){
                            NextActivity.goActivity(activity, new LoginActivity());
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminUser> call, Throwable t) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("No Internet Connection")
                                .setMessage("Please reconnect to the internet and try again...")
                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NextActivity.goActivityWithClearTasks(activity, new SplashActivity());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(false);
                    }
                });
            }
        }, SPLASH_TIME_OUT);
    }
}
