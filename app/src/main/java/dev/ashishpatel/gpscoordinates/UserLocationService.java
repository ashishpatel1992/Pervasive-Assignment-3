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

public class UserLocationService extends Service {
    private LocationManager locationManager;
    private Thread userLocationThread;
    private GPSListener myLocationListener;
    private final String TAG = "ULS: locationTag";
    String GPS_FILTER = "gpscoordinates.action.GPS_LOCATION";

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged Called");

            if (location != null) {
                try {
                    locationManager.removeUpdates(this);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to remove locationListener", e);
                }

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                Intent userLocation = new Intent(GPS_FILTER);
                userLocation.putExtra("lat", lat);
                userLocation.putExtra("lng", lng);
                Log.i(TAG, "onLocationChanged: Lat: " + lat + ", Lng: " + lng);
                /**
                 * TODO 5: When the callback function is called, it sends the coordinates to the main activity via a broadcast message (4 marks)
                 */
                sendBroadcast(userLocation);
            }

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i(TAG, "onProviderEnabled Called");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled Called");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onStatusChanged Called");
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
        /**
         * TODO 4: The service starts a new thread that registers a callback function to receive the coordinates (4 marks)
         */
        userLocationThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    /**
                     * Run the thread in loop to process the location requests
                     */
                    Looper.prepare();
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    myLocationListener = new GPSListener();
                    /**
                     * Verify if GPS permission is granted
                     */
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "No Permission ");
                        return;
                    } else {
                        Log.i(TAG, "permission Granted");
                    }
                    long minTime = 1000;
                    float minDistance = 0;
                    /**
                     * Request for location from GPS hardware
                     */
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
                    Looper.loop();

                } catch (Exception e) {
                    Log.e(TAG, "ThreadRun: ", e);
                }

            }
        });
        /**
         * Start the service as separate thread
         */
        userLocationThread.start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
