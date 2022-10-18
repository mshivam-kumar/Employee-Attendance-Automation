package com.attendance.ai.smart;

import static java.lang.Math.abs;

import java.lang.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;


public class UserLogIn extends AppCompatActivity {

    private TextView AddressText;
    private Button checkDistanceRange;
    private LocationRequest locationRequest;
    private static double latitude;
    private static double longitude;
    private static double latitudeCompareWith;
    private static double longitudeCompareWith;
    private static double distanceCompareWith;

    private static final long TIME_INTERVAL_GAP=2000;
    private long lastTimeClicked=System.currentTimeMillis();
    private  boolean checkButtonClickedForFirstTime=false;;


    private String SPrefLatitude,SPrefLongitude,SPrefMaxDistanc;
    private TextView showMsg;


    SharedPreferences adminSaveLatitudeLongitude;
    SharedPreferences.Editor adminSaveLatitudeLongitudeSSPrefEditor;
    private static double calculatedDistanceOfUser;
    private Button faceRecognition;

    public static  long FACE_RECOGNITION_BUTTON_PRESSED_TIME;


    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onRestart() {//With back button reload this activity to update changes in group list
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_log_in);


        adminSaveLatitudeLongitude= getSharedPreferences("latitude_longitude_details",MODE_PRIVATE);
        adminSaveLatitudeLongitudeSSPrefEditor=adminSaveLatitudeLongitude.edit();

        SPrefLatitude=adminSaveLatitudeLongitude.getString("latitude","");
        SPrefLongitude=adminSaveLatitudeLongitude.getString("longitude","");
        SPrefMaxDistanc=adminSaveLatitudeLongitude.getString("maximum_distance_allowed","");

        try {
            latitudeCompareWith = Double.parseDouble(SPrefLatitude.trim());
            longitudeCompareWith = Double.parseDouble(SPrefLongitude.trim());
            distanceCompareWith = Double.parseDouble(SPrefMaxDistanc.trim());
        }
        catch (Exception e)
        {
            Log.d("svm","Unable to parse shared preference string to double\n" +
                    "Error : "+e.getMessage());
        }



        AddressText = findViewById(R.id.showLocStatus);
        checkDistanceRange = findViewById(R.id.checkYourDisRange);
        showMsg=findViewById(R.id.showMsgUserLogIn);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        TextView inRange=findViewById(R.id.textViewInRange);



        //set click results to menu button
        findViewById(R.id.nav_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dl.isDrawerOpen(GravityCompat.END)) {
                    dl.closeDrawer(GravityCompat.END);
                } else {
                    dl.openDrawer(GravityCompat.END);
                }
            }
        });


        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        dl = (DrawerLayout) findViewById(R.id.dl);


        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
//        abdt = new ActionBarDrawerToggle(this, dl, R.drawable.ic_baseline_person_24, R.string.Open, R.string.Close);
//        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                onOptionsItemSelected(item);
                int id=item.getItemId();


//                if(id==R.id.manage_individual_person_data)
//                {
//
////                    startActivity(new Intent(UserLogIn.this, HOMEIndividualPersonDataListView.class));
//                    startActivity(new Intent(UserLogIn.this, ChooseMoneyWillGiveOrGetIndividualPersonGroup.class));
//
//                }
                if(id==R.id.nav_about)
                {
//                    startActivity(new Intent(UserLogIn.this,About.class));
                    startActivity(new Intent(UserLogIn.this,ServiceNotSupported.class));
//                    finish();
                }
                else if(id==R.id.nav_userInstructions)
                {
//                    startActivity(new Intent(UserLogIn.this,UserInstructions.class));
                    startActivity(new Intent(UserLogIn.this,ServiceNotSupported.class));
                }
//                else if(id==R.id.settings)
//                {
//                    startActivity(new Intent(UserLogIn.this,SettingsApp.class));
//                }
//                else if(id==R.id.share_app)
//                {
//                    Intent sendIntent = new Intent();
//                    sendIntent.setAction(Intent.ACTION_SEND);
//                    sendIntent.setType("text/plain");
//                    sendIntent.putExtra(Intent.EXTRA_TEXT,
//                            "Make yourself calculator and register free, automate your data today.\nDownload Data Shielder app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//                    startActivity(Intent.createChooser(sendIntent, "Share via"));
////                    startActivity(sendIntent);
////                    Toast.makeText(UserLogIn.this, "Share App", Toast.LENGTH_SHORT).show();
////                    startActivity(new Intent(UserLogIn.this,UserInstructions.class));
//                }

//                else if(id==R.id.rate_us)
//                {
//                    try {
//                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
//                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(goToMarket);
//                        //Log.d("dbsvm","Inside try block to rate");
//                    } catch (ActivityNotFoundException e) {
//                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                        //Log.d("dbsvm","Inside catch block to rate");
//                    }
////                    Toast.makeText(UserLogIn.this, "Rate Us", Toast.LENGTH_SHORT).show();
////                    startActivity(new Intent(UserLogIn.this,UserInstructions.class));
//                }
                else if(id==R.id.contact_us)
                {
//                    Toast.makeText(UserLogIn.this, "Contact Us", Toast.LENGTH_SHORT).show();
                    String[] TO = {"datahiveshield.help@gmail.com"};
                    Uri uri = Uri.parse("mailto:datahiveshield.help@gmail.com")
                            .buildUpon()
                            .appendQueryParameter("subject", "")
                            .appendQueryParameter("body", "")
                            .build();
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }

                //Tree directory of all folders in Android-> created in google drive
                //Unable to upload a file to google drive automatically without
                //showing choosing intent to user
//                else if(id==R.id.google_drive_support)
//                {
//
////                    Toast.makeText(UserLogIn.this, "Save To Google Drive", Toast.LENGTH_SHORT).show();
//
//                    startActivity(new Intent(UserLogIn.this, GDriveMain.class));
////                    startActivity(new Intent(UserLogIn.this,ServiceNotSupported.class));
//                }


//                else if(id==R.id.nav_addOrSwitchAccount)
//                {
//                    GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
//                    startActivity(new Intent(UserLogIn.this,GoogleSignIn1.class));
//                    finish();
//
//                }
//                else if(id==R.id.nav_signOut)
//                {
////                    Toast.makeText(UserLogIn.this, "Sign Out", Toast.LENGTH_SHORT).show();
//                    GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
//                    startActivity(new Intent(UserLogIn.this,GoogleSignIn1.class));
//                    finish();
//                }
                return true;
            }
        });


       





        faceRecognition=findViewById(R.id.faceRecogUserLogIn);
//        faceRecognition.setVisibility(View.GONE);
        faceRecognition.setEnabled(false);

        faceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogIn.FACE_RECOGNITION_BUTTON_PRESSED_TIME=System.currentTimeMillis();
                startActivity(new Intent(UserLogIn.this, UserSetupForFaceRecog.class));
            }
        });

        checkDistanceRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                faceRecognition.setEnabled(false);
                long now=System.currentTimeMillis();
                if(((now-lastTimeClicked)<TIME_INTERVAL_GAP) && checkButtonClickedForFirstTime) {
//                    UserLogIn.checkButtonClickedForFirstTime=true;

                    inRange.setText("");
                    showMsg.setText("Cllick The Button After 2 Seconds \n      For More Accurate Results");
                    return;
                }
                lastTimeClicked=now;

                checkButtonClickedForFirstTime=true;

                inRange.setText("");


                showMsg.setText("Please Wait !!");

                getCurrentLocation();
                checkDistanceRange.setEnabled(false);


            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(UserLogIn.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(UserLogIn.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(UserLogIn.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        UserLogIn.latitude=latitude;
                                        UserLogIn.longitude=longitude;

                                        UserLogIn.calculatedDistanceOfUser=distance()*1000;//distance in meters and will be
                                        //compared in meters

                                        if(distanceCompareWith<13)
                                        {
                                            UserLogIn.calculatedDistanceOfUser=UserLogIn.calculatedDistanceOfUser-30;
                                        }
                                        else if(distanceCompareWith>0)
                                        {
                                            UserLogIn.calculatedDistanceOfUser = UserLogIn.calculatedDistanceOfUser - 20;//giving 15% more accuracy
                                        }
                                        //substrating 10 from the calculated distance to makit more accurate


                                        AddressText.setText("Your Location : \nLatitude:    "+ latitude  + "\nLongitude: "+ longitude);

                                        Log.d("svm","Your Location : \nLatitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                                Log.d("svm","dis : "+UserLogIn.calculatedDistanceOfUser+" meters");

//                                        if(UserLogIn.calculatedDistanceOfUser<=distanceCompareWith)
//                                        if(Double.compare(UserLogIn.calculatedDistanceOfUser,1)<0)
//                                        {
//                                            UserLogIn.calculatedDistanceOfUser*=110;
//                                        Log.d("svm","dis*20 : "+UserLogIn.calculatedDistanceOfUser+" meters");
//                                        }

                                        double diff=UserLogIn.calculatedDistanceOfUser-Double.parseDouble(String.valueOf(distanceCompareWith));
                                        Log.d("svm","New dif "+(diff-1)+" abs :"+Math.abs(diff));
                                        TextView inRange=findViewById(R.id.textViewInRange);
                                        if(Double.compare(UserLogIn.calculatedDistanceOfUser,distanceCompareWith)<0)
                                        {

                                            inRange.setText("                       Congrats !! \nYou Are In Range Of Specified Location\n" +
                                                    "Now You Can Go For Face Recognition.\n        Please Press The Button Below");
                                            showMsg.setText("");
//                                            faceRecognition.setVisibility(View.VISIBLE);
                                            faceRecognition.setEnabled(true);

                                        }
                                        else
                                        {
                                            double dif=Double.parseDouble(String.valueOf(distanceCompareWith))-UserLogIn.calculatedDistanceOfUser;
                                            if(Double.compare(Math.abs(dif),5000000.00)>0)//5000 km
                                            {
                                                inRange.setText("");
                                                showMsg.setText("                             Sorry !!\n You Are " + Math.abs(dif) + " meters\n" +
                                                        "            away from the specified range.\n If you find the data inappropriate, ask your\n            admin to set range credentials.");
                                            }
                                            else
                                            {
                                                inRange.setText("");
                                                showMsg.setText("                             Sorry !!\n You Are "+Math.abs(dif)+" meters\n" +
                                                        "            away from specified range");
                                            }
                                        }
                                        UserLogIn.calculatedDistanceOfUser=0;
                                        checkDistanceRange.setEnabled(true);


                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(UserLogIn.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(UserLogIn.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    private  double distance()
    {
        double lat1=0,lon1=0,lat2=0,lon2=0;
        try {
            lat1 = latitudeCompareWith;
            lon1 = longitudeCompareWith;
            lat2 = UserLogIn.latitude;
            lon2 = UserLogIn.longitude;
        }
        catch (Exception  e)
        {
            Log.d("svm","Inside distance Error : "+e.getMessage());
        }


        Log.d("svm"," Inside distance Lat1 : "+lat1+"" +
                "\nLon1 : "+lon1+"\nLat2 : "+lat2+"\n Lon2 : "+lon2);
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
//        lon1 = Math.toRadians(lon1);
//        lon2 = Math.toRadians(lon2);
//        lat1 = Math.toRadians(lat1);
//        lat2 = Math.toRadians(lat2);
//
//        // Haversine formula
//        double dlon = lon2 - lon1;
//        double dlat = lat2 - lat1;
//        double a = Math.pow(Math.sin(dlat / 2), 2)
//                + Math.cos(lat1) * Math.cos(lat2)
//                * Math.pow(Math.sin(dlon / 2),2);
//
//        double c = 2 * Math.asin(Math.sqrt(a));
//
//        // Radius of earth in kilometers. Use 3956
//        // for miles
//        double r = 6371;
//
//        UserLogIn.calculatedDistanceOfUser=0;
//        Log.d("svm","dis : "+(c*r*1000)+" meters");

        double p = 0.017453292519943295;    // Math.PI / 180
//        double c = Math.cos;
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2+
                Math.cos(lat1*p)*Math.cos(lat2*p)*
                        (1-Math.cos((lon2-lon1)*p))/2;

        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km

        // calculate the result
//        return(c * r);
    }



}