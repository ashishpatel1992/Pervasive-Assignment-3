package dev.ashishpatel.gpscoordinates;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String tag_location = "logLocation";
    Button btn_getGPS;
    LocationManager locationManager;
    TextView loc_text;


    public void getGPSCoordinates(Activity activity){
       if(
               ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
       ){
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if(location != null){
               double lat = location.getLatitude();
               double lng = location.getLongitude();
               loc_text.setText(lat+" "+lng);
           }else{
               Toast.makeText(this,"Unable to find location",Toast.LENGTH_SHORT).show();
           }
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
        final Activity thisActivity = this;
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        btn_getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    loc_text.setText("Please turn on your GPS");
                    if(enableGPS()){
                        loc_text.setText("GPS is Turned On. Click to Fetch");
                    }

                } else {
                    getGPSCoordinates(thisActivity);
//                    UserLocationService uls = new UserLocationService();
//                    uls.onStartCommand(,1,1);
                }

            }
        });
    }
}