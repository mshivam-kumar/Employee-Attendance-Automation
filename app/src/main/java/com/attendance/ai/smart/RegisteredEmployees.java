package com.attendance.ai.smart;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.attendance.ai.smart.adapter.RecyclerViewAdapter;
import com.attendance.ai.smart.data.AdminDbHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RegisteredEmployees extends AppCompatActivity {
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.employees_attendance_records);

        recyclerView=findViewById(R.id.homeRecyclerView);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Stop control go to recycler view after some change made in recycler view
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        try {
            showAllEmployeesIds();
        }
        catch (Exception e)
        {
            Log.d("svm","Unable to show empId in recycler view Error : "+e.getMessage());
        }
//

    }

    public void showAllEmployeesIds()
    {
//        Params.DB_NAME=Params.DEFAULT_DB_NAME;
//        Params.Curent_Running_Db=Params.DB_NAME;
        ArrayList<String > employeesIdArrayList = new ArrayList<>();
//        ListView mylistView=findViewById(R.id.listView);

        //the constructor so ,DB_NAME change will only take place if constructor is called after changing name
        //Storing all new database names int the same db, to fetch all later
        AdminDbHandler db=new AdminDbHandler(RegisteredEmployees.this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            employeesIdArrayList = db.getAllEmployeesIdOnlyFromAdmin();

            for(String id: employeesIdArrayList)
            {
                Log.d("svm","id : "+id);
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
                recyclerViewAdapter = new RecyclerViewAdapter(RegisteredEmployees.this,a );
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
