package com.hbidriver.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hbidriver.app.R;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.AdminUser;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity activity = LoginActivity.this;
    private EditText edEmail, edPass;
    private Button btnLogin;
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getLogin(activity)) {
            NextActivity.goActivityWithClearTasks(activity, new MainActivity());
            overridePendingTransition(0, 0);
        }

        initGUI();
        initEvent();

    }

    private void initGUI() {
        edEmail = findViewById(R.id.ed_email);
        edPass = findViewById(R.id.ed_pass);
        btnLogin = findViewById(R.id.btn_login);
        spotsDialog = new SpotsDialog(activity, R.style.Custom);
    }

    private void initEvent() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (!edEmail.getText().toString().equals("") && !edPass.getText().toString().equals("")) {
                    spotsDialog.show();
                    RetrofitClient.getService().logInAdmin(edEmail.getText().toString(), edPass.getText().toString()).enqueue(new Callback<AdminUser>() {
                        @Override
                        public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                            AdminUser user = response.body();
                            spotsDialog.hide();
                            if (user != null) {
                                if (user.isStatus() && "driver".equalsIgnoreCase(user.getRole()) ) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);

                                    SharedPrefManager.setUserData(LoginActivity.this, json);
                                    SharedPrefManager.setLogin(LoginActivity.this, true);

                                    NextActivity.goActivityWithClearTasks(activity, new MainActivity());
                                } else {
                                    new AlertDialog.Builder(activity).setTitle("Note")
                                            .setMessage(user.getMsg())
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            } else {
                                new AlertDialog.Builder(activity).setTitle("Note")
                                        .setMessage("Incorrect email or password")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminUser> call, Throwable t) {
                            spotsDialog.hide();
                            Toast.makeText(activity, "No internet connection...", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (edEmail.getText().toString().equals("") || edPass.getText().toString().equals("")) {
                    if (edEmail.getText().toString().equals("")) {
                        edEmail.setError("username is required");
                    }
                    if (edPass.getText().toString().equals("")) {
                        edPass.setError("password is required");
                    }
                }
                break;
            default:
        }
    }
}
