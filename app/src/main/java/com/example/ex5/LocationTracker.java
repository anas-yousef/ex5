package com.example.ex5;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationTracker extends AppCompatActivity {
    LocationManager locationManager;
    Context context;
    private LocationInfo locationInfo;
    Context currentContext;
    public LocationListener locationListener;



    public LocationTracker(Context context) {
        this.context = context;
        this.currentContext = this;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.locationInfo = new LocationInfo(0,0,0);
    }

    @SuppressLint("MissingPermission")
    public void startTracking() {
        if (this.locationManager != null) {
            LocationTracker.this.locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    LocationTracker.this.locationInfo = new LocationInfo(100.0,longitude, latitude);
                    Intent intent = new Intent("Something Changed");
                    intent.putExtra("longitude", LocationTracker.this.locationInfo.getLongitude());
                    intent.putExtra("latitude", LocationTracker.this.locationInfo.getLatitude());
                    LocationTracker.this.context.sendBroadcast(intent);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 10, locationListener);

//            Intent intent = new Intent("Something Changed");
//            intent.putExtra("longitude", LocationTracker.this.locationInfo.getLongitude());
//            intent.putExtra("latitude", LocationTracker.this.locationInfo.getLatitude());
//            LocationTracker.this.context.sendBroadcast(intent);
        }
        else{
            //Right some Error
        }
    }

    public void stopTracking()
    {
        Intent intent = new Intent("Stop Tracking");
        this.locationManager.removeUpdates(this.locationListener);
        this.locationListener = null;
        LocationTracker.this.context.sendBroadcast(intent);
    }
}
