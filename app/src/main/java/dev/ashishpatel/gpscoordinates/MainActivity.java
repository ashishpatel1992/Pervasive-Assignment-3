package dev.ashishpatel.gpscoordinates;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String TAG = "main: logLocation";
    Button btn_getGPS;
    LocationManager locationManager;
    TextView loc_text;
    ComponentName service;
    Intent intentMyService;
    String GPS_FILTER = "gpscoordinates.action.GPS_LOCATION";
    GPSBroadcastReceiver receiver;

    /**
     * Creates a GPS Service that runs in background
     */
    public void GPSService() {
        /**
         * TODO 2: Display "Getting location coordinates" in the main Activity while the coordinates are being fetched after pressing the button (2 marks)
         */
        loc_text.setText("Getting location coordinates...\nStay outdoors for better GPS signal");
        btn_getGPS.setEnabled(false);
        Log.i(TAG, "GPSService: ");
        intentMyService = new Intent(this, UserLocationService.class);
        /**
         * TODO 3: After the button press, the main activity starts a service (4 marks)
         */
        service = startService(intentMyService);

        IntentFilter intentFilter = new IntentFilter(GPS_FILTER);

        receiver = new GPSBroadcastReceiver();
        registerReceiver(receiver, intentFilter);

        Toast.makeText(getApplicationContext(), "GPS Service Started", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "GPSService END ");
    }


    private class GPSBroadcastReceiver extends BroadcastReceiver {
        /**
         * Receives GPS coordinates from the service and updates in Main UI
         */
        @Override
        public void onReceive(Context context, Intent userLocation) {
            Log.i(TAG, "onReceive: ");
            double lat = userLocation.getDoubleExtra("lat", -1);
            double lng = userLocation.getDoubleExtra("lng", -1);
            String msg = "Latitude: " + lat + ", Longitude: " + lng;
            Log.i(TAG, msg);
            /**
             * TODO 6: The main activity displays the coordinates (2 marks)
             */
            loc_text.setText(msg);
            btn_getGPS.setEnabled(true);

        }
    }

    /**
     * Creates a Alert Dialog asking the user to turn on the GPS
     *
     * @return true if GPS is turned On
     */
    public boolean enableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Please turn on your GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getGPS = findViewById(R.id.btn_get_gps);
        loc_text = findViewById(R.id.txt_loc_text);
        /**
         * Ask for GPS Permission from user
         */
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        /*
        TODO 1:  In the main activity, there is a button to get current GPS coordinates (2 marks)
         */
        btn_getGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ");
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                /**
                 * Check if GPS permission is not granted
                 */
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    loc_text.setText("Please turn on your GPS");
                    /**
                     * Turn on GPS as it is disabled
                     */
                    if (enableGPS()) {
                        loc_text.setText("Press Locate Me to get the location\nStay outdoors for better GPS signal");
                    }

                } else {
                    GPSService();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            stopService(intentMyService);
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.i(TAG, "onDestroy: Main");
        }

    }


}