package com.hbidriver.app.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hbidriver.app.R;
import com.hbidriver.app.utils.NextActivity;

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Activity activity=SettingActivity.this;
    private TextView txtChangPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initGUI();
    }

    private void initGUI(){
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txtChangPassword=findViewById(R.id.change_password);
        txtChangPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextActivity.goActivity(activity,new ChangePassword());
            }
        });
    }

    //onBackPressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
