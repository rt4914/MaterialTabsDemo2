package com.arvind.linebalancing.serverutilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arvind.linebalancing.BuildConfig;
import com.arvind.linebalancing.activity.LoginActivity;
import com.arvind.linebalancing.datastorage.SecurePreferences;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GeneralVolleyRequest {

    private static final String TAG = GeneralVolleyRequest.class.getSimpleName();

    private Context context;
    private IVolleyResponse mResultCallback = null;

    public GeneralVolleyRequest(Context context, IVolleyResponse mResultCallback) {

        this.context = context;
        this.mResultCallback = mResultCallback;

    }

    public void requestToServer(int iRequestType, final String sFeature, String sURL, final Map<String, String> mapParams) {

        StringRequest srGeneral = new StringRequest(iRequestType, sURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseJSONObject = new JSONObject(response);
                    int iResponseCode = responseJSONObject.optInt("response_code");
                    if (iResponseCode == 402) {
                        Log.d(TAG, "onResponse: 402");
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("AUTO_LOGIN", false);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    } else if (iResponseCode == 403) {
                        Log.d(TAG, "onResponse: 403");
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("AUTO_LOGIN", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    } else {
                        if (mResultCallback != null) {
                            mResultCallback.notifySuccess(sFeature, response);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mResultCallback != null) {
                        mResultCallback.notifySuccess(sFeature, response);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResultCallback != null) {
                    mResultCallback.notifyError(sFeature, error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                return mapParams;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
                SecurePreferences mSecurePreferences = new SecurePreferences(context, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", Constants.CONTENT_HEADER);

                String sToken = mSecurePreferences.getString(Constants.SP_KEY_AUTH_TOKEN);
                if(sToken!=null){
                    headers.put("access-token", sToken);
                }
                else{
                    headers.put("access-token", "");
                }
                return headers;
            }
        };

        MyApplication.getInstance().addToRequestQueue(srGeneral, sFeature);

    }

}
