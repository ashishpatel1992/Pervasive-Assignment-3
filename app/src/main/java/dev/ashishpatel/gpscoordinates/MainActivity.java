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

//    public void getGPSCoordinates(){
//       if(
//               ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//               ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//       ){
//           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//        }else{
//           LocationListener locationListener = new UserLocationService().new UserLocationListener(LocationManager.GPS_PROVIDER);
////           locationListener.onLocationChanged();
//
//           startService(new Intent(this,UserLocationService.class));
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);
////           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//           Location location =null;
////           locationListener.onLocationChanged(location);
////           location = locationManager.
//           if(location != null){
//               double lat = location.getLatitude();
//               double lng = location.getLongitude();
//               loc_text.setText(lat+" "+lng);
//           }else{
//               Toast.makeText(this,"Unable to find location",Toast.LENGTH_SHORT).show();
//           }
//       }
//
//    }

    GPSBroadcastReceiver receiver;
    public void GPSService(){
        loc_text.setText("Getting GPS");
        Log.i(TAG, "GPSService: ");
        intentMyService = new Intent(this,UserLocationService.class);

        service = startService(intentMyService);

        IntentFilter intentFilter = new IntentFilter(GPS_FILTER);
//        intentFilter.addAction("UserLocation");

        receiver = new GPSBroadcastReceiver();
        registerReceiver(receiver,intentFilter);
        Toast.makeText(getApplicationContext(),"GPS Service Started",Toast.LENGTH_SHORT).show();

        Log.i(TAG, "GPSService END ");
//        stopService(new Intent(intentMyService));
    }
    private class GPSBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent userLocation) {
            Log.i(TAG, "onReceive: ");
            double lat = userLocation.getDoubleExtra("lat",-1);
            double lng = userLocation.getDoubleExtra("lng",-1);
            String msg = "Latitude: "+lat+", Longitude: "+lng;
            Log.i(TAG, msg);
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            loc_text.setText(msg);

//            stopService(locationIntent);
        }

    }
    public boolean enableGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
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
        //final Activity thisActivity = this;
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        btn_getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: ");
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    loc_text.setText("Please turn on your GPS");
                    if(enableGPS()){
                        loc_text.setText("GPS is Turned On. Click to Fetch");
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
        try{
            stopService(intentMyService);
            unregisterReceiver(receiver);
//            unregisterReceiver(re);
        }catch (Exception e){
            Log.i(TAG, "onDestroy: Main");
        }

    }


}