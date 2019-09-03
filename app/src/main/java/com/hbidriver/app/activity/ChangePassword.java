package com.hbidriver.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hbidriver.app.R;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.ResponseOnChangePassword;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    private Toolbar toolbar;
    private Activity activity= ChangePassword.this;
    private EditText edNewPassword, edConfirmPassword;
    private Button changePassword;
    private String new_password, confirm_password;
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initGUI();
        setData();
    }
    private void initGUI(){
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        spotsDialog=new SpotsDialog(activity, R.style.Custom);
        edNewPassword=findViewById(R.id.new_password);
        edConfirmPassword=findViewById(R.id.confirm_password);
        changePassword=findViewById(R.id.btn_change_password);
    }
    private void setData(){
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_password=edNewPassword.getText().toString();
                confirm_password=edConfirmPassword.getText().toString();
                if(!new_password.equals("") && !confirm_password.equals("")){
                    if(new_password.equals(confirm_password)){
                        spotsDialog.show();
                        RetrofitClient.getService().changePassword(SharedPrefManager.getUserData(activity).getUser_id(),new_password,"Bearer "+SplashActivity.adminUser.getToken()).enqueue(new Callback<ResponseOnChangePassword>() {
                            @Override
                            public void onResponse(Call<ResponseOnChangePassword> call, Response<ResponseOnChangePassword> response) {
                                spotsDialog.hide();
                                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                                builder.setTitle("Note")
                                        .setMessage(response.body().getData())
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        });
                                AlertDialog alertDialog=builder.create();
                                alertDialog.show();
                                alertDialog.setCanceledOnTouchOutside(false);
                            }

                            @Override
                            public void onFailure(Call<ResponseOnChangePassword> call, Throwable t) {
                                spotsDialog.hide();
                            }
                        });
                    }else {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("Note")
                                .setMessage("Password do not match!")
                                .setPositiveButton("OK", null);
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(false);
                    }
                }else{
                    if (new_password.equals("")){
                        edNewPassword.setError("input new password");
                    }
                    if(confirm_password.equals("")){
                        edConfirmPassword.setError("input confirm password");
                    }
                }
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
