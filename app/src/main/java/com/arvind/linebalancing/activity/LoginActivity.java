package com.arvind.linebalancing.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arvind.linebalancing.BuildConfig;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.datastorage.SecurePreferences;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.GetDesignationDataService;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView tvLoginLabel;
    TextView tvNext;
    TextView tvEmailLabel;
    EditText etEmail;
    TextView tvPasswordLabel;
    EditText etPassword;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sPassword = null;
    private String sEmail = null;
    private String sAdminId = null;
    private String sAdminDesignationId = null;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();

            DesignationTable designationTable =  AppDatabase.getInstance(LoginActivity.this).designationTableDao().getDesignationById(sAdminDesignationId);

            if (designationTable != null) {
                if (designationTable.getDesignationName().equalsIgnoreCase("admin")) {
                    showToast("Login Successfull.");
                    nextActivity();
                } else {
                    showToast(getResources().getString(R.string.unexpected_error));
                }
            } else {
                showToast(getResources().getString(R.string.unexpected_error));
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);
        volleySetup();
        setProgressDialog();

        initialUISetup();
    }

    private void initialUISetup() {

        tvLoginLabel = findViewById(R.id.tvLoginLabel);
        tvNext = findViewById(R.id.tvNext);
        tvEmailLabel = findViewById(R.id.tvEmailLabel);
        etEmail = findViewById(R.id.etEmail);
        tvPasswordLabel = findViewById(R.id.tvPasswordLabel);
        etPassword = findViewById(R.id.etPassword);

        etPassword.setTypeface(utilityFile.getTfRegular());
        etEmail.setTypeface(utilityFile.getTfRegular());
        tvLoginLabel.setTypeface(utilityFile.getTfBold());
        tvNext.setTypeface(utilityFile.getTfMedium());
        tvPasswordLabel.setTypeface(utilityFile.getTfMedium());
        tvEmailLabel.setTypeface(utilityFile.getTfMedium());

    }

    public void nextLabelClicked(View view) {

        sEmail = etEmail.getText().toString();
        sPassword = etPassword.getText().toString();

        loginButtonClicked(sEmail, sPassword);

    }

    public void loginButtonClicked(String sEmail, String sPassword) {

        if (sEmail == null || sEmail.isEmpty()) {
            showToast("Please enter email.");
            return;
        }

        if (sPassword == null || sPassword.isEmpty()) {
            showToast("Please enter password");
            return;
        }

        if (!utilityFile.isPasswordValid(sPassword)) {
            showToast("Invalid! required more than 5 characters");
            return;
        }

        if (utilityFile.isConnectingToInternet()) {
            sendAdminLoginRequest();
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void sendAdminLoginRequest() {

        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();
        params.put("employeeEmail", sEmail);
        params.put("employeePassword", sPassword);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADMIN_LOGIN,
                Constants.BASE_SERVER_URL + "login_employee",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADMIN_LOGIN)) {
                    loginAdminResponse(response);
                } else {
                    showToast(getResources().getString(R.string.unexpected_error));
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                hideProgressDialog();
                showToast(getResources().getString(R.string.volley_server_error));

            }
        };

        mGeneralVolleyRequest = new GeneralVolleyRequest(this, mRequestCallback);

    }

    private void loginAdminResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONArray messageJSONArray = responseObject.optJSONArray("message");

                if (messageJSONArray != null && messageJSONArray.length() > 0) {

                    JSONObject adminJSONObject = messageJSONArray.optJSONObject(0);

                    sAdminId = adminJSONObject.optString("id");
                    sAdminDesignationId = adminJSONObject.optString("designationId");

                    DecodeResponse decodeResponse = new DecodeResponse(this);
                    decodeResponse.decodeAdminObject(adminJSONObject);

                    nextService();

                } else {
                    showToast(getResources().getString(R.string.unexpected_error));
                }

            } else if (iResponseCode == Constants.RESPONSE_CODE_USER_NOT_FOUND) {
                showToast("Admin not found.");
            } else if (iResponseCode == Constants.RESPONSE_CODE_INCORRECT_PASSWORD) {
                showToast("Password incorrect.");
            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "loginAdminResponse: ", e);
            showToast(getResources().getString(R.string.unexpected_error));
        }

    }

    private void nextService() {

        if (utilityFile.isConnectingToInternet()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("DESIGNATION_LIST"));

            Log.d(TAG, "nextService: ");
            showProgressDialog();
            Intent intent = new Intent(this, GetDesignationDataService.class);
            startService(intent);
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void nextActivity() {

        String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
        SecurePreferences mSecurePreferences = new SecurePreferences(this, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);
        mSecurePreferences.put(Constants.SP_KEY_LOGGED_IN, "true");
        mSecurePreferences.put(Constants.SP_KEY_EMAIL, sEmail);
        mSecurePreferences.put(Constants.SP_KEY_PASSWORD, sPassword);
        //mSecurePreferences.put(Constants.SP_KEY_INITIAL_SETUP_TIMESTAMP, "");

        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void setProgressDialog() {

        if (pdLoading == null) {

            pdLoading = new ProgressDialog(this);
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);

        }

    }

    private void showProgressDialog() {

        if (pdLoading == null) {
            setProgressDialog();
        }
        if (pdLoading.isShowing()) {
            pdLoading.cancel();
        }
        pdLoading.show();

    }

    private void hideProgressDialog() {

        if (pdLoading == null) {
            setProgressDialog();
        }

        if (pdLoading.isShowing()) {
            pdLoading.cancel();
        }

    }

}
