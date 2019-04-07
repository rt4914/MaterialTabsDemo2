package com.advanced.education.admin.wisel.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UtilityFile {

    private static final String TAG = UtilityFile.class.getSimpleName();

    private Context context;

    private Typeface tfBold;
    private Typeface tfExtraBold;
    private Typeface tfExtraLight;
    private Typeface tfLight;
    private Typeface tfMedium;
    private Typeface tfRegular;
    private Typeface tfSemiBold;

    public UtilityFile(Context context) {
        this.context = context;

        setFont();

    }

    public void checkPermissions(ArrayList<Integer> permissionArrayList) {

        if (permissionArrayList != null && !permissionArrayList.isEmpty()) {

            for (int index : permissionArrayList) {

                switch (index) {

                    case Constants.REQUEST_CALL_ACCESS:
                        requestCallPermission();
                        break;

                    case Constants.REQUEST_CAMERA_ACCESS:
                        requestCameraPermission();
                        break;

                    case Constants.REQUEST_LOCATION_ACCESS:
                        requestLocationPermission();
                        break;

                    case Constants.REQUEST_READ_STORAGE_ACCESS:
                        requestReadPermission();
                        break;

                    case Constants.REQUEST_WRITE_STORAGE_ACCESS:
                        requestWritePermission();
                        break;

                    default:
                        break;

                }

            }

        }

    }

    public void requestCameraPermission() {

        Boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getActivity(context),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(getActivity(context),
                    new String[]{Manifest.permission.CAMERA},
                    Constants.REQUEST_CAMERA_ACCESS);
        }

    }

    public void requestWritePermission() {

        Boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getActivity(context),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(getActivity(context),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_WRITE_STORAGE_ACCESS);
        }

    }

    public void requestReadPermission() {

        Boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getActivity(context),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(getActivity(context),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_READ_STORAGE_ACCESS);
        }

    }

    public void requestLocationPermission() {

        Boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getActivity(context),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(getActivity(context),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_LOCATION_ACCESS);
        }

    }

    public void requestCallPermission() {

        Boolean hasPermissionCamera = (ContextCompat.checkSelfPermission(getActivity(context),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(getActivity(context),
                    new String[]{Manifest.permission.CALL_PHONE},
                    Constants.REQUEST_CALL_ACCESS);
        }

    }

    private void setFont() {

        tfBold = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_BOLD);
        tfExtraBold = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_EXTRA_BOLD);
        tfExtraLight = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_EXTRA_LIGHT);
        tfLight = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_LIGHT);
        tfMedium = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_MEDIUM);
        tfRegular = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_REGULAR);
        tfSemiBold = Typeface.createFromAsset(context.getAssets(), Constants.FONT_TYPE_SEMI_BOLD);

    }

    public Typeface getTfBold() {
        return tfBold;
    }

    public Typeface getTfExtraBold() {
        return tfExtraBold;
    }

    public Typeface getTfExtraLight() {
        return tfExtraLight;
    }

    public Typeface getTfLight() {
        return tfLight;
    }

    public Typeface getTfMedium() {
        return tfMedium;
    }

    public Typeface getTfRegular() {
        return tfRegular;
    }

    public Typeface getTfSemiBold() {
        return tfSemiBold;
    }

    //Works only with activities and fragments and not with services
    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    public String getStringFromLongTimestamp(long lTimestamp, String sFormat) {

        Date date = new Date(lTimestamp * 1000);
        DateFormat df = new SimpleDateFormat(sFormat, Locale.US);
        return df.format(date);

    }

    public boolean isPhoneNumberValid(String sPhoneNumber) {

        return sPhoneNumber != null && !sPhoneNumber.isEmpty() && sPhoneNumber.length() == 10;

    }

    public boolean isPasswordValid(String sPassword) {

        return sPassword.length() >= 6;

    }

    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

            return false;
        } else
            return false;

    }

    public boolean isEmailValid(String sEmail) {

        return !sEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches();

    }

    public boolean isValidOTP(String sOTP) {

        return sOTP.length() >= 4;

    }

    public boolean doesStringExist(String string) {

        return string != null && !string.isEmpty();

    }

    public long convertToDateTimestamp(long lTimestamp) {

        lTimestamp = lTimestamp + 19800;
        long lNumberOfDays = lTimestamp / (24 * 60 * 60);
        lTimestamp = lNumberOfDays * (24 * 60 * 60);
        return lTimestamp;

    }

    public long combineDateAndTime(long lMessageDate, long lMessageTime) {

        long ONE_DAY_VALUE = 24 * 60 * 60;
        long lPerfectDateTimestamp = convertToDateTimestamp(lMessageDate);
        long lPerfectTimeTimestamp = lMessageTime % ONE_DAY_VALUE;
        return (lPerfectDateTimestamp + lPerfectTimeTimestamp);

    }

    public long getCurrentTimestamp() {

        return (new Date().getTime() / 1000) + 19800;

    }

    public long getTodayDateTimestamp() {

        return convertToDateTimestamp(getCurrentTimestamp());

    }

    //https://stackoverflow.com/questions/13627308/add-st-nd-rd-and-th-ordinal-suffix-to-a-number
    public String dayDateWithSuffix(int iDate) {

        int j = iDate % 10;
        int k = iDate % 100;

        if (j == 1 && k != 11) {
            return "st";
        }
        if (j == 2 && k != 12) {
            return "nd";
        }
        if (j == 3 && k != 13) {
            return "rd";
        }

        return "th";

    }

    public boolean isPincodeValid(String sPincode) {

        return sPincode != null && !sPincode.isEmpty() && sPincode.length() == 6;

    }

    public double round (double value, int precision) {

        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;

    }

    public float convertInchToCM(String size) {
        float inches = Float.parseFloat(size);
        float cm = inches * 2.54f;
        cm = (float) round((double)cm, 1);
        return cm;
    }

    public void hideKeyboardFrom(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

}

