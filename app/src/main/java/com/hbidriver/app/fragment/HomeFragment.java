package com.hbidriver.app.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.hbidriver.app.R;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.activity.AcountActivity;
import com.hbidriver.app.activity.ChartActivity;
import com.hbidriver.app.activity.SaleActivity;
import com.hbidriver.app.activity.SettingActivity;
import com.hbidriver.app.activity.SplashActivity;
import com.hbidriver.app.adapter.HomeAdapter;
import com.hbidriver.app.banner.MainSliderAdapter;
import com.hbidriver.app.banner.PicassoImageLoadingService;
import com.hbidriver.app.callback.HomeCallback;
import com.hbidriver.app.model.Home;
import com.hbidriver.app.model.ResponseOnUpdateLocation;
import com.hbidriver.app.model.SlidesModel;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeCallback , OnMapReadyCallback{
    private View view;
    private Slider slider;
    private RecyclerView recyclerView;
    private List<Home> list;
    private HomeAdapter mAdapter;
    private LinearLayoutManager manager;

    private LocationRequest mLocationRequest;
    //map
    private GoogleMap mMap;
    private long UPDATE_INTERVAL = 20 * 1000;  /* 20 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static final float DEFAULT_ZOOM = 17f;
    private static final float TILT_LEVEL = 17f;

    String[] name={
            "Chart Room",
            "My Account",
            "Setting",

    };
    int[] image={
            R.drawable.ic_circle_color,
            R.drawable.ic_user_color,
            R.drawable.ic_settings_color
    };

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initGUI();
        initMap();
        startLocationUpdates();
        setupBanner();
        setData();

        return view;
    }

    private void initGUI(){
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupBanner(){
        slider = view.findViewById(R.id.banner_slider);
        Slider.init(new PicassoImageLoadingService(getActivity()));

        //delay for testing empty view functionality
        slider.postDelayed(new Runnable() {
            @Override
            public void run() {
                RetrofitClient.getService().getSlides("Bearer "+SplashActivity.adminUser.getToken()).enqueue(new Callback<SlidesModel>() {
                    @Override
                    public void onResponse(Call<SlidesModel> call, Response<SlidesModel> response) {
                        List<SlidesModel.DataEntity> list=new ArrayList<>();
                        list=response.body().getData();
                        slider.setAdapter(new MainSliderAdapter(list));
                        slider.setSelectedSlide(0);
                    }

                    @Override
                    public void onFailure(Call<SlidesModel> call, Throwable t) {

                    }
                });
            }
        }, 1500);
    }

    private void setData(){
        list = new ArrayList<>();
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getData();
        mAdapter = new HomeAdapter(list,this);
        recyclerView.setAdapter(mAdapter);

    }

    private void getData(){
        for(int i = 0; i<name.length; i++){
            list.add(new Home(name[i],image[i]));
        }
    }

    @Override
    public void onItemClick(Home item, int position) {
       if (position==0){
            NextActivity.goActivity(getActivity(), new ChartActivity());
        }else if (position==1){
           NextActivity.goActivity(getActivity(), new AcountActivity());
       }else if (position==2){
           NextActivity.goActivity(getActivity(), new SettingActivity());
       }
    }

    //map
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps

        if (getActivity()!=null) {
            RetrofitClient.getService().updateLocation(SharedPrefManager.getUserData(getActivity()).getUser_id(), location.getLatitude(), location.getLongitude(), "Bearer " + SplashActivity.adminUser.getToken()).enqueue(new Callback<ResponseOnUpdateLocation>() {
                @Override
                public void onResponse(Call<ResponseOnUpdateLocation> call, Response<ResponseOnUpdateLocation> response) {
                    if (response.body() != null) {
                        ResponseOnUpdateLocation result = response.body();
//                    Toast.makeText(getActivity(),result.getLatitude()+" | "+result.getLongitude(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseOnUpdateLocation> call, Throwable t) {
                    Toast.makeText(getActivity(), "No internet connection...", Toast.LENGTH_LONG).show();
                }
            });
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .tilt(TILT_LEVEL)
                .zoom(DEFAULT_ZOOM)
                .build();                  // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(checkPermissions()) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            Toast.makeText(getActivity(), "No Location", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                100);
    }
}
