package com.attendance.ai.smart.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.attendance.ai.smart.AttendanceSettings;
import com.attendance.ai.smart.EmployeeDetails;
import com.attendance.ai.smart.params.Params;

import java.io.File;
import java.util.ArrayList;

public class AdminDbHandler extends SQLiteOpenHelper {
    public static SQLiteDatabase db1;
    public Context context;

    //    Context context;
    public AdminDbHandler(Context context)
    {



//        super(context, Params.DATATABSE_DIRECTORY_NAME,null,Params.DB_VERSION);
//                + File.separator + "/."+Params.EmailId+"/"+ Params.DB_NAME

        super(context, context.getExternalFilesDir(null).getAbsolutePath()
                + File.separator+"/."+ Params.ADMIN_DATABASE
                + File.separator
                + Params.ADMIN_DATABASE,null,Params.DB_VERSION);

        this.context=context;

        Log.d("svm","Admin database location : "+ Params.ADMIN_DATABASE
                + File.separator
                + Params.ADMIN_DATABASE);
        //Log.d("dbsvm","\nGroup  location : "+context.getExternalFilesDir(null).getAbsolutePath()

//                + File.separator + "/."+Params.EmailId+"/"+Params.DB_NAME
//                + File.separator
//                + Params.DB_NAME);
        //For default data
//            super(context, Params.DEFAULT_DATA_DB_NAME, null, Params.DEFAULT_DATA_DB_VERSION);

//        //Log.d("dbsvm","inside MyDbHandler");
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    // to create admin database table for storing employee details
    public void createEmployeesStoringTable(SQLiteDatabase db)//Main table to store value data of customers
    {
        try {

            String create = "CREATE TABLE IF NOT EXISTS " + Params.EMPLOYEE_STORING_TABLE_BY_ADMIN + "(" +
                    Params.EMP_ID + " VARCHAR(10) PRIMARY KEY NOT  NULL , " +
                    Params.EMP_NAME + " VARCHAR(15) NOT NULL, " + Params.EMP_GENDER + " VARCHAR(10) NOT NULL , " +
                    Params.EMP_DESIGNATION + " VARCHAR(20) NOT NULL  "+ ")";

            //Log.d("dbsvm", "Query being run : " + create);
            db.execSQL(create);
//            //Log.d("dbsvm", " Main contacts_db Table created");

        }
        catch (Exception e)
        {
            //Log.d("dbsvm",e.getMessage().toString());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertEmployeeDetails(EmployeeDetails emp)
    {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(Params.EMP_ID,emp.getID());
                values.put(Params.EMP_NAME,emp.getName());
                values.put(Params.EMP_GENDER,emp.getGender());
                values.put(Params.EMP_DESIGNATION,emp.getDesignation());



                db.insert(Params.EMPLOYEE_STORING_TABLE_BY_ADMIN, null, values);

                Log.d("svm","id :"+emp.getID()+" name : "+emp.getName()+" gender : "+emp.getGender()+"" +
                        "\n Designation : "+emp.getDesignation());
                Log.d("svm", "Above data successfully inserted");
                db.close();

            }
            catch (Exception e)
            {
                Log.d("svm","Unable to insert employee details in admin database \nError :"+e.getMessage());
            }
//
    }








    public boolean isEmployeeIdAlreadyExists(String id) {

        SQLiteDatabase db = getWritableDatabase();

        boolean flag=false;//does not exist

        try {
            String selectString = "SELECT * FROM " + Params.EMPLOYEE_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[]{id});

//        boolean hasObject = false;

            if (cursor.moveToFirst()) {
//            hasObject = true;
                flag = true;
                //region if you had multiple records to check for, use this region.

                int count = 0;
                while (cursor.moveToNext()) {
                    count++;
                }
                //here, count is records found
                //Log.d("dbsvm", String.format("%d records found", count));

                //endregion

            }

        cursor.close();          // Dont forget to close your cursor
        }
        catch (Exception e)
        {
            Log.d("svm","Unable to check whether employee id exists or not \nError : "+e.getMessage());
        }

        db.close();              //AND your Database!

        return flag;
    }


    public String  getEmployeeNameForParicularEmployeeId(String id)
    {
        SQLiteDatabase db = getWritableDatabase();

        String selectString = "SELECT "+Params.EMP_NAME+" FROM " + Params.EMPLOYEE_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
                Cursor cursor = db.rawQuery(selectString, new String[]{id});
        String name="";
        try {

            if(cursor.moveToFirst()) {

                name = cursor.getString(0);
            }
            cursor.close();          // Dont forget to close your cursor
        }catch (Exception  e){
            Log.d("svm","Unable to get employee name from id \nError : "+e.getMessage());
        }


        db.close();              //AND your Database!
        return name;
    }

    public ArrayList<String>  getEmployeeDetailsForParicularEmployeeId(String id)
    {
        SQLiteDatabase db = getWritableDatabase();

        String selectString = "SELECT * FROM " + Params.EMPLOYEE_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        ArrayList<String> empDetailsArrayList=new ArrayList<>();
        Cursor cursor = db.rawQuery(selectString, new String[]{id});
        try {

            String id1="",name="",gender="",desig="";
            //Loop through now
            while(cursor.moveToNext()){
                id1=cursor.getString(0);
                name=cursor.getString(1);
                gender=cursor.getString(2);
                desig=cursor.getString(3);

                empDetailsArrayList.add(id1);
                empDetailsArrayList.add(name);
                empDetailsArrayList.add(gender);
                empDetailsArrayList.add(desig);
//                emp=new EmployeeDetails(id,name,gender,desig);


            }
            cursor.close();          // Dont forget to close your cursor
            db.close();              //AND your Database!
        }catch (Exception  e){
            Log.d("svm","Unable to get employee name from id \nError : "+e.getMessage());
        }
        return empDetailsArrayList;
    }



    public String getTodayExitTime(String tableName,String date) {

        SQLiteDatabase db = getWritableDatabase();
        boolean flag=false;//does not exist
        String exitTime="";
        try {
            String selectString = "SELECT " + Params.EXIT_TIME + " FROM " + tableName + " WHERE " + Params.DATE + " =?";

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[]{date});

//        boolean hasObject = false;
            if (cursor.moveToFirst()) {
//            hasObject = true;
                exitTime = cursor.getString(0);
                if(exitTime.length()>0)
                {
                    flag = true;

                }
                //region if you had multiple records to check for, use this region.

//                int count = 0;
//                while (cursor.moveToNext()) {
//                    count++;
//                }
                //here, count is records found
                //Log.d("dbsvm", String.format("%d records found", count));

                //endregion

            }

            cursor.close();          // Dont forget to close your cursor
            db.close();              //AND your Database!
            if(flag)
            {
                Log.d("svm", "Exit time exist with date : " + date + " time : "+exitTime);

            }
            else {
                Log.d("svm", "exit time of attendance with date  " + date + " does not exist");
            }

        }
        catch (Exception e)
        {
            Log.d("svm","Inside isEmployeeAttendanceWithTodayDATEExitTimeExist Error : "+e.getMessage());
        }

        if(flag)
        {
            return exitTime;
        }
        return "";
    }


    public boolean isExitTimeExist(String tableName,String date) {

        SQLiteDatabase db = getWritableDatabase();
        boolean flag=false;//does not exist
        String exitTime="";
        try {
            String selectString = "SELECT " + Params.EXIT_TIME + " FROM " + tableName + " WHERE " + Params.DATE + " =?";

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[]{date});

//        boolean hasObject = false;
            if (cursor.moveToFirst()) {
//            hasObject = true;
                exitTime = cursor.getString(0);
                if(exitTime.length()>0)
                {
                    flag = true;

                }
                //region if you had multiple records to check for, use this region.

//                int count = 0;
//                while (cursor.moveToNext()) {
//                    count++;
//                }
                //here, count is records found
                //Log.d("dbsvm", String.format("%d records found", count));

                //endregion

            }

            cursor.close();          // Dont forget to close your cursor
            db.close();              //AND your Database!
            if(flag)
            {
                Log.d("svm", "Exit time exist with date : " + date + " time : "+exitTime);

            }
            else {
                Log.d("svm", "exit time of attendance with date  " + date + " does not exist");
            }

        }
        catch (Exception e)
        {
            Log.d("svm","Inside isEmployeeAttendanceWithTodayDATEExitTimeExist Error : "+e.getMessage());
        }
        return flag;
    }

    public ArrayList<String>  getSavedMonthYrFromTable(String monthYrTableName)
    {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> savedMonthYrArrayList=new ArrayList<>();


//        String selectString = "SELECT "+Params.MONTH_YR_NAME+" FROM " +Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";
        String selectString = "SELECT "+Params.MONTH_YR_NAME+" FROM " +monthYrTableName ;

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString,null);
        String name="";
        try {

            while(cursor.moveToNext()) {

                name = cursor.getString(0);
                savedMonthYrArrayList.add(name);
            }
            cursor.close();          // Dont forget to close your cursor
        }catch (Exception  e){
            Log.d("svm","Unable to get employee name from id \nError : "+e.getMessage());
        }


        db.close();              //AND your Database!
        return savedMonthYrArrayList;
    }


    public ArrayList<ArrayList<String>> getSavedMonthYrDATAFromTable(String monthYrTableName)
    {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> savedMonthYrArrayList=new ArrayList<>();
        ArrayList<ArrayList<String>> aAL=new ArrayList<>();

//        String selectString = "SELECT "+Params.MONTH_YR_NAME+" FROM " +Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";
        String selectString = "SELECT * FROM " +monthYrTableName ;

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString,null);
        String name="",tMGraces,tM1HrGraces;
        try {

            while(cursor.moveToNext()) {

                savedMonthYrArrayList.clear();
                name = cursor.getString(0);
                tMGraces = cursor.getString(1);
                tM1HrGraces = cursor.getString(2);
                savedMonthYrArrayList.add(name);
                savedMonthYrArrayList.add(tMGraces);
                savedMonthYrArrayList.add(tM1HrGraces);
                aAL.add(savedMonthYrArrayList);
            }
            cursor.close();          // Dont forget to close your cursor
        }catch (Exception  e){
            Log.d("svm","Unable to get employee name from id \nError : "+e.getMessage());
        }


        db.close();              //AND your Database!
        return aAL;
    }




    public void updateEmployeeExitTimeToNULL(String tableName,String currentAccessedMonthYr)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.EXIT_TIME,"");

            db.update(tableName, values, Params.MONTH_YR_NAME + "=?" ,
                    new String[]{String.valueOf(currentAccessedMonthYr)});

//            db.execSQL("Attach "+Params.DB_NAME+" AS "+" newDbName;");

//            db.insert(Params.TABLE_NAME, null, values);
            //Log.d("dbsvm", "ConfigureCustomerGroupDataWithMonthAndYearSuccessfully updated");
            Log.d("svm","exit time set to null :"+"\"\""+" successfully");
            db.close();
        }
        catch (Exception e)
        {
            Log.d("dbsvm","Unbale to update total new graces in monthYr table, Error: "+e.getMessage().toString());
        }
    }


    public void updateEmployeeMonthYrGraces(String tableName,String currentAccessedMonthYr,String totalNewGraces)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.TOTAL_GRACES,totalNewGraces);

                db.update(tableName, values, Params.MONTH_YR_NAME + "=?" ,
                        new String[]{String.valueOf(currentAccessedMonthYr)});

//            db.execSQL("Attach "+Params.DB_NAME+" AS "+" newDbName;");

//            db.insert(Params.TABLE_NAME, null, values);
            //Log.d("dbsvm", "ConfigureCustomerGroupDataWithMonthAndYearSuccessfully updated");
            Log.d("svm","New grace :"+totalNewGraces+" updated successfully");
            db.close();
        }
        catch (Exception e)
        {
            Log.d("dbsvm","Unbale to update total new graces in monthYr table, Error: "+e.getMessage().toString());
        }
    }

    public void updateEmployeeMonthYrOneHrGraces(String tableName,String currentAccessedMonthYr,String totalNewGraces)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.TOTAL_1HR_GRACES,totalNewGraces);

            db.update(tableName, values, Params.MONTH_YR_NAME + "=?" ,
                    new String[]{String.valueOf(currentAccessedMonthYr)});

//            db.execSQL("Attach "+Params.DB_NAME+" AS "+" newDbName;");

//            db.insert(Params.TABLE_NAME, null, values);
            //Log.d("dbsvm", "ConfigureCustomerGroupDataWithMonthAndYearSuccessfully updated");
            Log.d("svm","New grace :"+totalNewGraces+" updated successfully");
            db.close();
        }
        catch (Exception e)
        {
            Log.d("dbsvm","Unbale to update total new graces in monthYr table, Error: "+e.getMessage().toString());
        }
    }


    //create attendance record storing table .. entry time , exit time etc.
//    public void createEmployeesAttendanceRecordStoringTable(SQLiteDatabase db,String attendanceRecordTableName)//Main table to store value data of customers
//    {
//        try {
//
//            String create = "CREATE TABLE IF NOT EXISTS " + attendanceRecordTableName + "(" +
//                    Params.DATE + " VARCHAR(10) PRIMARY KEY  , " +
//                    Params.ENTRY_TIME + " VARCHAR(10) NOT NULL, " + Params.EXIT_TIME + " VARCHAR(10) NOT NULL , " +
//                    Params.TOTAL_HOURS_SERVED+ " VARCHAR(10) NOT  NULL , " +
//                    Params.ATTENDANCE_STATUS + " VARCHAR(8) NOT NULL  "+ ")";//absent or present
//            Log.d("dbsvm", "Query being run : " + create);
//            db.execSQL(create);
//            Log.d("dbsvm", "Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN Table created");
//
//        }
//        catch (Exception e)
//        {
//            Log.d("dbsvm","Unable to create table : "+e.getMessage().toString());
//        }
//
//    }

    public boolean isEmployeeCurrentMonthYrExist(String tableName,String monthYr) {

        SQLiteDatabase db = getWritableDatabase();
        boolean flag=false;//does not exist
        try {
            String selectString = "SELECT * FROM " + tableName + " WHERE " + Params.DATE + " =?";

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[]{monthYr});

//        boolean hasObject = false;
            if (cursor.moveToFirst()) {
//            hasObject = true;
                flag = true;//exist
                //region if you had multiple records to check for, use this region.

                int count = 0;
                while (cursor.moveToNext()) {
                    count++;
                }
                //here, count is records found
                //Log.d("dbsvm", String.format("%d records found", count));
                Log.d("svm","attendance with date "+monthYr+" exist");

                //endregion
                cursor.close();          // Dont forget to close your cursor
                db.close();              //AND your Database!

            }
            else
            {
                Log.d("svm", "attendance with date " + monthYr + " does not exist");
            }

        }
        catch (Exception e)
        {
            Log.d("svm","Inside isEmployeeAttendanceWithTodayDATEExist Error : "+e.getMessage());
        }
        return flag;
    }


    //Employee Attendance Settings
    public void createEmployeesAttendanceSettingsStoringTable(SQLiteDatabase db)//Main table to store value data of customers
    {
        try {

            String create = "CREATE TABLE IF NOT EXISTS " + Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN + "(" +
                    Params.MIN_ENTRY_TIME + " VARCHAR(5) , " +
                    Params.MIN_EXIT_TIME + " VARCHAR(5) , " +
                    Params.TOTAL_NUM_OF_MONTHLY_GRACES + " VARCHAR(5) , " +
                    Params.GRACE_TIME + " VARCHAR(5) , " +
                    Params.MIN_HALF_DAY_EXIT_TIME + " VARCHAR(5) , " +
                    Params.TOTAL_NUM_OF_MONTHLY_ONE_HR_LEAVES + " VARCHAR(5)  "+ ")";

            //Log.d("dbsvm", "Query being run : " + create);
            db.execSQL(create);
//            //Log.d("dbsvm", " Main contacts_db Table created");

        }
        catch (Exception e)
        {
            //Log.d("dbsvm",e.getMessage().toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertAttendanceSettings(AttendanceSettings as)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.MIN_ENTRY_TIME,as.getMinEntryTime());
            values.put(Params.MIN_EXIT_TIME,as.getMinExitTime());
            values.put(Params.TOTAL_NUM_OF_MONTHLY_GRACES,as.getTotalNumOfMonthlyGraces());
            values.put(Params.GRACE_TIME,as.getGraceTime());
            values.put(Params.MIN_HALF_DAY_EXIT_TIME,as.getMinHalfDayExitTime());


            values.put(Params.TOTAL_NUM_OF_MONTHLY_ONE_HR_LEAVES,as.getTotalNumOfMonthlyOneHrLeaves());
//            values.put(Params.GRACE_FOR_ONE_HR_LEAVES,as.getGraceForOneHrLeaves());



            db.insert(Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN, null, values);

            Log.d("svm","min entry  :"+as.getMinEntryTime()+" exit entry : "+as.getMinExitTime()+
                    "total monthly graces : "+as.getTotalNumOfMonthlyGraces()+"\n" +
                    "grace time :"+as.getGraceTime()+" min half day exit time : "+as.getMinHalfDayExitTime());

            Log.d("svm","total monthly one hr leaves : "+as.getTotalNumOfMonthlyOneHrLeaves()+" grace for one hr : "+as.getGraceForOneHrLeaves());
            Log.d("svm", "Above data successfully inserted");
            db.close();

        }
        catch (Exception e)
        {
            Log.d("svm","Unable to insert attendance settings into database table \nError :"+e.getMessage());
        }
//
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void insertOneHrLeaveTimingAttendanceSettings(String timing)
//    {
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues values = new ContentValues();
//
//
//            values.put(Params.ONE_HR_LEAVE_TIMINGS,timing);
//
//
//
//            db.insert(Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN, null, values);
//
//            Log.d("svm", timing+" inserted successfully");
////            db.close();//do not close database here as it is already open in method insertAttendanceSettings ,
////            from where insertOneHrLeaveTimingAttendanceSettings was called
//
//        }
//        catch (Exception e)
//        {
//            Log.d("svm","Unable to insert employee details in admin database \nError :"+e.getMessage());
//        }
////
//    }

//    public  void deleteWholeOneHrLeaveTimingsData()
//    {
//        try {
//            SQLiteDatabase db=getWritableDatabase();
//            String updateString="UPDATE "+Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN+"  SET "+Params.ONE_HR_LEAVE_TIMINGS+" = Null";
//            Cursor cursor = db.rawQuery(updateString, null);
//
//        }catch (Exception e)
//        {
//            Log.d("svm","error in deleting whole oneHrLeaveTimings column error : "+e.getMessage());
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateEmployeeAttendanceSettings(AttendanceSettings as) {

//        key==1 to update Rate and unit , used inside TableTotalView.java
//        key==0 to udpate values in InsertDbData.java textBoxes other than milk_rate
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {

            values.put(Params.MIN_ENTRY_TIME,as.getMinEntryTime());
            values.put(Params.MIN_EXIT_TIME,as.getMinExitTime());
            values.put(Params.TOTAL_NUM_OF_MONTHLY_GRACES,as.getTotalNumOfMonthlyGraces());
            values.put(Params.GRACE_TIME,as.getGraceTime());
            values.put(Params.MIN_HALF_DAY_EXIT_TIME,as.getMinHalfDayExitTime());


            values.put(Params.TOTAL_NUM_OF_MONTHLY_ONE_HR_LEAVES,as.getTotalNumOfMonthlyOneHrLeaves());
//            values.put(Params.GRACE_FOR_ONE_HR_LEAVES,as.getGraceForOneHrLeaves());



//            db.insert(Params.EMPLOYEE_STORING_TABLE_BY_ADMIN, null, values);
            db.update(Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN, values, null, null);

            Log.d("svm","min entry  :"+as.getMinEntryTime()+" exit entry : "+as.getMinExitTime()+
                    "total monthly graces : "+as.getTotalNumOfMonthlyGraces()+"\n" +
                    "grace time :"+as.getGraceTime()+" min half day exit time : "+as.getMinHalfDayExitTime()+"\n" +
                    "total monthly one hr leaves : "+as.getTotalNumOfMonthlyOneHrLeaves()+" grace for one hr : "+as.getGraceForOneHrLeaves());
            Log.d("svm", "Above data successfully inserted");
            db.close();

            //Log.d("dbsvm", "Default milk rate updated successfully");

        }
        catch (Exception e)
        {
            Log.d("svm","Unable to update employee attendance settings");
        }
    }

    public boolean isAttendanceSettingsDataExist() {

        SQLiteDatabase db = getWritableDatabase();
        boolean flag=false;//does not exist
        String minEntryTime="";
        try {
            String selectString = "SELECT * FROM " +Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN;

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, null);

//        boolean hasObject = false;
            if (cursor.moveToFirst()) {
//            hasObject = true;
                minEntryTime = cursor.getString(0);
//                if(minEntryTime.length()>0)
//                {
                    flag = true;

//                }
                //region if you had multiple records to check for, use this region.

//                int count = 0;
//                while (cursor.moveToNext()) {
//                    count++;
//                }
                //here, count is records found
                //Log.d("dbsvm", String.format("%d records found", count));

                //endregion

            }

            cursor.close();          // Dont forget to close your cursor
            db.close();              //AND your Database!
            if(flag)
            {
                Log.d("svm", "Attendance settings data already already exists");

            }
            else {
                Log.d("svm", "Attendance settings data does not exits");
            }

        }
        catch (Exception e)
        {
            Log.d("svm","Inside isEmployeeAttendanceWithTodayDATEExitTimeExist Error : "+e.getMessage());
        }
        return flag;
    }

    public AttendanceSettings  getAttendanceSettings()
    {
        SQLiteDatabase db = getWritableDatabase();

        String selectString = "SELECT * FROM " + Params.EMPLOYEE_ATTENDANCE_SETTINGS_TABLE_BY_ADMIN ;

        String minEntryTime="",minExitTime="",totalNumOfMonthlyGraces="",graceTime="",minHalfDayExitTime="";
//        String oneHrLeaveFirstTiming="",oneHrLeaveSecondTiming="",oneHrLeaveThirdTiming="";
//         ArrayList<String>oneHrLeavetimingsArrayList=new ArrayList<>();
         String totalNumOfMonthlyOneHrLeaves="";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        AttendanceSettings as = null;
        Cursor cursor = db.rawQuery(selectString, null);
        try {

            //Loop through now
//            while(cursor.moveToNext()){
                if (cursor.moveToFirst()) {

                minEntryTime=cursor.getString(0);
                Log.d("svm","Min entry time "+minEntryTime);
                minExitTime=cursor.getString(1);
                totalNumOfMonthlyGraces=cursor.getString(2);
                graceTime=cursor.getString(3);
                minHalfDayExitTime=cursor.getString(4);


                totalNumOfMonthlyOneHrLeaves=cursor.getString(5);
            }

                as=new AttendanceSettings(minEntryTime,minExitTime,totalNumOfMonthlyGraces,graceTime,minHalfDayExitTime,totalNumOfMonthlyOneHrLeaves);

            cursor.close();          // Dont forget to close your cursor
            db.close();              //AND your Database!
        }catch (Exception  e){
            Log.d("svm","Unable to get employee name from id \nError : "+e.getMessage());
        }
        return as;
    }





}
