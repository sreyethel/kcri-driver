package com.hbidriver.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hbidriver.app.R;
import com.hbidriver.app.adapter.NotificationAdapter;
import com.hbidriver.app.model.Notifi;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private Activity activity = NotificationActivity.this;
    private Toolbar toolbar;
    private List<Notifi> list;
    private NotificationAdapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initGUI();
        setData();
    }

    private void initGUI(){
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notification");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    //onBackPressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setData(){
        recyclerView = findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getData();

        mAdapter = new NotificationAdapter(list);
        recyclerView.setAdapter(mAdapter);
    }

    private void getData(){
        for (int i=0; i<=10; i++){
            Notifi item = new Notifi("Dalin KOY","Phnom Penh");
            list.add(item);
        }

    }
}
