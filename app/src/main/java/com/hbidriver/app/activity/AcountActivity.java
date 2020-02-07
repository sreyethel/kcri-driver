package com.hbidriver.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbidriver.app.R;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;

public class AcountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Activity activity=AcountActivity.this;
    private TextView tvUserName, tvLocation;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        initGUI();
        setData();
    }

    private void initGUI(){
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        tvUserName=findViewById(R.id.my_account_user_name);
        tvLocation=findViewById(R.id.my_account_location);
        imageView=findViewById(R.id.my_account_image);

    }
    private void setData(){
        tvUserName.setText(MainActivity.user.getData().getUsername());
        tvLocation.setText(MainActivity.user.getData().getAddress());
        Picasso.with(activity).load(MainActivity.user.getData().getImage()).placeholder(R.drawable.default_user).into(imageView);

    }

    //onBackPressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            NextActivity.goActivity(activity,new EditAccount());
        }

        return super.onOptionsItemSelected(item);
    }
}
