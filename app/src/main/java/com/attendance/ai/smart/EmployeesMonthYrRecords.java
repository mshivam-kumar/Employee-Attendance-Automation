package com.attendance.ai.smart;


import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.attendance.ai.smart.adapter.RecyclerViewAdapter;
import com.attendance.ai.smart.data.AdminDbHandler;
import com.attendance.ai.smart.data.AdminDbHandler;
import com.attendance.ai.smart.params.Params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class EmployeesMonthYrRecords extends AppCompatActivity {
    public androidx.recyclerview.widget.RecyclerView recyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;
    public static ArrayList<String>a;

    @Override
    protected void onRestart() {//With back button reload this activity to update changes in group list
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.employees_attendance_records);

        recyclerView=findViewById(R.id.homeRecyclerView);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Stop control go to recycler view after some change made in recycler view
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        AdminDbHandler db=new AdminDbHandler(EmployeesMonthYrRecords.this);
        SQLiteDatabase db1=db.getWritableDatabase();
//        db.createEmployeesStoringTable(db1);

        try {
            showAllEmployeesIds();
        }
        catch (Exception e)
        {
            Log.d("svm","Unable to show empId in recycler view Error : "+e.getMessage());
        }
//

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showAllEmployeesIds()
    {
//        Params.DB_NAME=Params.DEFAULT_DB_NAME;
//        Params.Curent_Running_Db=Params.DB_NAME;
        ArrayList<String > employeesIdArrayList = new ArrayList<>();
//        ListView mylistView=findViewById(R.id.listView);

        //the constructor so ,DB_NAME change will only take place if constructor is called after changing name
        //Storing all new database names int the same db, to fetch all later
//        AdminDbHandler db=new AdminDbHandler(EmployeesMonthYrRecords.this);
        AdminDbHandler db=new AdminDbHandler(EmployeesMonthYrRecords.this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //saved month yr records
            ArrayList<ArrayList<String>> aAL=db.getSavedMonthYrDATAFromTable(Params.CURRENT_PRESSED_EMP_ID+"month_yr");
            Log.d("svm","Saved monthYr records of employee");
            try {
                for (ArrayList<String> aL : aAL) {
                    Log.d("svm", "month Yr :" + aL.get(0) + " total monthly graces : " + aL.get(1) + " total monthly " +
                            "1 Hr graces : " + aL.get(2));
                }
            }
            catch (Exception e)
            {
                Log.d("svm","Unable to show month yr saved data error : "+e.getMessage());
            }

//            db.insertMontYrIntoTable(Params.CURRENT_PRESSED_EMP_ID+"month_yr","April 2022","0");
            employeesIdArrayList = db.getSavedMonthYrFromTable(Params.CURRENT_PRESSED_EMP_ID+"month_yr");


            for(String id: employeesIdArrayList)
            {
                Log.d("svm","inside EmployeesMonthYrRecords  month_yr_name : "+id);
            }

        }
        if(employeesIdArrayList.size()==0)
        {
//            customerDatabasesArrayList.add("No group found.\nCreate a new group to insert data");
            TextView noGroupFound=findViewById(R.id.ifNoGroupFound);
            noGroupFound.setText("No record found.\nCreate a new group to insert data");
//            customerDatabasesArrayList.add("");
        }

        if(employeesIdArrayList.size()!=0) {

            try {
                a=employeesIdArrayList;
                Collections.sort(a);//Show month year configuration in ascending order
                recyclerViewAdapter = new RecyclerViewAdapter(EmployeesMonthYrRecords.this,a );
                recyclerView.setAdapter(recyclerViewAdapter);
            }
            catch (Exception e)
            {
                //Log.d("dbsvm","Inside HOME" +
//                        "\nError : "+e.getMessage().toString());
            }


        }

    }

}
