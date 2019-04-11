package com.arvind.linebalancing.serverutilities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetLineDataService extends Service {

    private static final String TAG = GetLineDataService.class.getSimpleName();

    private Context context;
    private GeneralVolleyRequest mGeneralVolleyRequest = null;

    private Toast mToast = null;
    private ProgressDialog pdLoading = null;

    private int iResponseCode = 0;
    private String sAdminId = null;

    public GetLineDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        volleySetup();

        if (intent != null) {

            context = getApplicationContext();

            getLineDataRequest();

        }

        return START_STICKY;
    }

    private void getLineDataRequest() {

        String url = Constants.BASE_SERVER_URL + "get_lines";

        mGeneralVolleyRequest.requestToServer(Request.Method.GET, Constants.API_GET_LINE,
                url,
                null);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                //Log.d(TAG, "volleySetup: notifySuccess: requestType: " + requestType);
                Log.d(TAG, "volleySetup: notifySuccess: response: " + response);

                if (requestType.matches(Constants.API_GET_LINE)) {
                    getLineResponse(response);
                } else {

                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                //Log.d(TAG, "volleySetup: notifySuccess: requestType: " + requestType);
                Log.d(TAG, "volleySetup: notifySuccess: response: " + error);

            }
        };

        mGeneralVolleyRequest = new GeneralVolleyRequest(this, mRequestCallback);

    }

    private void getLineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            iResponseCode = responseObject.optInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(this).lineTableDao().deleteAllLines();

                JSONArray messageJSONArray = responseObject.optJSONArray("message");

                for (int i = 0; i < messageJSONArray.length(); i++) {
                    JSONObject lineJSONObject = messageJSONArray.optJSONObject(i);

                    DecodeResponse decodeResponse = new DecodeResponse(this);
                    decodeResponse.decodeLineObject(lineJSONObject);
                }

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (
                JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

        sendResponseToActivity();

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void sendResponseToActivity() {

        Intent intent = new Intent("LINE_LIST");
        // You can also include some extra data.
        intent.putExtra("RESPONSE_CODE", iResponseCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        stopSelf();

    }

}
