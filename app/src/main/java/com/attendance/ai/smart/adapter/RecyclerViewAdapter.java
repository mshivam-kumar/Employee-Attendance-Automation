package com.attendance.ai.smart.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.attendance.ai.smart.AttendanceView;
import com.attendance.ai.smart.RegisteredEmployees;
import com.attendance.ai.smart.EmployeesMonthYrRecords;
import com.attendance.ai.smart.R;
import com.attendance.ai.smart.data.AdminDbHandler;
import com.attendance.ai.smart.params.Params;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    public static ArrayList<String> contactList;

    public RecyclerViewAdapter(Context context, ArrayList<String> contactList1) {
        this.context = context;

        try {
            contactList = contactList1;
        }catch (Exception e)
        {
            Log.d("dbsvm","Error : "+e.getMessage().toString());
        }
    }

    // Where to get the single card as viewholder Object

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);



        return new ViewHolder(view);
    }

    // What will happen after we create the viewholder object

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

//        holder.phoneNumber.setText(contact.getC2()+"");

        try {
            int i=position;
            ViewHolder viewHolder=holder;
            if (i % 5 == 0)
                viewHolder.itemView.setBackgroundResource(R.drawable.grad_green_blue);
            else if (i % 5 == 1)
                viewHolder.itemView.setBackgroundResource(R.drawable.grad_aqua);
            else if (i % 5 == 2)
                viewHolder.itemView.setBackgroundResource(R.drawable.grad_red);
            else if (i % 5 == 3)
                viewHolder.itemView.setBackgroundResource(R.drawable.grad_sun);
            else if (i % 5 == 4)
                viewHolder.itemView.setBackgroundResource(R.drawable.grad_pink_red);

            TextView item = holder.empId;
            item.setText(contactList.get(position));

            TextView eNameItem=holder.eName;
            AdminDbHandler db=new AdminDbHandler(context);

            String nameFromdb=db.getEmployeeNameForParicularEmployeeId(contactList.get(position));
            eNameItem.setText(nameFromdb);

//            contactList.add("str");
        }catch (Exception e)
        {
            Log.d("dbsvm","Inside RecyclerViewAdapter\n" +
                    "Error : "+e.getMessage().toString());
        }
//        String contact = contactList.get(position);
//        for(String s: HOMEcustomerGroupListView.a)
//        {

//        }

    }

    // How many items?
    @Override
    public int getItemCount() {

        return contactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView empId,eName;
        private static final long TIME_INTERVAL_GAP=500;
        private long lastTimeClicked=System.currentTimeMillis();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            empId = itemView.findViewById(R.id.name);
            eName=itemView.findViewById(R.id.eNameForId);
//            phoneNumber = itemView.findViewById(R.id.phone_number);
//            iconButton = itemView.findViewById(R.id.icon_button);

//            iconButton.setOnClickListener(this);
        }


//        @Override
//        public void onClick(View view) {
//            //Log.d("ClickFromViewHolder", "Clicked");
//
//        }
        @Override
        public void onClick(View view) {
            long now=System.currentTimeMillis();
            if(now-lastTimeClicked<TIME_INTERVAL_GAP)
                return;
            lastTimeClicked=now;
//            //Log.d("ClickFromViewHolder", "Clicked");
            int position = this.getAdapterPosition();
            String empId = contactList.get(position);



            if(context instanceof RegisteredEmployees)//if HomecutomerGroupListViewCalls  then
            {
                    Params.CURRENT_PRESSED_EMP_ID = "t" + empId;//Individual employee attendance table start with t ..."t"+id
                    Log.d("svm", "id : " + empId + " pressed");


                    context.startActivity(new Intent(context, EmployeesMonthYrRecords.class));
                    Log.d("svm","instance of employeesAttendanceRecords");
//                    context.startActivity(new Intent(context, AttendanceView.class));


            }
            else if(context instanceof EmployeesMonthYrRecords)  {
                Params.CURRENT_PRESSED_MONTH_YR=empId.toLowerCase();
                Log.d("svm","instance of employeesMonthYrRecords");
                Log.d("svm", "current month yr : " + Params.CURRENT_PRESSED_MONTH_YR + " pressed");
                context.startActivity(new Intent(context, AttendanceView.class));


            }

        }
    }
}

