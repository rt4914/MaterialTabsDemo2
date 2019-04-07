package com.advanced.education.admin.wisel.utilities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class MyTimePicker {

    private Context context;
    private MyTimePickerInterface myTimePickerInterface;

    private Calendar calendarTime;

    public MyTimePicker(Context context, MyTimePickerInterface myTimePickerInterface) {
        this.context = context;
        this.myTimePickerInterface = myTimePickerInterface;
    }

    public void showTimePicker(final String sRequestType) {

        calendarTime = Calendar.getInstance();
        int hour = calendarTime.get(Calendar.HOUR_OF_DAY);
        int minute = calendarTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                calendarTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendarTime.set(Calendar.MINUTE, selectedMinute);

                if (myTimePickerInterface != null) {
                    myTimePickerInterface.getCalendarTime(getCalendarTime(), sRequestType);
                }

            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private long getCalendarTime() {

        if (calendarTime != null) {
            Date date = calendarTime.getTime();
            return (date.getTime() / 1000)+19800;
        } else {
            return 0;
        }

    }
}
