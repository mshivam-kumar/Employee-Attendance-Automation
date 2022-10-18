package com.attendance.ai.smart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.attendance.ai.smart.data.AdminDbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserSetupForFaceRecog extends AppCompatActivity {
    FaceDetector detector;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
//    ImageView face_preview;
    Interpreter tfLite;
    TextView reco_name,preview_info;
//    Button recognize;
    Button camera_switch, actions;
//    ImageButton add_face;
    CameraSelector cameraSelector;
    boolean start=true,flipX=false;
    Context context= UserSetupForFaceRecog.this;
//    int cam_face=CameraSelector.LENS_FACING_BACK; //Default Back Camera
    int cam_face=CameraSelector.LENS_FACING_FRONT; // for userr Default Front Camera

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile="mobile_face_net.tflite"; //model name

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces

    private  String tempConformNameText,conformNameText;
    
    AdminDbHandler db;
    public String gloabalEmpId,tempEmpId;

    private static final long TIME_ALLOWED_TO_SUBMIT_ATTENDANCE=60000;//1 min=60000 MILI SECONDS 1.5 given after accessing this activity

    Calendar calendar=Calendar.getInstance();
    SimpleDateFormat sdf_1 = new SimpleDateFormat("MMMM_yyyy");
    public String monthYrString = sdf_1.format(calendar.getTime());
    public  String currentMonthAttStoringTableName;



//    //today's data
//    public static Date DATE1=Date.from(Instant.now());
//    static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");//current date
//
//    public static String todayDate = formatter.format(DATE1);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            db=new AdminDbHandler(UserSetupForFaceRecog.this);
            registered = readFromSP(); //Load saved faces from memory when app starts
        }
        catch (Exception e)
        {
            Log.d("svm","Unable to load image data from readFromSP method\n" +
                    "Error : "+e.getMessage());
        }
        setContentView(R.layout.user_set_up_for_face_recog);
//        face_preview =findViewById(R.id.imageView);
        reco_name =findViewById(R.id.textView);
        preview_info =findViewById(R.id.textView2);

        TextView textViewConform=findViewById(R.id.textViewConformName);
        Button submitAttendence=findViewById(R.id.submitAttendance);
        submitAttendence.setEnabled(false);

//        add_face=findViewById(R.id.imageButton);
//        add_face.setVisibility(View.INVISIBLE);

        Button conformButton=findViewById(R.id.button2);
        conformButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                conformNameText=tempConformNameText;
                String gloabalEmpIdWithouTt=tempEmpId;
                gloabalEmpId="t"+tempEmpId;
                boolean flag=true;

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                Date d1_current_time=null,d2_min_entry=null,d2_min_exit=null;
                String currentTime = sdf.format(cal.getTime());

                try
                {
                    AttendanceSettings as_default=new AttendanceSettings("8:40","16:45","2","5","12:30","2");
                    db.insertAttendanceSettings(as_default);

                }
                catch (Exception e)
                {
                    //unable to get default minimum attendance requirement values
                    textViewConform.setText(" There Is Some Problem In getting Minimum Attendance Requirements. Please Contact To Your Admin ");
                    Log.d("svm"," There Is Some Problem In inserting Minimum Attendance Requirements. Please Contact To Your Admin \n error : "+e.getMessage());

                }

                AttendanceSettings as=db.getAttendanceSettings();//attendance settings

                try {
                    d1_current_time = sdf.parse(currentTime);
                    d2_min_entry = sdf.parse(as.getMinEntryTime());
                    d2_min_exit=sdf.parse(as.getMinExitTime());
                } catch (ParseException e) {
                    Log.d("svm","Inside confirm button , unable to get current time , error : "+e.getMessage());
                }


                try {
                    currentMonthAttStoringTableName=gloabalEmpId+monthYrString+"emp_records".toLowerCase();
                    Log.d("svm","Inside conform button currentMonthAttStoringTable : "+currentMonthAttStoringTableName);

//                    SQLiteDatabase db1 = db.getWritableDatabase();
//                    db.createEmployeesAttendanceRecordStoringTable(db1,currentMonthAttStoringTableName);
//                    Log.d("svm","current month att storing table name"+currentMonthAttStoringTableName);


//                    if(!db.isEmployeeCurrentMonthYrExist(gloabalEmpId+"month_yr",monthYrString))//if current month is not in database
//                    {
//
//
//
//                        AttendanceSettings as=db.getAttendanceSettings();
//                        Log.d("svm","total monthly graces : "+as.getTotalNumOfMonthlyGraces()+" total 1 hr graces : "+as.getTotalNumOfMonthlyOneHrLeaves()+" fetched from database to save inside monthYr table");
//                        db.insertMontYrIntoTable(gloabalEmpId+"month_yr",monthYrString,"0","0");
//                        //now create table to store attendance data for current month only
//
//                        Log.d("svm","current month att storing table name");
//                        SQLiteDatabase db_1 = db.getWritableDatabase();
//                        db.createEmployeesAttendanceRecordStoringTable(db_1,currentMonthAttStoringTableName);
//
//
//                    }

                    if (!conformNameText.equals("null")) {//registered person identified
                        SQLiteDatabase db1 = db.getWritableDatabase();
                        db.createEmployeesAttendanceRecordStoringTable(db1,currentMonthAttStoringTableName);
                        Log.d("svm","current month att storing table name"+currentMonthAttStoringTableName);

                        if(!db.isEmployeeCurrentMonthYrExist(gloabalEmpId+"month_yr",monthYrString))//if current month is not in database
                        {



//                            AttendanceSettings as=db.getAttendanceSettings();
                            Log.d("svm","total monthly graces : "+as.getTotalNumOfMonthlyGraces()+" total 1 hr graces : "+as.getTotalNumOfMonthlyOneHrLeaves()+" fetched from database to save inside monthYr table");
                            db.insertMontYrIntoTable(gloabalEmpId+"month_yr",monthYrString,"0","0");
                            //now create table to store attendance data for current month only

                            Log.d("svm","current month att storing table name");
                            SQLiteDatabase db_1 = db.getWritableDatabase();
                            db.createEmployeesAttendanceRecordStoringTable(db_1,currentMonthAttStoringTableName);


                        }

                       long elapsed_current_exit_time=d2_min_exit.getTime()-d1_current_time.getTime();//if <0 after min exit time else before min exit time



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            if(db.isExitTimeExist(gloabalEmpId,EmployeeDetails.todayDate) && db.getTodayEntryTime(gloabalEmpId,EmployeeDetails.todayDate).length()>0)
                            if(db.isExitTimeExist(currentMonthAttStoringTableName,EmployeeDetails.todayDate) && db.getTodayEntryTime(gloabalEmpId,EmployeeDetails.todayDate).length()>0 && elapsed_current_exit_time<0)//after min exit time
                            {
                                textViewConform.setText("name : " + conformNameText + " id : " + gloabalEmpIdWithouTt + "  recognition  conformed. Your entry \nand exit time is already submitted");
                                flag=false;


                            }

//                            else if(flag && db.getTodayEntryTime(gloabalEmpId,EmployeeDetails.todayDate).length()>0) {
                            else if(flag && db.getTodayEntryTime(currentMonthAttStoringTableName,EmployeeDetails.todayDate).length()>0) {
                                textViewConform.setText("name : " + conformNameText + " id : " + gloabalEmpIdWithouTt + "  recognition  conformed. You have \ndone entry.your exit is still pending. ");
                                submitAttendence.setEnabled(true);

                            }
                            else
                            {
                                textViewConform.setText("name : " + conformNameText + " id : " + gloabalEmpIdWithouTt + "  recognition  conformed");
                                submitAttendence.setEnabled(true);



                            }
                        }

                    } else {
                        textViewConform.setText("                           Sorry !!\n            You Are Not Registered");
                    }

                }
                catch (Exception e)
                {
                    if(!registered.isEmpty()) {
                        textViewConform.setText("Please Bring Face In View Of Camera");
                        Log.d("svm", "Inside UserSetup conformButton Error : " + e.getMessage());
                    }
                }
                //If the person is recognized successfully , it should not be unknown
                //After getting conform name mark the user present in database
                //get a unique id from recognition which will  work as primary key
            }

        });


        submitAttendence.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                long now=System.currentTimeMillis();
                try
                {
                    AttendanceSettings as_default=new AttendanceSettings("8:40","16:45","2","5","12:30","2");
                    db.insertAttendanceSettings(as_default);

                }
                catch (Exception e)
                {
                    //unable to get default minimum attendance requirement values
                    textViewConform.setText(" There Is Some Problem In getting Minimum Attendance Requirements. Please Contact To Your Admin ");
                    Log.d("svm"," There Is Some Problem In inserting Minimum Attendance Requirements. Please Contact To Your Admin \n error : "+e.getMessage());

                }
                AttendanceSettings as=db.getAttendanceSettings();//attendance settings

                //allow user to submit attendance for only 1 minute after checking range
                if(((now-UserLogIn.FACE_RECOGNITION_BUTTON_PRESSED_TIME)<UserSetupForFaceRecog.TIME_ALLOWED_TO_SUBMIT_ATTENDANCE) ) {

                    try {

                        try {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                ArrayList<EmployeeDetails> employeeDetailsArrayList = db.getIndividualEmployeeAttendanceRecords(currentMonthAttStoringTableName);
//                                Log.d("svm", gloabalEmpId + " table attendance records");
                                Log.d("svm", currentMonthAttStoringTableName + " table attendance records");

                                //saved attendance records of recognized employee, temporarly showing below
//                                for (EmployeeDetails e : employeeDetailsArrayList) {
//                                    Log.d("svm", "Date : " + e.getDate() + "\nEntry : " + e.getEntryTime() + "\n" +
//                                            "Exit : " + e.getExitTime() + "\nTotal service hrs : " + e.getTotalHoursServed() + "\n" +
//                                            "Attendance Status : " + e.getAttendanceStatus());
//                                }
                            }
                        } catch (Exception e) {
                            Log.d("svm", "Inside submitAttendance Error :" + e.getMessage());
                        }

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                        if (!conformNameText.equals("null")) {
                            //variable gloabalEmpId will work as table name for manipulation in database
                            //first create table
                            //unable to create table ,check later on

                            Log.d("svm", "global emp id : " + gloabalEmpId +" table name : "+currentMonthAttStoringTableName);
//                            db.createEmployeeAttendanceStoringTableWithIdName(db1, gloabalEmpId);//table create for employee having globalEmpId with this id

//                            db.createEmployeesAttendanceRecordStoringTable(db1,gloabalEmpId+"emp_records");

                            String currentTime = sdf.format(cal.getTime());
                            Log.d("svm", "Current  time : " + currentTime);
                            Date d1_current_time=null,d2_min_entry=null,dEntryTimeLimitWithGraceAdded=null,d2_min_exit=null,dMinHalfDayExit=null;
                            long elapsed_current_entry=0,elapsed_current_entry_with_grace_added=0,elapsed_current_exit_time=0,elapsed_current_half_day_exit_time=0;

                            long totalMonthly1HrLeaves=Integer.parseInt(as.getTotalNumOfMonthlyOneHrLeaves());
                            long oneHrDiffFromEntryTime=70;//minutes more than one hr
                            long oneHrDiffFromSecondEntryTime=70;//minutes more than one hr
                            long currentMonthUsed1HrLeaves=Integer.parseInt(db.getCurrentAccessedMonthYr1HourGracesFromTable(gloabalEmpId + "month_yr", monthYrString));

                            String todayExitTimeIfExists="";
                            String todayEntryTimeIfExists="";


                            try
                            {
                                d1_current_time = sdf.parse(currentTime);
                                d2_min_entry = sdf.parse(as.getMinEntryTime());
                                d2_min_exit=sdf.parse(as.getMinExitTime());
                                dMinHalfDayExit=sdf.parse(as.getMinHalfDayExitTime());
                                elapsed_current_entry = d2_min_entry.getTime() - d1_current_time.getTime();//if >0 before min entry time else after min entry time
                                elapsed_current_exit_time=d2_min_exit.getTime()-d1_current_time.getTime();//if <0 after min exit time else before min exit time
                                elapsed_current_half_day_exit_time=dMinHalfDayExit.getTime()-d1_current_time.getTime();

                                Log.d("svm","elapsed current : "+elapsed_current_entry);

                                String graceTime=as.getGraceTime();
                                String minEntryTime=as.getMinEntryTime();
                                String tmin=minEntryTime.substring(minEntryTime.indexOf(":")+1);
                                String tHr=minEntryTime.substring(0,minEntryTime.indexOf(":"));
                                Log.d("svm","minimum mins allowed : "+tmin);
                                Log.d("svm","without graced minutes : "+tmin);
                                String t1gmin="0";
                                try{
                                     t1gmin = (Integer.parseInt(tmin) + Integer.parseInt(graceTime)) + "";
                                    Log.d("svm", "with graced minutes : " + t1gmin);
                                }
                                catch (Exception e)
                                {
                                    Log.d("svm","Unable to add grace minutes to increase allowed time error : "+e.getMessage());
                                }

                                if(Integer.parseInt(t1gmin)>60)
                                {
                                    tHr=Integer.parseInt(tHr)+1+"";
                                    t1gmin=Integer.parseInt(t1gmin)-60+"";
                                }
                                String newMinTimeWithGraceAdded=tHr+":"+t1gmin;

                                Log.d("svm","new allowed time with grace added : "+newMinTimeWithGraceAdded);

                                dEntryTimeLimitWithGraceAdded=sdf.parse(newMinTimeWithGraceAdded);
                                elapsed_current_entry_with_grace_added=dEntryTimeLimitWithGraceAdded.getTime()-d1_current_time.getTime();
                                Log.d("svm","elapsed time result with grace : "+elapsed_current_entry_with_grace_added);

                                //for one hr leave
//                                long totalMonthly1HrLeaves=Integer.parseInt(as.getTotalNumOfMonthlyOneHrLeaves());
//                                long oneHrDiffFromEntryTime=70;//minutes more than one hr
//                                long oneHrDiffFromSecondEntryTime=70;//minutes more than one hr
//                                long currentMonthUsed1HrLeaves=Integer.parseInt(db.getCurrentAccessedMonthYr1HourGracesFromTable(gloabalEmpId + "month_yr", monthYrString));
//
//                                String todayExitTimeIfExists="";
//                                String todayEntryTimeIfExists="";
                                try {
                                    todayEntryTimeIfExists=db.getTodayEntryTime(currentMonthAttStoringTableName, EmployeeDetails.todayDate);
                                    oneHrDiffFromEntryTime=getMinutesTimeDifferenceBtwTwoTimes(todayEntryTimeIfExists,currentTime);
                                    Log.d("svm","One hr difference in minutes from entry time : "+oneHrDiffFromEntryTime);
                                    Log.d("svm","today entry time : "+todayEntryTimeIfExists);
                                }
                                catch (Exception e)
                                {
                                    Log.d("svm","Today entry time does not exist error : "+e.getMessage());
                                }

                                try {
                                    todayExitTimeIfExists=db.getTodayExitTime(currentMonthAttStoringTableName, EmployeeDetails.todayDate);
                                    oneHrDiffFromSecondEntryTime=getMinutesTimeDifferenceBtwTwoTimes(todayExitTimeIfExists,currentTime);
                                    Log.d("svm","One hr difference in minutes from second entry (first exit) time : "+oneHrDiffFromEntryTime);
                                    Log.d("svm","today exit time : "+todayExitTimeIfExists);
                                }
                                catch (Exception e)
                                {
                                    Log.d("svm","Today exit time does not exist error : "+e.getMessage());
                                }


                            }catch(Exception e)
                            {
                                System.out.print("error while submitting attendace : "+e.getMessage());
                            }


                            //check entry time for today's date if not found enter entry time
                            //if entry time found (already enterd) then enter total hrs,exit time and status as present
                            //if entry time and exit time both found then show you attendance is already submitted

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                if (!db.isEmployeeAttendanceWithTodayDATEExist(gloabalEmpId, EmployeeDetails.todayDate)) {//means entry time not inserted
                                Log.d("svm","table "+currentMonthAttStoringTableName);

                                //entry time not inserted
                                if (!db.isEmployeeAttendanceWithTodayDATEExist(currentMonthAttStoringTableName, EmployeeDetails.todayDate)) {//means entry time not inserted

                                    EmployeeDetails emp = new EmployeeDetails(EmployeeDetails.todayDate, currentTime, "", "", "","Absent");



                                    Log.d("svm","monthYr string : "+monthYrString);
//                                    if(!db.isEmployeeCurrentMonthYrExist(gloabalEmpId+"month_yr",monthYrString))//if current month is not in database
//                                    if(!db.isEmployeeCurrentMonthYrExist(gloabalEmpId+"month_yr",monthYrString))//if current month is not in database
//                                    {
//                                        db.insertMontYrIntoTable(gloabalEmpId+"month_yr",monthYrString,"0");
//                                        //now create table to store attendance data for current month only
//
//                                        Log.d("svm","current month att storing table name");
//                                        SQLiteDatabase db1 = db.getWritableDatabase();
//                                        db.createEmployeesAttendanceRecordStoringTable(db1,currentMonthAttStoringTableName);
//
//
//                                    }

//                                    db.insertIndividualEmployeeAttendanceDetails(gloabalEmpId, emp);//attendance data inserted
                                    if(elapsed_current_half_day_exit_time>0) {// >0 means before half day exit time ,employee can not do entry after min half day exit time
                                        //employee can do entry before minimum half day exit time for getting half day
                                        boolean flagGraceUsed = false;
                                        if (elapsed_current_entry > 0) {//before minEntryTime
                                            db.insertIndividualEmployeeAttendanceDetails(currentMonthAttStoringTableName, emp);//attendance data inserted
                                            textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry Is Submitted Successfully ");
                                        } else if (elapsed_current_entry_with_grace_added > 0) {
                                            try {
//                                            String m = monthYrString.substring(0, 1).toUpperCase();
//                                            String m1 = monthYrString.substring(1).toLowerCase();
//                                            m = m + m1;
                                                String gracesUsed = "0";
                                                Log.d("svm", " inside else if current accessed month yr : " + monthYrString);
                                                try {
                                                    gracesUsed = db.getCurrentAccessedMonthYrGracesFromTable(gloabalEmpId + "month_yr", monthYrString);
                                                    Log.d("svm", "received number of grace used by employee : " + gracesUsed);
                                                } catch (Exception e) {
                                                    gracesUsed = "0";
                                                    Log.d("svm", "error in fetching total Graces error : " + e.getMessage());
                                                }
                                                int gracesLeftByEmp = Integer.parseInt(as.getTotalNumOfMonthlyGraces()) - Integer.parseInt(gracesUsed);
                                                Log.d("svm", "total graces : " + as.getTotalNumOfMonthlyGraces());
                                                Log.d("svm", "graces used by employee : " + gracesUsed);
                                                Log.d("svm", "total graces left by employee : " + gracesLeftByEmp);
                                                if (gracesLeftByEmp > 0) {
                                                    db.insertIndividualEmployeeAttendanceDetails(currentMonthAttStoringTableName, emp);//attendance data inserted
                                                    //update the one increased grace in database
                                                    String totalNewGraces = Integer.parseInt(gracesUsed) + 1 + "";
                                                    db.updateEmployeeMonthYrGraces(gloabalEmpId + "month_yr", monthYrString, totalNewGraces);
                                                    textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry Is Submitted Successfully \ngrace is used");
                                                } else {
                                                    emp.setHalfFullDayStatus("HALF DAY");
                                                    db.insertIndividualEmployeeAttendanceDetails(currentMonthAttStoringTableName, emp);//attendance data inserted
                                                    textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry Is Submitted Successfully. Half Day Will Be Marked At " + as.getMinExitTime() + " Else Absent Will Be Marked.");

                                                }
                                            } catch (Exception e) {
                                                Log.d("svm", "Inside else if error : " + e.getMessage());
                                            }

                                        } else {
                                            emp.setHalfFullDayStatus("HALF DAY");
                                            db.insertIndividualEmployeeAttendanceDetails(currentMonthAttStoringTableName, emp);//attendance data inserted
                                            textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry Is Submitted Successfully. Half Day Will Be Marked At " + as.getMinExitTime() + " Else Absent Will Be Marked.");

                                        }
                                    }
                                    else
                                    {
//                                        emp=new EmployeeDetails(EmployeeDetails.todayDate, "", "", "", "","Absent");
//                                        db.insertIndividualEmployeeAttendanceDetails(currentMonthAttStoringTableName, emp);//attendance data inserted
                                        textViewConform.setTextColor(Color.rgb(255,0,0));
                                        textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry Can Not Be Submitted, As You Have Exceeded Minimum Half\n Day Exit Time i.e. " + as.getMinHalfDayExitTime() );

                                    }


                                        //Now take user to enter entry time
                                        Log.d("svm", "Entry time entered attendance with date exist " + EmployeeDetails.todayDate);
//                                } else if (!db.isExitTimeExist(gloabalEmpId, EmployeeDetails.todayDate)) {//check if exit time is inserted or not

                                }
                                //after doing entry for first time in current date employee gone for one hr break <60 minutes

                                else if(oneHrDiffFromEntryTime<=60 && currentMonthUsed1HrLeaves < totalMonthly1HrLeaves)
                                {
                                    int totalNewOneHrGracesUsedByEmp=Integer.parseInt(db.getCurrentAccessedMonthYr1HourGracesFromTable(gloabalEmpId + "month_yr", monthYrString))+1;
                                    db.updateEmployeeMonthYrOneHrGraces(gloabalEmpId + "month_yr", monthYrString, totalNewOneHrGracesUsedByEmp+"");
                                    db.updateEmployeeExitTimeToNULL(gloabalEmpId + "month_yr", monthYrString);

                                    textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Attendance Is Submitted Successfully. One Hr Leave Is Used. Now Come "+as.getMinExitTime() );


                                }
                                //if break is more than 60 minutes from entry time onwards
//                                else if(getMinutesTimeDifferenceBtwTwoTimes(db.getTodayEntryTime(currentMonthAttStoringTableName, EmployeeDetails.todayDate),sdf.format(cal.getTime()))>60 && Integer.parseInt(db.getCurrentAccessedMonthYr1HourGracesFromTable(gloabalEmpId + "month_yr", monthYrString))> (Integer.parseInt(as.getTotalNumOfMonthlyOneHrLeaves())))
//                                {
//                                    db.updateEmployeeExitTimeToNULL(gloabalEmpId + "month_yr", monthYrString);
//                                    textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Attendance Is Submitted Successfully. Half Day Will Be Marked.Now Come "+as.getMinExitTime()+" Time To Update Total Served Hrs");
//
//
//
//                                }

                                //if employee wish for one hr break after more than one hr of entry time
                                else if (db.isExitTimeExist(currentMonthAttStoringTableName, EmployeeDetails.todayDate) && oneHrDiffFromSecondEntryTime<=60 && currentMonthUsed1HrLeaves  <  totalMonthly1HrLeaves ) {//check if exit time is inserted or not

                                    int totalNewOneHrGracesUsedByEmp=Integer.parseInt(db.getCurrentAccessedMonthYr1HourGracesFromTable(gloabalEmpId + "month_yr", monthYrString))+1;
                                    db.updateEmployeeMonthYrOneHrGraces(gloabalEmpId + "month_yr", monthYrString, totalNewOneHrGracesUsedByEmp+"");
                                    db.updateEmployeeExitTimeToNULL(gloabalEmpId + "month_yr", monthYrString);

                                    String currentTime1 = sdf.format(cal.getTime());

                                    String exitTime = currentTime1;
//                                    String entryTime = db.getTodayEntryTime(gloabalEmpId, EmployeeDetails.todayDate);
                                    String entryTime = db.getTodayEntryTime(currentMonthAttStoringTableName, EmployeeDetails.todayDate);
                                    String attStatus = "Present";
                                    String tServicehrs = "";
                                    try {
                                        tServicehrs = getEntryExitTimeDifference(entryTime, currentTime);
                                    } catch (Exception e) {
                                        Log.d("svm", "InsertExitTime Error : " + e.getMessage());
                                    }
                                    String hFStatus="HALF DAY";
                                    if(elapsed_current_exit_time<0 && !db.getStatusOfHalfFullDayForParticularDate(currentMonthAttStoringTableName, EmployeeDetails.todayDate))//half day already not marked at entry time
                                    {
                                        hFStatus = "FULL DAY";
//                                    }
//                                    if(elapsed_current_exit_time<0 && db.getStatusOfHalfFullDayForParticularDate(currentMonthAttStoringTableName, EmployeeDetails.todayDate ) {


                                        db.insertExitTimeServiceHrsAndAttStatus(currentMonthAttStoringTableName, exitTime, tServicehrs, hFStatus, attStatus);//exit time entered

                                        textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Exit Time Is Submitted Successfully. Full Day Marked");
                                    }
                                    //before exit time and half day not marked at entry time
                                    else if(elapsed_current_exit_time>0 || db.getStatusOfHalfFullDayForParticularDate(currentMonthAttStoringTableName, EmployeeDetails.todayDate))
                                    {
                                        db.insertExitTimeServiceHrsAndAttStatus(currentMonthAttStoringTableName, exitTime, tServicehrs, hFStatus, attStatus);//exit time entered

                                        textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Exit Time Is Submitted Successfully. Half Day Marked");

                                    }
                                    else
                                    {
                                        textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Attendance Is Submitted Successfully. One Hr Leave Is Used. Now Come "+as.getMinExitTime());
                                    }

                                }


                                //if exit time is not inserted even one time
                                else if (!db.isExitTimeExist(currentMonthAttStoringTableName, EmployeeDetails.todayDate)) {//check if exit time is inserted or not

                                    
                                    String currentTime1 = sdf.format(cal.getTime());

                                    String exitTime = currentTime1;
//                                    String entryTime = db.getTodayEntryTime(gloabalEmpId, EmployeeDetails.todayDate);
                                    String entryTime = db.getTodayEntryTime(currentMonthAttStoringTableName, EmployeeDetails.todayDate);
                                    String attStatus = "Present";
                                    String tServicehrs = "";
                                    try {
                                        tServicehrs = getEntryExitTimeDifference(entryTime, currentTime);
                                    } catch (Exception e) {
                                        Log.d("svm", "InsertExitTime Error : " + e.getMessage());
                                    }
//                                    db.insertExitTimeServiceHrsAndAttStatus(gloabalEmpId, exitTime, tServicehrs, attStatus);//exit time entered

                                    String hFStatus="HALF DAY";
                                    if(elapsed_current_exit_time<0 && !db.getStatusOfHalfFullDayForParticularDate(currentMonthAttStoringTableName, EmployeeDetails.todayDate))//half day already not marked at entry time
                                    {
                                        hFStatus = "FULL DAY";
//                                    }
//                                    if(elapsed_current_exit_time<0 && db.getStatusOfHalfFullDayForParticularDate(currentMonthAttStoringTableName, EmployeeDetails.todayDate ) {


                                        db.insertExitTimeServiceHrsAndAttStatus(currentMonthAttStoringTableName, exitTime, tServicehrs, hFStatus, attStatus);//exit time entered

                                        textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Exit Time Is Submitted Successfully. Full Day Marked");
                                    }
                                    else
                                    {

                                        if(elapsed_current_half_day_exit_time<0) {
                                            db.insertExitTimeServiceHrsAndAttStatus(currentMonthAttStoringTableName, exitTime, tServicehrs, hFStatus, attStatus);//exit time entered
                                            textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Exit Time Is Submitted Successfully. Half Day Is Marked");
                                        }
                                        else
                                        {
                                            attStatus="Absent";
                                            hFStatus="";
                                            db.insertExitTimeServiceHrsAndAttStatus(currentMonthAttStoringTableName, exitTime, tServicehrs, hFStatus, attStatus);//exit time entered
                                            textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Exit Time Is Submitted Successfully. You Will Be Marked Absent As You Are Before Minimum Half Day Exit Time.");

                                        }

                                    }

                                    Log.d("svm", "Attendance already done Entry time already  exist");
                                } else //both entry time exit time exist, show user attendance already submitted
                                {
//                                String currentTime=sdf.format(cal.getTime());
//
//                                String exitTime=currentTime;
//                                String entryTime=db.getTodayEntryTime(gloabalEmpId,EmployeeDetails.todayDate);
//                                Log.d("svm","entry time "+entryTime);
//                                String attStatus="Present";
//                                String tServicehrs="";
//                                try {
//                                    tServicehrs=getEntryExitTimeDifference(entryTime,currentTime);
//                                    Log.d("svm","Service hrs : "+tServicehrs);
//                                }
//                                catch (Exception e)
//                                {
//                                    Log.d("svm","InsertExitTime Error : "+e.getMessage());
//                                }
                                    textViewConform.setText(" Thank You !! " + conformNameText + "\n Your Entry and Exit Time Is Already Submitted Successfully ");


                                    Log.d("svm", "Your attendance is  already submitted successfully");
                                }
                            }


                        } else {
                            textViewConform.setText("Unable To Submit The Attendance");
                        }
                        submitAttendence.setEnabled(false);
                    } catch (Exception e) {
                        textViewConform.setText("Unable To Submit The Attendance");
                        Log.d("svm", "Inside UserSetup submit Button Error : " + e.getMessage());


                    }
                }

                else
                {
                    textViewConform.setText("Sorry!! You Have Surpassed 1 Min Time Allowed To Submit The Attendance.\nGo Back And Check Range Again.");
                }
                //store the attendance into the database

            }
        });




//        face_preview.setVisibility(View.INVISIBLE);
//        recognize=findViewById(R.id.button3);

        camera_switch=findViewById(R.id.button5);

//        actions=findViewById(R.id.button2);

//        recognize.setEnabled(false);
//        recognize.setVisibility(View.GONE);//Admin controls hidden from user

//        actions.setEnabled(false);//making button first unable to use for user
//        actions.setVisibility(View.GONE);//temporarly hiding actions button ,  as these privilages are available to admin

        preview_info.setText("\n     Recognized Face:");

        //Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

//        try {
//
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//            }
//        }
//        catch (Exception e)
//        {
//            //Log.d("svm","Error : "+e.getMessage());
//        }


        //On-screen Action Button
//        actions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Select Action:");
//
//                // add a checkbox list
//                String[] names= {"View Recognition List","Update Recognition List","Save Recognitions","Load Recognitions","Clear All Recognitions","Import Photo (Beta)"};
//
//                builder.setItems(names, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        switch (which)
//                        {
//                            case 0:
//                                displaynameListview();
//                                break;
//                            case 1:
//                                updatenameListview();
//                                break;
//                            case 2:
//                                insertToSP(registered,false);
//                                break;
//                            case 3:
//                                registered.putAll(readFromSP());//load the saved data
//                                break;
//                            case 4:
//                                clearnameList();
//                                break;
//                            case 5:
//                                loadphoto();
//                                break;
//                        }
//
//                    }
//                });


//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", null);
//
//                // create and show the alert dialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        //On-screen switch to toggle between Cameras.
        camera_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cam_face==CameraSelector.LENS_FACING_BACK) {
                    cam_face = CameraSelector.LENS_FACING_FRONT;
                    flipX=true;
                }
                else {
                    cam_face = CameraSelector.LENS_FACING_BACK;
                    flipX=false;
                }
                cameraProvider.unbindAll();
                cameraBind();
            }
        });

//        add_face.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                addFace();
//            }
//        }));


//        recognize.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(recognize.getText().toString().equals("Recognize"))
//                {
//                    start=true;
//                    recognize.setText("Add Face");
//                    add_face.setVisibility(View.INVISIBLE);
//                    reco_name.setVisibility(View.VISIBLE);
//                    face_preview.setVisibility(View.INVISIBLE);
//                    preview_info.setText("\n    Recognized Face:");
//                    //preview_info.setVisibility(View.INVISIBLE);
//                }
//                else
//                {
//                    recognize.setText("Recognize");
//                    add_face.setVisibility(View.VISIBLE);
//                    reco_name.setVisibility(View.INVISIBLE);
//                    face_preview.setVisibility(View.VISIBLE);
//                    preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.");
//
//
//                }
//
//            }
//        });

        //Load model
        try {
            tfLite=new Interpreter(loadModelFile(UserSetupForFaceRecog.this,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();



    }

    public long getMinutesTimeDifferenceBtwTwoTimes(String time1,String time2)
    {

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

// Parsing the Time Period
        Date date1=new Date();
        Date date2=new Date();
        try
        {
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
        }catch(Exception e){}

// Calculating the difference in milliseconds
        long differenceInMilliSeconds
                = Math.abs(date2.getTime() - date1.getTime());

// Calculating the difference in Hours
        long differenceInHours
                = (differenceInMilliSeconds / (60 * 60 * 1000))
                % 24;

// Calculating the difference in Minutes
        long differenceInMinutes
                = (differenceInMilliSeconds / (60 * 1000)) % 60;

        long totalDifferenceOfMinutes=differenceInHours*60+differenceInMinutes;
        String t=differenceInHours+"h:"+differenceInMinutes+"m";
        Log.d("svm","total  time method : "+t);

        return totalDifferenceOfMinutes;
    }


    public String getEntryExitTimeDifference(String time1,String time2)
    {

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

// Parsing the Time Period
        Date date1=new Date();
        Date date2=new Date();
        try
        {
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
        }catch(Exception e){}

// Calculating the difference in milliseconds
        long differenceInMilliSeconds
                = Math.abs(date2.getTime() - date1.getTime());

// Calculating the difference in Hours
        long differenceInHours
                = (differenceInMilliSeconds / (60 * 60 * 1000))
                % 24;

// Calculating the difference in Minutes
        long differenceInMinutes
                = (differenceInMilliSeconds / (60 * 1000)) % 60;

        String t=differenceInHours+"h:"+differenceInMinutes+"m";
        Log.d("svm","total  time method "+t);

        return t;
    }



    private void addFace()
    {
        {

            start=false;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enter Name");

            // Set up the input
            final EditText input = new EditText(context);

            input.setInputType(InputType.TYPE_CLASS_TEXT );
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                    //Create and Initialize new object with Face embeddings and Name.
                    SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                            "0", "", -1f);
                    result.setExtra(embeedings);

                    registered.put( input.getText().toString(),result);
                    start=true;

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    start=true;
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }
    private  void clearnameList()
    {
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("Do you want to delete all Recognitions?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registered.clear();
                Toast.makeText(context, "Recognitions Cleared", Toast.LENGTH_SHORT).show();
            }
        });
        insertToSP(registered,true);
        builder.setNegativeButton("Cancel",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updatenameListview()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(registered.isEmpty()) {
            builder.setTitle("No Faces Added!!");
            builder.setPositiveButton("OK",null);
        }
        else{
            builder.setTitle("Select Recognition to delete:");

            // add a checkbox list
            String[] names= new String[registered.size()];
            boolean[] checkedItems = new boolean[registered.size()];
            int i=0;
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
            {
                //System.out.println("NAME"+entry.getKey());
                names[i]=entry.getKey();
                checkedItems[i]=false;
                i=i+1;

            }

            builder.setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // user checked or unchecked a box
                    //Toast.makeText(MainActivity.this, names[which], Toast.LENGTH_SHORT).show();
                    checkedItems[which]=isChecked;

                }
            });


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // System.out.println("status:"+ Arrays.toString(checkedItems));
                    for(int i=0;i<checkedItems.length;i++)
                    {
                        //System.out.println("status:"+checkedItems[i]);
                        if(checkedItems[i])
                        {
//                                Toast.makeText(MainActivity.this, names[i], Toast.LENGTH_SHORT).show();
                            registered.remove(names[i]);
                        }

                    }
                    Toast.makeText(context, "Recognitions Updated", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void displaynameListview()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // System.out.println("Registered"+registered);
        if(registered.isEmpty())
            builder.setTitle("No Faces Added!!");
        else
            builder.setTitle("Recognitions:");

        // add a checkbox list
        String[] names= new String[registered.size()];
        boolean[] checkedItems = new boolean[registered.size()];
        int i=0;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {
            //System.out.println("NAME"+entry.getKey());
            names[i]=entry.getKey();
            checkedItems[i]=false;
            i=i+1;

        }
        builder.setItems(names,null);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //Bind camera and preview view
    private void cameraBind()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView=findViewById(R.id.previewView);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {

            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {

                InputImage image = null;


                @SuppressLint("UnsafeExperimentalUsageError")
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                    //Log.d("svm","Rotation "+imageProxy.getImageInfo().getRotationDegrees());
                }

                //Log.d("svm","ANALYSIS");

                //Process acquired image to detect faces
                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {

                                                if(faces.size()!=0) {
                                                    Face face = faces.get(0); //Get first face from detected faces
                                                    //Log.d("svm",""+face);

                                                    //mediaImage to Bitmap
                                                    Bitmap frame_bmp = toBitmap(mediaImage);

                                                    int rot = imageProxy.getImageInfo().getRotationDegrees();

                                                    //Adjust orientation of Face
                                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);



                                                    //Get bounding box of face
                                                    RectF boundingBox = new RectF(face.getBoundingBox());

                                                    //Crop out bounding box from whole Bitmap(image)
                                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                                    if(flipX)
                                                        cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
                                                    //Scale the acquired Face to 112*112 which is required input for model
                                                    Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

//                                                    if(start)
                                                    if(start )
                                                        recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
                                                    //Log.d("svm",""+boundingBox);
                                                    try {
                                                        Thread.sleep(10);  //Camera preview refreshed every 10 millisec(adjust as required)
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else
                                                {
                                                    if(registered.isEmpty()) {
                                                        reco_name.setText("Admin Has Not Registered Any User Yet At Your Company.Please Contact Your Admin For Further Details.");
                                                    }
                                                    else {
                                                        reco_name.setText("No Face Detected!");
                                                        tempConformNameText=null;
                                                    }
                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        })
                                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Face>> task) {

                                        imageProxy.close(); //v.important to acquire next frame for analysis
                                    }
                                });


            }
        });


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);


    }

    public void recognizeImage(final Bitmap bitmap) {

        // set Face to Preview
//        face_preview.setImageBitmap(bitmap);

        //Create ByteBuffer to store normalized image

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable

        outputMap.put(0, embeedings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model



        float distance = Float.MAX_VALUE;
        String id = "0";
        String label = "?";

        //Compare new face with saved Faces.
        if (registered.size() > 0) {

            final Pair<String, Float> nearest = findNearest(embeedings[0]);//Find closest matching face

            if (nearest != null) {

                final String id_emp = nearest.first;
                label = id_emp;
                distance = nearest.second;
                if(distance<1.000f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    String name=db.getEmployeeNameForParicularEmployeeId(id_emp);
                    reco_name.setText("name : "+name+"\nid : "+id_emp);
                    tempEmpId=id_emp;

                    tempConformNameText=name;
                }
                else {
                    reco_name.setText("Unknown");
                    tempConformNameText="null";
                }
                ////Log.d("svm","nearest: " + name + " - distance: " + distance);


            }
        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );

    }
//    public void register(String name, SimilarityClassifier.Recognition rec) {
//        registered.put(name, rec);
//    }

    //Compare Faces by distance between face embeddings
    private Pair<String, Float> findNearest(float[] emb) {

        Pair<String, Float> ret = null;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {

            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                ret = new Pair<>(name, distance);
            }
        }

        return ret;

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(//from  w w  w. ja v  a  2s. c  om
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }

    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    //Save Faces to Shared Preferences.Conversion of Recognition objects to json string
    private void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap,boolean clear) {
        if(clear)
            jsonMap.clear();
        else
            jsonMap.putAll(readFromSP());
        String jsonString = new Gson().toJson(jsonMap);
//        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply();
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show();
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String, SimilarityClassifier.Recognition> readFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        String json=sharedPreferences.getString("map",defValue);
        // System.out.println("Output json"+json.toString());
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
        // System.out.println("Output map"+retrievedMap.toString());

        //During type conversion and save/load procedure,format changes(eg float converted to double).
        //So embeddings need to be extracted from it in required format(eg.double to float).
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
        {
            float[][] output=new float[1][OUTPUT_SIZE];
            ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);

            //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );

        }
//        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
//        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show();
        return retrievedMap;
    }

    //Load Photo from phone storage
    private void loadphoto()
    {
        start=false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    //Similar Analyzing Procedure
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {

                            if(faces.size()!=0) {
//                                recognize.setText("Recognize");
//                                add_face.setVisibility(View.VISIBLE);
                                reco_name.setVisibility(View.INVISIBLE);
//                                face_preview.setVisibility(View.VISIBLE);
                                preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.");
                                Face face = faces.get(0);
                                ////Log.d("svm",""+face);

                                //write code to recreate bitmap from source
                                //Write code to show bitmap to canvas

                                Bitmap frame_bmp= null;
                                try {
                                    frame_bmp = getBitmapFromUri(selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                                //face_preview.setImageBitmap(frame_bmp1);


                                RectF boundingBox = new RectF(face.getBoundingBox());


                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
                                // face_preview.setImageBitmap(scaled);

                                recognizeImage(scaled);
                                addFace();
                                ////Log.d("svm",""+boundingBox);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    face_preview.setImageBitmap(getBitmapFromUri(selectedImageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
