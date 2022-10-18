package com.attendance.ai.smart.params;

public class Params {

    public static final int DB_VERSION = 1;

    //Admin database
    public static final String ADMIN_DATABASE = "admin_database";
    //Admin database storing table
    public static final String EMPLOYEE_STORING_TABLE_BY_ADMIN = "employee_storing_table";
    //Admin database storing table attributes
    public static final String EMP_ID = "emp_id";
    public static final String EMP_NAME = "emp_name";
    public static final String EMP_GENDER = "emp_gender";
    public static final String EMP_DESIGNATION = "emp_designation";


    //For storing employee attendance data;
    //Attendance storing table will be with employee id
    //Attributes of employee attendance table
    public static final String DATE = "date";
    public static final String ENTRY_TIME = "entry_time";
    public static final String EXIT_TIME = "exit_time";
    public static final String TOTAL_HOURS_SERVED = "total_hours_served";
    public static final String HALF_FULL_DAY_STATUS = "half_full_day_status";
    public static final String ATTENDANCE_STATUS = "attendance_status";//P or A

    public static String CURRENT_PRESSED_EMP_ID="";


//    for UserDbHandlerMonthYrConfigs.java
    //below table name is temporarliy , it will be replaced later
    public static final String  EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN="employee_month_yr_storing_table";
    public static final String MONTH_YR_NAME="month_name";
    public static final String  TOTAL_GRACES="total_graces";
    public static final String  TOTAL_1HR_GRACES="total_1hr_graces";
    public static String CURRENT_PRESSED_MONTH_YR="";


    //Employee Attendance Settings
    public static final String EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN="employee_attendance_settings_table";
    public static final String MIN_ENTRY_TIME="min_entry_time";
    public static final String MIN_EXIT_TIME="min_exit_time";
    public static final String TOTAL_NUM_OF_MONTHLY_GRACES="total_num_of_monthly_graces";
    public static final String GRACE_TIME="grace_time";
    public static final String MIN_HALF_DAY_EXIT_TIME="min_half_day_exit_time";
    public static final String ONE_HR_LEAVE_FIRST_TIMING="one_hr_leave_first_timing";
    public static final String ONE_HR_LEAVE_SECOND_TIMING="one_hr_leave_second_timing";
    public static final String ONE_HR_LEAVE_THIRD_TIMING="one_hr_leave_third_timing";
    public static final String TOTAL_NUM_OF_MONTHLY_ONE_HR_LEAVES="total_num_of_monthly_one_hr_leaves";
    public static final String GRACE_FOR_ONE_HR_LEAVES="grace_for_one_hr_leaves";




}