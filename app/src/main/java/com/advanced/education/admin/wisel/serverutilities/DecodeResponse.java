package com.advanced.education.admin.wisel.serverutilities;

import android.content.Context;

import com.advanced.education.admin.wisel.utilities.UtilityFile;

public class DecodeResponse {

    private static final String TAG = DecodeResponse.class.getSimpleName();

    private Context context;

    private UtilityFile utilityFile;

    public DecodeResponse(Context context) {

        this.context = context;

        utilityFile = new UtilityFile(context);

    }

}
