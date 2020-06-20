package com.example.ex5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private Button trackingButton;
    private Button showHomeLocation;
    private Button clearHomeLocation;
    private Button stopTrackingButton;
    private Button setPhoneNumber;
    private Button sendSMS;
    private Button deleteNumber;

    private TextView latitudeText;
    private TextView longitudeText;
    private TextView accuracyText;
    private TextView homeLocation;

    private LocationTracker locationTracker;
    private float savedLatitude;
    private float savedLongitude;

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    public final static String SMS_ACTION = "POST_PC.ACTION_SEND_SMS";
    public static final String LOCATION_ACTION = "location changed";

    private BroadcastReceiver localSendSmsBroadcastReceiver;
    private Receiver receiver;
    private DialogHandler dialogHandler;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;


    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_SMS
    };
    SharedPreferences sharedPreferences;


    class Receiver extends BroadcastReceiver {

        public Receiver() {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(LOCATION_ACTION)) {
                LocationInfo locationInfo = new LocationInfo(intent.getDoubleExtra("accuracy", 0), intent.getDoubleExtra(LONGITUDE, 0f)
                        , intent.getDoubleExtra(LATITUDE, 0f));
                MainActivity.this.longitudeText.setText("Longitude: " + Double.toString(intent.getDoubleExtra(LONGITUDE, 0f)));
                MainActivity.this.latitudeText.setText("Latitude: " + Double.toString(intent.getDoubleExtra(LATITUDE, 0f)));
                MainActivity.this.accuracyText.setText("Accuracy: " + Double.toString(intent.getDoubleExtra("accuracy", 0f)));
                MainActivity.this.changeHomeLocation(locationInfo);

            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setPhoneNumber = findViewById(R.id.setPhoneNumber);

        this.deleteNumber = findViewById(R.id.deleteNumber);;

        this.trackingButton = findViewById(R.id.buttonTrackPoition);
        this.stopTrackingButton = findViewById(R.id.stopTracking);

        this.sendSMS = findViewById(R.id.sendSMS);
        this.sendSMS.setVisibility(View.INVISIBLE);

        this.showHomeLocation = findViewById(R.id.showHome);
        this.showHomeLocation.setVisibility(View.INVISIBLE);

        this.clearHomeLocation = findViewById(R.id.clearHomeLocation);
        this.clearHomeLocation.setVisibility(View.INVISIBLE);

        this.longitudeText = findViewById(R.id.longitude);
        this.latitudeText = findViewById(R.id.latitude);
        this.accuracyText = findViewById(R.id.accuracy);
        this.homeLocation = findViewById(R.id.homeLocation);

        final Activity activity = this;
        this.locationTracker = new LocationTracker(this);
        sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);

        this.savedLatitude = sharedPreferences.getFloat(LATITUDE, 0F);
        this.savedLongitude = sharedPreferences.getFloat(LONGITUDE, 0F);
        this.dialogHandler = new DialogHandler(sharedPreferences, activity);
        this.checkSP();
        this.receiver = new Receiver();
        this.localSendSmsBroadcastReceiver = new LocalSendSmsBroadcastReceiver();
        registerReceiver(localSendSmsBroadcastReceiver, new IntentFilter(LocalSendSmsBroadcastReceiver.actionReceiver));
        //this.phoneNum = sharedPreferences.getString(LocalSendSmsBroadcastReceiver.PHONE, "");
//        registerReceiver(this.receiver, new IntentFilter(LOCATION_ACTION));

        this.clearHomeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.clearHomeData();
            }
        });

        this.trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasLocationPermission =
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED;


                if (hasLocationPermission) {
                    MainActivity.this.checkStartTracking();
                    MainActivity.this.locationTracker.startTracking();

                } else {

                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS,
                            PERMISSION_ALL);
                }
            }
        });

        this.stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.checkToStop();
            }
        });

        this.setPhoneNumber.setVisibility(View.VISIBLE);
        this.setPhoneNumberListener();

        this.sendSMS.setVisibility(View.VISIBLE);
        this.sendSMStoPhoneNumber();
        this.deleteNumber();

    }

    private void deleteNumber()
    {
        this.deleteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.sharedPreferences.edit().putString(LocalSendSmsBroadcastReceiver.PHONE,"").apply();
                MainActivity.this.sendSMS.setVisibility(View.INVISIBLE);
                MainActivity.this.setPhoneNumber.setVisibility(View.VISIBLE);
                int x = 0;
            }
        });

    }

    private void sendSMStoPhoneNumber()
    {
        this.sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = sharedPreferences.getString(LocalSendSmsBroadcastReceiver.PHONE, "");
                if (!sharedPreferences.getString(LocalSendSmsBroadcastReceiver.PHONE, "").equals("")) {
                    Intent intent = new Intent(SMS_ACTION);
                    intent.putExtra(LocalSendSmsBroadcastReceiver.PHONE, phoneNum);
                    intent.putExtra(LocalSendSmsBroadcastReceiver.CONTENT, "Honey, I'm home");
                    sendBroadcast(intent);
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Set Number First", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void setPhoneNumberListener() {
        setPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasSMSPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
                if (hasSMSPermission) {

                    MainActivity.this.dialogHandler.show(getSupportFragmentManager(), "test test");
                    MainActivity.this.sendSMS.setVisibility(View.VISIBLE);
                    MainActivity.this.setPhoneNumber.setVisibility(View.INVISIBLE);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,
                            PERMISSION_ALL);

                }
            }
        });
    }


    private void checkStartTracking() {
        this.trackingButton.setVisibility(View.INVISIBLE);
        this.stopTrackingButton.setVisibility(View.VISIBLE);
        registerReceiver(this.receiver, new IntentFilter(LOCATION_ACTION));
    }

    private void checkToStop() {
        this.trackingButton.setVisibility(View.VISIBLE);
        this.stopTrackingButton.setVisibility(View.INVISIBLE);
        if (this.receiver != null) {
            unregisterReceiver(this.receiver);
        }
    }

    private void checkSP() {
        if (sharedPreferences.getFloat(LATITUDE, 0F) != 0F || sharedPreferences.getFloat(LONGITUDE, 0F) != 0) {
            this.homeLocation.setVisibility(View.VISIBLE);
            this.homeLocation.setText("Home Location: " + "<LA: " + savedLatitude + ", LO: " + savedLongitude + ">");
        }
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
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            MainActivity.this.dialogHandler.show(getSupportFragmentManager(), "test test");
            MainActivity.this.sendSMS.setVisibility(View.VISIBLE);
        }
        else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // reached here? means we asked the user for this permission more than once,
                // and they still refuse. This would be a good time to open up a dialog
                // explaining why we need this permission
            }
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            MainActivity.this.locationTracker.startTracking(); // cool
        }
        else {
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

    private void clearHomeData() {
        sharedPreferences.edit().putFloat(LATITUDE, 0F).putFloat(LONGITUDE, 0F).apply();
        this.homeLocation.setText("");
        this.clearHomeLocation.setVisibility(View.INVISIBLE);
    }

    public void changeHomeLocation(final LocationInfo location) {
        if (location.getAccuracy() <= 50) {
            showHomeLocation.setVisibility(View.VISIBLE);
            showHomeLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreferences.edit().putFloat(LATITUDE, (float) location.getLatitude()).putFloat(LONGITUDE, (float) location.getLongitude()).apply();
                    homeLocation.setVisibility(View.VISIBLE);
                    homeLocation.setText("Home Location: " + "<LA: " + location.getLatitude() + ", LO: " + location.getLongitude() + ">");
                    Toast toast = Toast.makeText(MainActivity.this, "Changed Home Location", Toast.LENGTH_SHORT);
                    toast.show();
                    MainActivity.this.clearHomeLocation.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showHomeLocation.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

}