package com.attendance.ai.smart.alertMessage;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class GetAlerts {



    public void alertDialogBox(Context context, String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setMessage("Please insert appropriate name and phone number");
        dialog.setMessage(msg);

//        dialog.setTitle("Dialog Box");
        dialog.setTitle(title);

//       dialog.setPositiveButton("YES",
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
//                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
//
    }

//    public String PromptUserForInput(Context context)
//{
//    final String[] m_Text = new String[1];
//    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//    builder.setTitle("Enter File Name to Save");
//
//// Set up the input
//     EditText input = new EditText(context);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//    input.setInputType(InputType.TYPE_CLASS_TEXT);
//    builder.setView(input);
//
//// Set up the buttons
//    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            m_Text[0] =input.getText().toString();
////
//        }
//    });
//    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            dialog.cancel();
//        }
//    });
//    AlertDialog alertDialog=builder.create();
//    alertDialog.show();
////    return m_Text[0];
//}

}
