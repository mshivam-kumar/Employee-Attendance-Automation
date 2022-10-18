package com.attendance.ai.smart;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.attendance.ai.smart.alertMessage.GetAlerts;
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

public class AdminLogIn extends AppCompatActivity {
    private LocationRequest locationRequest;
    private Button loadCurrentLocationButton;
    private static double latitude1,longitude1;
    private EditText editTextLatitude,editTextLongitude,editTextMaxDistance;
    private TextView showMsg;
    private String SPrefLatitude,SPrefLongitude,SPrefMaxDistanc;


    SharedPreferences adminSaveLatitudeLongitude;
    SharedPreferences.Editor adminSaveLatitudeLongitudeSSPrefEditor;

    private static final long TIME_INTERVAL_GAP=2000;
    private long lastTimeClicked=System.currentTimeMillis();
    private  boolean checkButtonClickedForFirstTime=false;;

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_log_in);

        //Save current longitude and latitude in shared preference ...this saved data will be used
        //to check the 100 meters range of user ...from saved location

        loadCurrentLocationButton=findViewById(R.id.loadCurrentLocation);
        editTextLatitude=findViewById(R.id.editTextLatitudeToSave);
        editTextLongitude=findViewById(R.id.editTextLogitudeToSave);
        editTextMaxDistance=findViewById(R.id.editTextMaxDisToSave2);
        showMsg=findViewById(R.id.showMsg);

        adminSaveLatitudeLongitude= getSharedPreferences("latitude_longitude_details",MODE_PRIVATE);
        adminSaveLatitudeLongitudeSSPrefEditor=adminSaveLatitudeLongitude.edit();

        SPrefLatitude=adminSaveLatitudeLongitude.getString("latitude","");
        SPrefLongitude=adminSaveLatitudeLongitude.getString("longitude","");
        SPrefMaxDistanc=adminSaveLatitudeLongitude.getString("maximum_distance_allowed","");

        editTextLatitude.setText(SPrefLatitude);
        editTextLongitude.setText(SPrefLongitude);
        editTextMaxDistance.setText(SPrefMaxDistanc);



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
                    startActivity(new Intent(AdminLogIn.this,ServiceNotSupported.class));
//                    finish();
                }
                else if(id==R.id.nav_userInstructions)
                {
//                    startActivity(new Intent(UserLogIn.this,UserInstructions.class));
                    startActivity(new Intent(AdminLogIn.this,ServiceNotSupported.class));
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





        Button manageUserFaceRecog=findViewById(R.id.manageUserFaceRecog);
        manageUserFaceRecog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminLogIn.this,AdminSetupForFaceRecog.class));
            }
        });

        Button saveLocation=findViewById(R.id.saveLocation);
        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat=editTextLatitude.getText().toString();
                String lon=editTextLongitude.getText().toString();
                String dist=editTextMaxDistance.getText().toString();
                if(lat.length()>0 && lon.length()>0 && dist.length()>0) {
                    adminSaveLatitudeLongitudeSSPrefEditor.putString("latitude", editTextLatitude.getText().toString().trim());
                    adminSaveLatitudeLongitudeSSPrefEditor.commit();
                    adminSaveLatitudeLongitudeSSPrefEditor.putString("longitude", editTextLongitude.getText().toString().trim());
                    adminSaveLatitudeLongitudeSSPrefEditor.commit();
                    adminSaveLatitudeLongitudeSSPrefEditor.putString("maximum_distance_allowed", editTextMaxDistance.getText().toString().trim());
                    adminSaveLatitudeLongitudeSSPrefEditor.commit();

                    Toast.makeText(AdminLogIn.this, "New Data Updated Successfully.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showMsg.setText("");
                    GetAlerts ga=new GetAlerts();
                    ga.alertDialogBox(AdminLogIn.this,"None Of The Text Boxes Can Be Empty.Please Fill All The Required Details Correctly.","Unable To Save Range Credentials");
                }
            }
        });



        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        loadCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

//                long now=System.currentTimeMillis();
//                if(((now-lastTimeClicked)<TIME_INTERVAL_GAP) && checkButtonClickedForFirstTime) {
////                    UserLogIn.checkButtonClickedForFirstTime=true;
//                    showMsg.setTextColor(Color.parseColor("#D5261A"));
//
//                    showMsg.setText("Cllick The Button After 2 Seconds \n      For More Accurate Results");
//                    return;
//                }
//                lastTimeClicked=now;

//                checkButtonClickedForFirstTime=true;

                showMsg.setText("Please Wait !!");
                getCurrentLocation();

                loadCurrentLocationButton.setEnabled(false);
            }
        });

        Button empAttendanceSettings=findViewById(R.id.buttonAttendanceSettings);
        empAttendanceSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminLogIn.this,EmployeesAttendanceSettings.class));
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
            if (ActivityCompat.checkSelfPermission(AdminLogIn.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(AdminLogIn.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(AdminLogIn.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        AdminLogIn.latitude1=latitude;
                                        AdminLogIn.longitude1=longitude;
                                        editTextLatitude.setText(latitude+"");
                                        editTextLongitude.setText(longitude+"");

                                        loadCurrentLocationButton.setEnabled(true);

                                        showMsg.setTextColor(Color.parseColor("#30AE35"));
                                        showMsg.setText("Current Location Loaded Successfully\n          Into Text Boxes Now You\n               Can Save The Data");
                                        Log.d("svm","Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
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
                    Toast.makeText(AdminLogIn.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(AdminLogIn.this, 2);
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






}
