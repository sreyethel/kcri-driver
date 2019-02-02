package com.hbidriver.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.hbidriver.app.R;
import com.hbidriver.app.adapter.UserAdapter;
import com.hbidriver.app.callback.UserCallback;
import com.hbidriver.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity implements UserCallback {
    private Activity activity = ChartActivity.this;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private List<User> list;
    private UserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initGUI();
        setData();

    }

    private void initGUI(){
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Chart");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                manager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getData();

        mAdapter = new UserAdapter(list, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void getData(){
        for (int i=0; i<=5; i++){
            User item = new User("Dalin KOY");
            list.add(item);
        }
    }

    @Override
    public void onClickitem(User item, int pos) {
        Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();
    }
}
