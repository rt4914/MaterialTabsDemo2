package com.arvind.linebalancing.activity.designation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.MainActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddDesignationActivity extends AppCompatActivity {

    private static final String TAG = AddDesignationActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvDesignationNameLabel;
    private EditText etDesignationName;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sDesignationId;
    private String sPreviousDesignationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_designation);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sDesignationId = i.getStringExtra("DESIGNATION_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sDesignationId == null || sDesignationId.isEmpty())) {
                showToast(getResources().getString(R.string.unexpected_error));
                finish();
                return;
            }
        }

        utilityFile = new UtilityFile(this);

        setProgressDialog();
        volleySetup();
        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        ivSave = findViewById(R.id.ivSave);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        tvDesignationNameLabel = findViewById(R.id.tvDesignationNameLabel);
        etDesignationName = findViewById(R.id.etDesignationName);

        tvDesignationNameLabel.setTypeface(utilityFile.getTfMedium());
        etDesignationName.setTypeface(utilityFile.getTfRegular());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        DesignationTable designationTable = AppDatabase.getInstance(this).designationTableDao()
                .getDesignationById(sDesignationId);

        if (designationTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sPreviousDesignationName = designationTable.getDesignationName();
        etDesignationName.setText(designationTable.getDesignationName());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
//            ivSave.setVisibility(View.GONE);
        }

        titleLabel();

    }

    private void enableEditMode(boolean isEditable) {

        etDesignationName.setEnabled(isEditable);
        tvDesignationNameLabel.setClickable(isEditable);

    }

    public void saveButtonClicked(View view) {

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {
            verifyDetails();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            verifyDetails();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            sCurrentMode = Constants.ACTIVITY_MODE_EDIT;
            setInitialData();
        }

    }

    private void verifyDetails() {

        String sDesignationName = etDesignationName.getText().toString();

        if (sDesignationName == null || sDesignationName.isEmpty()) {
            showToast("Enter Designation Name.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sPreviousDesignationName != null
                && sDesignationName.matches(sPreviousDesignationName)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("designationName", sDesignationName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sDesignationId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_DESIGNATION,
                        Constants.BASE_SERVER_URL + "update_designation",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("designationName", sDesignationName);

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_DESIGNATION,
                    Constants.BASE_SERVER_URL + "add_designation",
                    params);

        }

    }


    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_DESIGNATION)) {
                    addDesignationResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_DESIGNATION)) {
                    updateDesignationResponse(response);
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

    private void addDesignationResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject designationJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeDesignationObject(designationJSONObject);

                showToast("Designation Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateDesignationResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject designationJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeDesignationObject(designationJSONObject);

                showToast("Designation Updated Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void nextActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return;

    }

    private void titleLabel() {

        switch (sCurrentMode) {
            case Constants.ACTIVITY_MODE_ADD:
                tvTitleLabel.setText("Add Designation");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Designation");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Designation Detail");
                break;
            default:
                tvTitleLabel.setText("Designation Detail");
                break;

        }

    }

    public void backButtonClicked(View view) {

        finish();

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

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

}
