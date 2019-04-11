package com.arvind.linebalancing.activity.operation;

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
import com.arvind.linebalancing.activity.connection.MachineOperationMapActivity;
import com.arvind.linebalancing.activity.machine.AddMachineActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddOperationActivity extends AppCompatActivity {

    private static final String TAG = AddOperationActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvOperationNameLabel;
    private EditText etOperationName;
    private TextView tvSAMValueLabel;
    private EditText etSAMValue;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sOperationId;
    private String sPreviousOperationName;
    private String sPreviousSAMValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sOperationId = i.getStringExtra("OPERATION_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sOperationId == null || sOperationId.isEmpty())) {
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

        tvOperationNameLabel = findViewById(R.id.tvOperationNameLabel);
        etOperationName = findViewById(R.id.etOperationName);
        tvSAMValueLabel = findViewById(R.id.tvSAMValueLabel);
        etSAMValue = findViewById(R.id.etSAMValue);

        tvOperationNameLabel.setTypeface(utilityFile.getTfMedium());
        etOperationName.setTypeface(utilityFile.getTfRegular());
        tvSAMValueLabel.setTypeface(utilityFile.getTfMedium());
        etSAMValue.setTypeface(utilityFile.getTfRegular());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        OperationTable operationTable = AppDatabase.getInstance(this).operationTableDao()
                .getOperationById(sOperationId);

        if (operationTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sPreviousOperationName = operationTable.getOperationName();
        sPreviousSAMValue = String.valueOf(operationTable.getSamValue());

        etOperationName.setText(operationTable.getOperationName());
        etSAMValue.setText(operationTable.getSamValue()+"");

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

        etOperationName.setEnabled(isEditable);
        tvOperationNameLabel.setClickable(isEditable);
        etSAMValue.setEnabled(isEditable);
        tvSAMValueLabel.setClickable(isEditable);

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

        String sOperationName = etOperationName.getText().toString();

        if (sOperationName == null || sOperationName.isEmpty()) {
            showToast("Enter Operation Name.");
            return;
        }

        String sSAMValue = etSAMValue.getText().toString();

        if (sSAMValue == null || sSAMValue.isEmpty()) {
            showToast("Enter SAM Value.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sPreviousOperationName != null && sPreviousSAMValue != null
                && sOperationName.matches(sPreviousOperationName) && sSAMValue.matches(sPreviousSAMValue)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("operationName", sOperationName);
                jsonObject.put("samValue", sSAMValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sOperationId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_OPERATION,
                        Constants.BASE_SERVER_URL + "update_operation",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("operationName",sOperationName);
            params.put("samValue",sSAMValue);

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_OPERATION,
                    Constants.BASE_SERVER_URL + "add_operation",
                    params);

        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_OPERATION)) {
                    addOperationResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_OPERATION)) {
                    updateOperationResponse(response);
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

    private void addOperationResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject operationJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeOperationObject(operationJSONObject);

                showToast("Operation Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateOperationResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject operationJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeOperationObject(operationJSONObject);

                showToast("Operation Updated Successfully.");

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
                tvTitleLabel.setText("Add Operation");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Operation");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Operation Detail");
                break;
            default:
                tvTitleLabel.setText("Operation Detail");
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
