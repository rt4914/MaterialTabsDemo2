package com.advanced.education.admin.wisel.utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class MyDatePicker {

    private Context context;
    private MyDatePickerInterface myDatePickerInterface;



    private Calendar calendarDate;

    private String sRequestType;

    public MyDatePicker(Context context, MyDatePickerInterface myDatePickerInterface) {

        this.context = context;
        this.myDatePickerInterface = myDatePickerInterface;

    }

    public void showDatePicker(String sRequestType) {

        this.sRequestType = sRequestType;

        calendarDate = Calendar.getInstance();
        new DatePickerDialog(context,
                mDatePicker,
                calendarDate.get(Calendar.YEAR),
                calendarDate.get(Calendar.MONTH),
                calendarDate.get(Calendar.DAY_OF_MONTH)).show();

    }

    private long getCalendarTime() {

        if (calendarDate != null) {
            Date date = calendarDate.getTime();
            //return (date.getTime() / 1000)+19800;
            return (date.getTime() / 1000);
        } else {
            return 0;
        }

    }

    private DatePickerDialog.OnDateSetListener mDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            calendarDate.set(Calendar.YEAR, year);
            calendarDate.set(Calendar.MONTH, month);
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (myDatePickerInterface != null) {
                myDatePickerInterface.getCalendarTime(getCalendarTime(), sRequestType);
            }

        }
    };

}
