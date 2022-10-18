package com.attendance.ai.smart;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.attendance.ai.smart.data.AdminDbHandler;
import com.attendance.ai.smart.params.Params;

import java.util.ArrayList;
import java.util.List;

public class AttendanceView extends AppCompatActivity {
//    public TableLayout table;//To store database table data and used to show table data on screen
    public static TableLayout table;//Made static to use in GeneratePdf.java to generate pdf of data
    //On call method tableView and returning table , null pointer exception is coming

   private static final long TIME_INTERVAL_GAP=500;
    private long lastTimeClicked=System.currentTimeMillis();



        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.attendance_view);


            table = (TableLayout) findViewById(R.id.viewForsetupUpdateInTable);

            TextView empName=findViewById(R.id.employeeName);
            TextView empId=findViewById(R.id.employeeId);
            TextView empGender=findViewById(R.id.empGender);
            TextView empDesignation=findViewById(R.id.empDesignation);

            TextView textViewMonthAccessed=findViewById(R.id.textViewMonthAccessed);
            textViewMonthAccessed.setText(Params.CURRENT_PRESSED_MONTH_YR.toUpperCase().replace("_"," "));
            TextView textViewGracesUsed=findViewById(R.id.textViewGracesUsed);
            TextView textView1HrGraceUsed=findViewById(R.id.textView1HrGracesUsed);
            AdminDbHandler db=new AdminDbHandler(AttendanceView.this);
            try {
                String firstMonthYrLetter=Params.CURRENT_PRESSED_MONTH_YR.substring(0,1).toUpperCase();
                String remMonthYrLetter=Params.CURRENT_PRESSED_MONTH_YR.substring(1);
//                AdminDbHandler db=new AdminDbHandler(AttendanceView.this);
                String tGraces = db.getCurrentAccessedMonthYrGracesFromTable(Params.CURRENT_PRESSED_EMP_ID + "month_yr", firstMonthYrLetter+remMonthYrLetter);
                AttendanceSettings as=db.getAttendanceSettings();

                textViewGracesUsed.setText(as.getGraceTime()+" Min Entry Graces Used : "+tGraces);

                Log.d("svm", "total graces used in month " + Params.CURRENT_PRESSED_MONTH_YR + " is : " + tGraces);

                String t1HrGraces=db.getCurrentAccessedMonthYr1HourGracesFromTable(Params.CURRENT_PRESSED_EMP_ID + "month_yr", firstMonthYrLetter+remMonthYrLetter);
                textView1HrGraceUsed.setText("1 Hr Leaves Used : "+t1HrGraces);
                Log.d("svm", "total 1 hr graces used in month " + Params.CURRENT_PRESSED_MONTH_YR + " is : " + t1HrGraces);

            }catch (Exception e){
                textViewGracesUsed.setText("");
                textView1HrGraceUsed.setText("");
                Log.d("svm","error while retriving total graces error : "+e.getMessage());
            }



//            AdminDbHandler db=new AdminDbHandler(AttendanceView.this);

            String id=Params.CURRENT_PRESSED_EMP_ID.substring(1);

            ArrayList<String> empDetailsArrayList=db.getEmployeeDetailsForParicularEmployeeId(id);
//            String name=db.getEmployeeNameForParicularEmployeeId(id);

            empName.setText(       "Name           :   "+empDetailsArrayList.get(1));//id at 0 and name at 1
            empId.setText(         "Id                  :   "+id);
            empGender.setText(     "Gender         :   "+empDetailsArrayList.get(2));
            empDesignation.setText("Designation :   "+empDetailsArrayList.get(3));





            try {
                tableView();//show cost with default rate value
            }
            catch (Exception e)
            {
                Log.d("svm","table error : "+e.getMessage());
            }



        }
//    public void tableView(MyDbHandlerGroupConfigs db)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tableView()

    {
                AdminDbHandler db=new AdminDbHandler(AttendanceView.this);
                Log.d("svm","Parmas.id "+Params.CURRENT_PRESSED_EMP_ID);

                //get the total number  of graces used for the current accessed month


        Log.d("svm","current pressed emp id : "+Params.CURRENT_PRESSED_EMP_ID+" current pressed month yr "+Params.CURRENT_PRESSED_MONTH_YR);

                db.getIndividualEmployeeAttendanceRecords(Params.CURRENT_PRESSED_EMP_ID+Params.CURRENT_PRESSED_MONTH_YR+"emp_records");

                Log.d("svm","fetch montly attendance data from table : "+Params.CURRENT_PRESSED_EMP_ID+Params.CURRENT_PRESSED_MONTH_YR+"emp_records");
        try {
//            MyDbHandlerGroupConfigs db = new MyDbHandlerGroupConfigs(com.easyway2in.sqlitedb.TableView.this);
            table.removeAllViews();//To update new costs at same postion
            ArrayList<String> dateArrayList = new ArrayList<>();
            ArrayList<String> entryTimeArrayList = new ArrayList<>();
            ArrayList<String> exitTimeArrayList = new ArrayList<>();
            ArrayList<String> totalHrsServedArrayList = new ArrayList<>();
            ArrayList<String> hFStatusArrayList = new ArrayList<>();
            ArrayList<String> attendanceStatusArrayList = new ArrayList<>();
            String tDATE;
            //Get all contacts
            List<EmployeeDetails> receivedAttendanceRecordList = db.getIndividualEmployeeAttendanceRecords(Params.CURRENT_PRESSED_EMP_ID+Params.CURRENT_PRESSED_MONTH_YR+"emp_records");//fetch all saved records
//            List<EmployeeDetails> receivedAttendanceRecordList = db.getIndividualEmployeeAttendanceRecords("t199March_2022emp_records");//fetch all saved records
            Log.d("svm","size of received list "+receivedAttendanceRecordList.size()+"");
            for (EmployeeDetails emp : receivedAttendanceRecordList) {

                Log.d("svm","date : "+emp.getDate()+" entry time : "+emp.getEntryTime());
                dateArrayList.add(emp.getDate());
                entryTimeArrayList.add("           " + emp.getEntryTime() + "");
                exitTimeArrayList.add("            " + emp.getExitTime() + "");
                totalHrsServedArrayList.add("         " + emp.getTotalHoursServed() + "");
                hFStatusArrayList.add("         " + emp.getHalfFullDayStatus() + "");
                attendanceStatusArrayList.add("             " + emp.getAttendanceStatus() + "");

            }



            TableRow hrow = new TableRow(this);
            String dayNum="S. No.   ";
            String hdate= "Date";
            String hc1 ="       "+"Entry Time";
            String hc2 = "       "+"Exit Time";
            String hc3 = "       "+"Total Hrs Served";
            String hc3_4 = "       "+"Day Status";
            String hc4 = "       "+"Attendance Status             ";

            TextView htvdayNum = new TextView(this);
            htvdayNum.setTextSize(15);
            htvdayNum.setTextColor(this.getResources().getColor(R.color.black));
            htvdayNum.setText(dayNum);
//
            TextView htvdate = new TextView(this);
            htvdate.setTextSize(18);
//                htv1.setLinkTextColor();
            htvdate.setTextColor(this.getResources().getColor(R.color.black));
            htvdate.setText(hdate);

            TextView htv1 = new TextView(this);
            htv1.setTextSize(18);
            htv1.setTextColor(this.getResources().getColor(R.color.black));
            htv1.setText(hc1);

            TextView htv2 = new TextView(this);
            htv2.setTextSize(18);
            htv2.setTextColor(this.getResources().getColor(R.color.black));
            htv2.setText(hc2);

            TextView htv3 = new TextView(this);
            htv3.setTextSize(18);
            htv3.setTextColor(this.getResources().getColor(R.color.black));
            htv3.setText(hc3);

            TextView htv3_4 = new TextView(this);
            htv3_4.setTextSize(18);
            htv3_4.setTextColor(this.getResources().getColor(R.color.black));
            htv3_4.setText(hc3_4);

            TextView htv4 = new TextView(this);
            htv4.setTextSize(18);
            htv4.setTextColor(this.getResources().getColor(R.color.black));
            htv4.setText(hc4);



            hrow.addView(htvdayNum);
            hrow.addView(htvdate);
            hrow.addView(htv1);
            hrow.addView(htv2);
            hrow.addView(htv3);
            hrow.addView(htv3_4);
            hrow.addView(htv4);

            table.addView(hrow);

            //Log.d("dbsvm",tempdate.size()+"\n");            //Log.d("dbsvm",tempdate.size()+"\n");

            for (int i = 0; i < dateArrayList.size(); i++) {
                //Log.d("dbsvm",tempdate.get(i)+"\n");
                TableRow row = new TableRow(this);
                String date = dateArrayList.get(i);
                String c1 = entryTimeArrayList.get(i);
                String c2 = exitTimeArrayList.get(i);
                String c3 = totalHrsServedArrayList.get(i);
                String c3_4=hFStatusArrayList.get(i);
                String c4 = attendanceStatusArrayList.get(i);

                TextView tvDayNum = new TextView(this);
                tvDayNum.setText(i+1+"");

                TextView tvdate = new TextView(this);
                tvdate.setText(date);
                TextView tv1 = new TextView(this);
                tv1.setText(c1);
                TextView tv2 = new TextView(this);
                tv2.setText(c2);
                TextView tv3 = new TextView(this);
                tv3.setText(c3);
                TextView tv3_4 = new TextView(this);
                tv3_4.setText(c3_4);
                TextView tv4 = new TextView(this);
                tv4.setText(c4);

                row.addView(tvDayNum);
                row.addView(tvdate);
                row.addView(tv1);
                row.addView(tv2);
                row.addView(tv3);
                row.addView(tv3_4);
                row.addView(tv4);


                table.addView(row);
            }





















        } catch (Exception e) {
            Log.d("svm","Unable to view data on table"+ e.getMessage().toString());
        }
        //Log.d("dbsvm","Total Table rows: "+table.getChildCount()+"");
//        return table;//To convert into pdf
    }

}




