package com.attendance.ai.smart;


import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.attendance.ai.smart.data.AdminDbHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class EmployeesAttendanceSettings extends AppCompatActivity {

    EditText editTextMinEntryTime,editTextMinExitTime,editTextTotalNumOfGraces,editTextGracesTime,editTextMinHalfDayExitTime;
    EditText editTextTotalNumOf1HrLeaves;

     String minEntryTime,minExitTime,totalNumOfMonthlyGraces,graceTime,minHalfDayExitTime;
     ArrayList<String> oneHrLeavetimingsArrayList;
     String totalNumOfMonthlyOneHrLeaves,graceForOneHrLeaves;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_attendance_settings);


        editTextMinEntryTime=findViewById(R.id.editTextMinEntrytime);
        editTextMinExitTime=findViewById(R.id.editTextMinExittime);
        editTextTotalNumOfGraces=findViewById(R.id.editTextTNumGraces);
        editTextGracesTime=findViewById(R.id.editTextGraceTimeInMins);
        editTextMinHalfDayExitTime=findViewById(R.id.editTextminHalfDayExitTime);




        editTextTotalNumOf1HrLeaves=findViewById(R.id.editTextTNum1HrLeaves);
//        editTextGraceForOneHrLeave=findViewById(R.id.editTextGraceFor1HrLeave);

        TextView textViewMinEntryTime=findViewById(R.id.editTextMinEntrytime);


        try {
            showAttendanceSettingsDataIntoTextBoxes();
        }
        catch (Exception e)
        {
            Log.d("svm","unable to show attendance data into text boxes error : "+e.getMessage());
        }

        Button btnChooseEntrytime=findViewById(R.id.btnChooseEntryTime);

        btnChooseEntrytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                timepicker.setEnabled(true);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EmployeesAttendanceSettings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        textViewMinEntryTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        TextView textViewMinExitTime=findViewById(R.id.editTextMinExittime);
        Button btnChooseExittime=findViewById(R.id.btnChooseExitTime);
        btnChooseExittime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                timepicker.setEnabled(true);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EmployeesAttendanceSettings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        textViewMinExitTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        AdminDbHandler db=new AdminDbHandler(EmployeesAttendanceSettings.this);
        SQLiteDatabase db1=db.getWritableDatabase();
        db.createEmployeesAttendanceSettingsStoringTable(db1);//create table

        Button saveAttendanceSetting=findViewById(R.id.btnSaveAttendanceSettings);
        saveAttendanceSetting.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


        try {
            AttendanceSettings as=getDataFromTextBoxes();

            if(!db.isAttendanceSettingsDataExist())//table is empty then insert for first time
            {
                // Default attendance settings are inserted in UserSetupForFaceRecog , so control will not come here
                //If want to change default settings then change the values inserted for default data in UserSetupForFaceRecog
                db.insertAttendanceSettings(as);
                Log.d("svm","data inserted for first time in attendance settings table");
            }
            else //table not empty then only update the table
            {
                db.updateEmployeeAttendanceSettings(as);
                Log.d("svm","attendance settings table updated");
            }

            Log.d("svm",as.getMinEntryTime()+" "+as.getMinExitTime()+" "+as.getTotalNumOfMonthlyGraces()+"" +
                    " "+as.getGraceTime()+" "+minHalfDayExitTime);
//            Log.d("svm",as.getOneHrLeaveFirstTiming()+" "+as.getOneHrLeaveSecondTiming()+" "+as.getOneHrLeaveThirdTiming());

            Log.d("svm",as.getTotalNumOfMonthlyOneHrLeaves()+" "+as.getGraceForOneHrLeaves());
            Toast.makeText(getApplicationContext(),"Settings Saved Successfully",Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Unable To Save Settings",Toast.LENGTH_SHORT).show();

            Log.d("svm","Unable to show attendance settings from text boxes error : "+e.getMessage());
        }

            }
        });

    }


    public AttendanceSettings getDataFromTextBoxes()
   {
       oneHrLeavetimingsArrayList=new ArrayList<>();//initializing instance variable arraylist

       AttendanceSettings as=null;
       minEntryTime=editTextMinEntryTime.getText().toString();
       minExitTime=editTextMinExitTime.getText().toString();
       totalNumOfMonthlyGraces=editTextTotalNumOfGraces.getText().toString();
       graceTime=editTextGracesTime.getText().toString();
       minHalfDayExitTime=editTextMinHalfDayExitTime.getText().toString();



       totalNumOfMonthlyOneHrLeaves=editTextTotalNumOf1HrLeaves.getText().toString();
//       graceForOneHrLeaves=editTextGraceForOneHrLeave.getText().toString();

       as=new AttendanceSettings(minEntryTime,minExitTime,totalNumOfMonthlyGraces,graceTime,minHalfDayExitTime,
               totalNumOfMonthlyOneHrLeaves);

       return as;
   }

    private void showAttendanceSettingsDataIntoTextBoxes() {
        AdminDbHandler db=new AdminDbHandler(EmployeesAttendanceSettings.this);
        AttendanceSettings as=db.getAttendanceSettings();
        editTextMinEntryTime.setText(as.getMinEntryTime());
        editTextMinExitTime.setText(as.getMinExitTime());
        editTextTotalNumOfGraces.setText(as.getTotalNumOfMonthlyGraces());
        editTextGracesTime.setText(as.getGraceTime());
        editTextMinHalfDayExitTime.setText(as.getMinHalfDayExitTime());


        editTextTotalNumOf1HrLeaves.setText(as.getTotalNumOfMonthlyOneHrLeaves());

//        editTextGraceForOneHrLeave.setText(as.getGraceForOneHrLeaves());

    }

}
