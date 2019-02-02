package com.hbidriver.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbidriver.app.R;
import com.hbidriver.app.utils.NextActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Activity activity = LoginActivity.this;
    private EditText edEmail, edPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initGUI();
        initEvent();

    }

    private void initGUI(){
        edEmail = findViewById(R.id.ed_email);
        edPass = findViewById(R.id.ed_pass);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void initEvent(){
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                NextActivity.goActivity(activity, new MainActivity());
                break;
                default:
        }
    }
}
