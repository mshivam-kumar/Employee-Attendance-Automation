package com.attendance.ai.smart;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import uk.co.senab.photoview.PhotoViewAttacher;

public class UserInstructions extends AppCompatActivity {
    private PhotoViewAttacher pAttacher;




    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.user_instructions);

//        androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);



//            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//
////            //Log.d("dbsvm","Inside UserInstructions : "+e.getMessage().toString());
//
////Setting custome toolbar back button to go back
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//
//        });

        pAttacher = new PhotoViewAttacher(findViewById(R.id.about_app_image));
        pAttacher.update();

    }
}
