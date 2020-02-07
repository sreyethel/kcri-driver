package com.hbidriver.app.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hbidriver.app.R;
import com.hbidriver.app.Services.RestClient;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.ResponseOnUpdateLocation;
import com.hbidriver.app.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {

    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    private static final int LOCATION_INTERVAL = 10 * 1000;
    private static final int LOCATION_DISTANCE = 10;
    private static final int NOTIFICATION_ID = 1;

    private static final int ONLINE_STATUS = 1;
    private static final int OFFLINE_STATUS = 0;

    private final LocationServiceBinder binder = new LocationServiceBinder();

    private HandlerThread handlerThread;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private int status;

    private class LocationListener implements android.location.LocationListener {

        private static final String TAG = "LocationListener";
        private Location mLastLocation;

        private LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(final Location location) {
            mLastLocation = location;
            Log.d(TAG, "Lat ::: " + mLastLocation.getLatitude() + " long ::: " + mLastLocation.getLongitude());

            status = OFFLINE_STATUS;
            if (isAppOnForeground(getApplicationContext())) {
                status = ONLINE_STATUS;
            }

            new Handler(handlerThread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    setDataToApi(location, status);
                }
            });

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private void initLocationManager() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    private void startTracking() {
        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListener
            );
        } catch (SecurityException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        final String channelID = getString(R.string.app_name);
        long when = System.currentTimeMillis();

        NotificationChannel channel = new NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), channelID).setAutoCancel(true);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setWhen(when);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onCreate() {
        super.onCreate();

        handlerThread = new HandlerThread("location_upload_thread");
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationService::lock");
        wl.acquire();

        initLocationManager();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, getNotification());
        }

        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }

        startTracking();

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemove");
        super.onTaskRemoved(rootIntent);
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), LocationService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    private void setDataToApi(Location location, int status) {


        int userId = SharedPrefManager.getUserData(getApplicationContext()).getUser_id();
        final String token = "Bearer " + SharedPrefManager.getUserData(getApplicationContext()).getToken();

        Log.d(TAG,"token ::: "+ token);

        RestClient.getServiceV2().updateLocation(
                userId,
                location.getLatitude(),
                location.getLongitude(),
                status,
                token)
                .enqueue(new Callback<ResponseOnUpdateLocation>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseOnUpdateLocation> call,
                                           @NonNull Response<ResponseOnUpdateLocation> response) {
                        if (response.body() != null) {
                            Log.d(TAG, response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseOnUpdateLocation> call, @NonNull Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = "com.hbidriver.app";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                //                Log.e("app",appPackageName);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
        mLocationManager.removeUpdates(mLocationListener);
        wl.release();
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

}
