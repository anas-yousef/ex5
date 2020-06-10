package com.example.ex5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private Button trackingButton;
    private LocationTracker locationTracker;
    private Receiver receiver;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private String info = "";

    class Receiver extends BroadcastReceiver {


        public Receiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals("Something Changed"))
            {
                MainActivity.this.info = "longitude: " + Double.toString(intent.getDoubleExtra("longitude", 0f));
                MainActivity.this.info += " latitude: " + Double.toString(intent.getDoubleExtra("latitude", 0f));
                Toast toast=Toast.makeText(getApplicationContext(), MainActivity.this.info,Toast.LENGTH_SHORT);
                    toast.show();

            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.trackingButton = findViewById(R.id.buttonTrackPoition);
        final Activity activity = this;
        this.locationTracker = new LocationTracker(activity);
        this.receiver = new Receiver();
        registerReceiver(this.receiver, new IntentFilter("Something Changed"));

        this.trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasLocationPermission =
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED;


                if (hasLocationPermission) {
                    MainActivity.this.locationTracker.startTracking();
                    MainActivity.this.trackingButton.setText("Stop Tracking");
                } else {

                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        // we know we asked for only 1 permission, so we will surely get exactly 1 result
        // (grantResults.size == 1)
        // depending on your use case, if you get only SOME of your permissions
        // (but not all of them), you can act accordingly

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MainActivity.this.locationTracker.startTracking(); // cool
            MainActivity.this.trackingButton.setText("Stop Tracking");
        } else {
            // the user has denied our request! =-O

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // reached here? means we asked the user for this permission more than once,
                // and they still refuse. This would be a good time to open up a dialog
                // explaining why we need this permission
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

}