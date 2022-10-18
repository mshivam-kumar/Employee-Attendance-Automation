package com.attendance.ai.smart.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.attendance.ai.smart.EmployeeDetails;
import com.attendance.ai.smart.params.Params;

import java.io.File;
import java.util.ArrayList;

public class UserDbHandlerMonthYrConfigs  extends SQLiteOpenHelper {
    public Context context;

    public UserDbHandlerMonthYrConfigs(Context context)
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

//    public void createEmployeesStoringMonthYrTable(SQLiteDatabase db,String TABLE_NAME)//Main table to store value data of customers
//    {
//        try {
//
//            String create = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
//                    Params.MONTH_YR_NAME + " VARCHAR(20) PRIMARY KEY NOT  NULL , " +
//                    Params.TOTAL_GRACES + " VARCHAR(2) NOT NULL  "+ ")";
//
//            //Log.d("dbsvm", "Query being run : " + create);
//            db.execSQL(create);
////            //Log.d("dbsvm", " Main contacts_db Table created");
//
//        }
//        catch (Exception e)
//        {
//            //Log.d("dbsvm",e.getMessage().toString());
//        }
//
//    }


    public void createEmployeesMonthYrStoringTable(SQLiteDatabase db)//Main table to store value data of customers
    {
        try {

            String create = "CREATE TABLE IF NOT EXISTS " + Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + "(" +
                    Params.MONTH_YR_NAME + " VARCHAR(20) PRIMARY KEY NOT  NULL , " +
                    Params.TOTAL_GRACES + " VARCHAR(2) NOT NULL  "+ ")";

            Log.d("dbsvm", "Query being run : " + create);
            db.execSQL(create);
            Log.d("dbsvm", "Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN Table created");

        }
        catch (Exception e)
        {
            Log.d("dbsvm","Unable to create table : "+e.getMessage().toString());
        }

    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void insertEmployeeDetails(EmployeeDetails emp)
//    {
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues values = new ContentValues();
//
//            values.put(Params.EMP_ID,emp.getID());
//            values.put(Params.EMP_NAME,emp.getName());
//            values.put(Params.EMP_GENDER,emp.getGender());
//            values.put(Params.EMP_DESIGNATION,emp.getDesignation());
//
//
//
//            db.insert(Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN, null, values);
//
//            Log.d("svm","id :"+emp.getID()+" name : "+emp.getName()+" gender : "+emp.getGender()+"" +
//                    "\n Designation : "+emp.getDesignation());
//            Log.d("svm", "Above data successfully inserted");
//            db.close();
//
//        }
//        catch (Exception e)
//        {
//            Log.d("svm","Unable to insert employee details in admin database \nError :"+e.getMessage());
//        }
////
//    }








    public boolean isEmployeeIdAlreadyExists(String id) {

        SQLiteDatabase db = getWritableDatabase();

        boolean flag=false;//does not exist

        try {
            String selectString = "SELECT * FROM " + Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";

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

        String selectString = "SELECT "+Params.EMP_NAME+" FROM " + Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";

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


    //insert new month year
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertMontYr(String monthName,String tGraces)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.MONTH_YR_NAME,monthName);
            values.put(Params.TOTAL_GRACES,tGraces);



            db.insert(Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN, null, values);

            Log.d("svm","Table Name : "+Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN+"MONTH_NAME :"+monthName+" tGraces : "+tGraces);
            Log.d("svm", "Above data successfully inserted");
            db.close();

        }
        catch (Exception e)
        {
            Log.d("svm","Unable to insert employee details in admin database \nError :"+e.getMessage());
        }
//
    }


    public ArrayList<String>  getSavedMonthYr()
    {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> savedMonthYrArrayList=new ArrayList<>();


//        String selectString = "SELECT "+Params.MONTH_YR_NAME+" FROM " +Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN + " WHERE " + Params.EMP_ID + " =?";
        String selectString = "SELECT "+Params.MONTH_YR_NAME+" FROM " +Params.EMPLOYEE_MONTH_YR_STORING_TABLE_BY_ADMIN ;

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



}
