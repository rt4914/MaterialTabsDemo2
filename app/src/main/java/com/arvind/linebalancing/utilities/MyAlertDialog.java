package com.arvind.linebalancing.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.List;

public class MyAlertDialog {

    private Context context;

    private MyAlertDialogInterface myAlertDialogInterface;
    private MyDeleteDialogInterface MyDeleteDialogInterface;

    public MyAlertDialog(Context context, MyAlertDialogInterface myAlertDialogInterface) {

        this.context = context;
        this.myAlertDialogInterface = myAlertDialogInterface;

    }

    public MyAlertDialog(Context context, MyDeleteDialogInterface MyDeleteDialogInterface) {

        this.context = context;
        this.MyDeleteDialogInterface = MyDeleteDialogInterface;

    }

    public void createAlertBox(String sTitle, boolean isCancelable, List<String> optionArrayList, final String sAlertDialogType) {

        CharSequence[] options = optionArrayList.toArray(new CharSequence[optionArrayList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(sTitle);
        builder.setCancelable(isCancelable);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (myAlertDialogInterface != null) {
                    myAlertDialogInterface.getSelectedItemIndex(i, sAlertDialogType);
                }

                dialogInterface.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void createDeleteAlertBox(String sTitle, boolean isCancelable, final String sAlertDialogType, final int adapterPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(sTitle);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setCancelable(isCancelable);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (MyDeleteDialogInterface != null) {
                    MyDeleteDialogInterface.isDeleted(true, sAlertDialogType, adapterPosition);
                }

                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (MyDeleteDialogInterface != null) {
                    MyDeleteDialogInterface.isDeleted(false, sAlertDialogType, adapterPosition);
                }

                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
