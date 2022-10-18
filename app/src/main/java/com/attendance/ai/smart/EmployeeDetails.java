package com.attendance.ai.smart;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EmployeeDetails {

    //today's data
    public static Date DATE1=Date.from(Instant.now());
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");//current date

    public static String todayDate = formatter.format(DATE1);

    //Instances for Employee
    private String date;
    private String entryTime;
    private String exitTime;
    private String totalHoursServed;
    private String halfFullDayStatus;
    private String attendanceStatus;

    //Instances for admin
    private String ID;
    private String name;
    private String gender;
    private String designation;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }


    //Constructor for Admin
    public EmployeeDetails(String ID, String name, String gender, String designation) {
        this.ID = ID;
        this.name = name;
        this.gender = gender;
        this.designation = designation;
    }



    //Constructor for Employee
    public EmployeeDetails(String date, String entryTime, String exitTime, String totalHoursServed,String halfFullDayStatus, String attendanceStatus) {
        this.date = date;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalHoursServed = totalHoursServed;
        this.halfFullDayStatus=halfFullDayStatus;
        this.attendanceStatus = attendanceStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public String getTotalHoursServed() {
        return totalHoursServed;
    }

    public void setTotalHoursServed(String totalHoursServed) {
        this.totalHoursServed = totalHoursServed;
    }

    public String getHalfFullDayStatus() {
        return halfFullDayStatus;
    }


    public void setHalfFullDayStatus(String halfFullDayStatus) {
        this.halfFullDayStatus = halfFullDayStatus;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }


    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}
