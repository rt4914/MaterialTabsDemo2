package com.advanced.education.admin.wisel.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.BuildConfig;
import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.datastorage.SecurePreferences;
import com.advanced.education.admin.wisel.serverutilities.DecodeResponse;
import com.advanced.education.admin.wisel.serverutilities.GeneralVolleyRequest;
import com.advanced.education.admin.wisel.serverutilities.IVolleyResponse;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.UtilityFile;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView tvLoginLabel;
    TextView tvNext;
    TextView tvPhoneNumberLabel;
    EditText etPhoneNumber;
    TextView tvPasswordLabel;
    EditText etPassword;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sPassword = null;
    private String sPhoneNumber = null;

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

        Intent i = getIntent();

        if (i != null) {
            String sPhoneNumber = i.getStringExtra("PHONE_NUMBER");
            String sPassword = i.getStringExtra("PASSWORD");
            boolean isAutoLogin = i.getBooleanExtra("AUTO_LOGIN", true);
            if (isAutoLogin) {

                if (sPhoneNumber != null && sPassword != null) {

                    loginButtonClicked(sPhoneNumber, sPassword);
                }
            }
        }

        initialUISetup();
    }

    private void initialUISetup() {

        tvLoginLabel = findViewById(R.id.tvLoginLabel);
        tvNext = findViewById(R.id.tvNext);
        tvPhoneNumberLabel = findViewById(R.id.tvPhoneNumberLabel);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvPasswordLabel = findViewById(R.id.tvPasswordLabel);
        etPassword = findViewById(R.id.etPassword);

        etPassword.setTypeface(utilityFile.getTfRegular());
        etPhoneNumber.setTypeface(utilityFile.getTfRegular());
        tvLoginLabel.setTypeface(utilityFile.getTfBold());
        tvNext.setTypeface(utilityFile.getTfMedium());
        tvPasswordLabel.setTypeface(utilityFile.getTfMedium());
        tvPhoneNumberLabel.setTypeface(utilityFile.getTfMedium());

    }

    public void nextLabelClicked(View view) {

        sPhoneNumber = etPhoneNumber.getText().toString();
        sPassword = etPassword.getText().toString();

        loginButtonClicked(sPhoneNumber, sPassword);

    }

    public void loginButtonClicked(String sPhoneNumber, String sPassword) {

        nextActivity();

        if (sPhoneNumber == null || sPhoneNumber.isEmpty()) {
            showToast("Please enter phone number.");
            return;
        }

        if (!utilityFile.isPhoneNumberValid(sPhoneNumber)) {
            showToast("Phone number must have 10 characters only.");
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
            //sendTeacherLoginRequest();
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void sendTeacherLoginRequest() {

        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", sPhoneNumber);
        params.put("password", sPassword);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_LOGIN_TEACHER,
                Constants.BASE_SERVER_URL + "teacher_login",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_LOGIN_TEACHER)) {
                    loginTeacherResponse(response);
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

    private void loginTeacherResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject messageJSONObject = responseObject.getJSONObject("message");

                showToast("Login successful.");

                DecodeResponse decodeResponse = new DecodeResponse(this);

                nextActivity();

            } else if (iResponseCode == Constants.RESPONSE_CODE_USER_NOT_FOUND) {
                showToast("Admin not found.");
            } else if (iResponseCode == Constants.RESPONSE_CODE_INCORRECT_PASSWORD) {
                showToast("Password incorrect.");
            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "loginTeacherResponse: ", e);
            showToast(getResources().getString(R.string.unexpected_error));
        }

    }

    private void nextActivity() {

        String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
        SecurePreferences mSecurePreferences = new SecurePreferences(this, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);
        mSecurePreferences.put(Constants.SP_KEY_LOGGED_IN, "true");
        mSecurePreferences.put(Constants.SP_KEY_PHONE_NUMBER, sPhoneNumber);
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
