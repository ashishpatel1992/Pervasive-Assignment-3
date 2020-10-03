package dev.ashishpatel.gpscoordinates;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

// TODO 1: https://stackoverflow.com/questions/8828639/get-gps-location-via-a-service-in-android
// TODO 2: https://github.com/codepath/android_guides/issues/220
public class UserLocationService extends Service {
    private final String TAG = "ULS: locationTag";
    String GPS_FILTER = "gpscoordinates.action.GPS_LOCATION";
//    String GPS_FILTER ="dev.ashishpatel.gpscoordinates"
    private LocationManager locationManager;

    Thread userLocationThread;
    GPSListener myLocationListener;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public UserLocationService(){
        Log.i(TAG, "UserLocationService: ");
    }

    class GPSListener implements LocationListener {
//        Location lastLoc;


        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged Called");
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Intent userLocation = new Intent(GPS_FILTER);
            userLocation.putExtra("lat",lat);
            userLocation.putExtra("lng",lng);
            Log.i(TAG, "onLocationChanged: Lat: "+lat+", Lng: "+lng );
            sendBroadcast(userLocation);

            //Save the last location
//            lastLoc = location;
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e(TAG, "onProviderEnabled Called");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e(TAG, "onProviderDisabled Called");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e(TAG, "onStatusChanged Called");
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand: ");
        userLocationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Looper.prepare();


                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    myLocationListener = new GPSListener();
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                        Log.i(TAG, "No Permission ");//                        return;
                    }else{
                        Log.i(TAG, "permission Granted");
                    }
                    long minTime = 0;
                    float minDistance = 0;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance, myLocationListener);

//                    Location lx = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    Log.i(TAG, "longitude: "+lx.getLongitude());
//                    Log.i(TAG, "Latitude: "+lx.getLatitude());

                    Looper.loop();

                }catch (Exception e){
                    Log.e(TAG, "ThreadRun: ",e );
                }
            }
        });
        userLocationThread.start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
//        initializeLocationManager();
//        try {
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    locationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
//        try {
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    locationListeners[0]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

//        if (locationManager != null) {
//            for (int i = 0; i < locationListeners.length; i++) {
//                try {
//                    locationManager.removeUpdates(locationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }
//            }
//        }
    }
}
