package com.hbidriver.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.hbidriver.app.R;
import com.hbidriver.app.Services.RestClient;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.fragment.HomeFragment;
import com.hbidriver.app.model.UserFromGetProfileModel;
import com.hbidriver.app.service.LocationService;
import com.hbidriver.app.utils.DialogManager;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity = MainActivity.this;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navHeader;
    private TextView txtName, txtWebsite;
    private ImageView imageView;
    public static UserFromGetProfileModel user;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initGUI();
        initEvent();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("HBI Import Export");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.textView);
        imageView = navHeader.findViewById(R.id.imageView);

        // load nav menu header data
        //loadNavHeader();
        loadDriverProfile();
        showUpdate();

    }

    private void initGUI() {

    }

    private void initEvent() {
        setFragment(new HomeFragment());
    }

    private void loadNavHeader() {
        // name, website
        RetrofitClient.getService().getUserProfile(SharedPrefManager.getUserData(activity).getUser_id(), "Bearer " + SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
            @Override
            public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                if (response.body() != null) {
                    user = response.body();
                    txtName.setText(user.getData().getUsername());
                    txtWebsite.setText(user.getData().getEmail());
                    Picasso.with(activity).load(user.getData().getImage()).placeholder(R.drawable.ic_person_black_24dp).into(imageView);
                }
            }

            @Override
            public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Note")
                        .setMessage("No internet connection")
                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NextActivity.goActivityWithClearTasks(activity, new MainActivity());
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });


    }

    private void loadDriverProfile() {
        int userId = SharedPrefManager.getUserData(activity).getUser_id();
        String token = "Bearer " + SharedPrefManager.getUserData(activity).getToken();
        RestClient.getServiceV2().getDriverProfile(userId, token).enqueue(new Callback<UserFromGetProfileModel>() {
            @Override
            public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                if (response.body() != null) {
                    user = response.body();
                    txtName.setText(user.getData().getUsername());
                    txtWebsite.setText(user.getData().getEmail());
                    Picasso.with(activity).load(user.getData().getImage()).placeholder(R.drawable.ic_person_black_24dp).into(imageView);
                }
            }

            @Override
            public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                showErrorDialog();
            }
        });
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Note")
                .setMessage("No internet connection")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NextActivity.goActivityWithClearTasks(activity, new MainActivity());
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            DialogManager.exDialog(activity, "Exit", "Do you want to close the APP?");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            NextActivity.goActivity(activity, new NotificationActivity());
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            setFragment(new HomeFragment());
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_chart) {
            NextActivity.goActivity(activity, new ChartActivity());
        } else if (id == R.id.nav_account) {
            NextActivity.goActivity(activity, new SaleActivity());
        } else if (id == R.id.nav_setting) {
            NextActivity.goActivity(activity, new SettingActivity());
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Note")
                    .setMessage("Are you sure you want to log out from app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPrefManager.setLogin(activity, false);
                            finish();
                            NextActivity.goActivityWithClearTasks(activity, new LoginActivity());
                        }
                    })
                    .setNegativeButton("No", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);
        }

        return true;
    }

    public boolean setFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showUpdate() {
        new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        if(!isUpdateAvailable) {
                            return;
                        }
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("Update available")
                                .setMessage("Check out the latest version available of this app")
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(false);
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d(TAG, "onFailed: " + error.toString());
                    }
                }).start();
    }
}
