package com.arvind.linebalancing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.table.SuggestionTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyDatePicker;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewSuggestionActivity extends AppCompatActivity {

    private static final String TAG = ViewSuggestionActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvSelectEmployee;
    private TextView tvSelectLine;
    private TextView tvSelectMachine;
    private TextView tvSelectOperation;
    private TextView tvSelectTime;
    private TextView tvEmployeeLabel;
    private TextView tvLineLabel;
    private TextView tvOperationLabel;
    private TextView tvMachineLabel;
    private TextView tvTimeLabel;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyDatePicker myDatePicker;
    private UtilityFile utilityFile;

    private long lTimestamp = 0;
    private long lPreviousTimestamp = 0;
    private String sSuggestionId;
    private String sPreviousEmployeeId;
    private String sPreviousMachineId;
    private String sPreviousLineId;
    private String sPreviousOperationId;
    private String sCurrentMode;

    private ArrayList<String> selectedEmployeeIdList;
    private ArrayList<String> selectedLineIdList;
    private ArrayList<String> selectedMachineIdList;
    private ArrayList<String> selectedOperationIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_suggestion);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sSuggestionId = i.getStringExtra("SUGGESTION_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sSuggestionId == null || sSuggestionId.isEmpty())) {
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

        tvEmployeeLabel = findViewById(R.id.tvEmployeeLabel);
        tvSelectEmployee = findViewById(R.id.tvSelectEmployee);
        tvOperationLabel = findViewById(R.id.tvOperationLabel);
        tvSelectOperation = findViewById(R.id.tvSelectOperation);
        tvMachineLabel = findViewById(R.id.tvMachineLabel);
        tvSelectMachine = findViewById(R.id.tvSelectMachine);
        tvLineLabel = findViewById(R.id.tvLineLabel);
        tvSelectLine = findViewById(R.id.tvSelectLine);
        tvTimeLabel = findViewById(R.id.tvTimeLabel);
        tvSelectTime = findViewById(R.id.tvSelectTime);

        tvEmployeeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectEmployee.setTypeface(utilityFile.getTfMedium());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectOperation.setTypeface(utilityFile.getTfMedium());
        tvTimeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectTime.setTypeface(utilityFile.getTfMedium());
        tvMachineLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectMachine.setTypeface(utilityFile.getTfMedium());
        tvLineLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectLine.setTypeface(utilityFile.getTfMedium());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        SuggestionTable suggestionTable = AppDatabase.getInstance(this).suggestionTableDao().getSuggestionById(sSuggestionId);

        if (suggestionTable != null) {

            sPreviousLineId = suggestionTable.getLineId();
            sPreviousOperationId = suggestionTable.getOperationId();
            sPreviousMachineId = suggestionTable.getMachineId();
//            sPreviousEmployeeId = suggestionTable.getEmployeeId();
            lPreviousTimestamp = suggestionTable.getTimestamp();

            LineTable lineTable = AppDatabase.getInstance(this).lineTableDao().getLineById(sPreviousLineId);
            OperationTable operationTable = AppDatabase.getInstance(this).operationTableDao().getOperationById(sPreviousOperationId);
            MachineTable machineTable = AppDatabase.getInstance(this).machineTableDao().getMachineById(sPreviousMachineId);
            EmployeeTable employeeTable = AppDatabase.getInstance(this).employeeTableDao().getEmployeeById(sPreviousEmployeeId);

            if (lineTable != null) {
                tvSelectLine.setText(lineTable.getLineName());
                selectedLineIdList = new ArrayList<>();
                selectedLineIdList.add(sPreviousLineId);
            }

            if (operationTable != null) {
                tvSelectOperation.setText(operationTable.getOperationName());
                selectedOperationIdList = new ArrayList<>();
                selectedOperationIdList.add(sPreviousOperationId);
            }

            if (machineTable != null) {
                tvSelectMachine.setText(machineTable.getMachineName());
                selectedMachineIdList = new ArrayList<>();
                selectedMachineIdList.add(sPreviousMachineId);
            }

            if (employeeTable != null) {
                tvSelectEmployee.setText(employeeTable.getEmployeeName());
                selectedEmployeeIdList = new ArrayList<>();
                selectedEmployeeIdList.add(sPreviousEmployeeId);
            }

            if (lPreviousTimestamp != 0) {
                tvSelectTime.setText(utilityFile.getStringFromLongTimestamp(lPreviousTimestamp, Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));
            }

        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            enableEditMode(false);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        }

        titleLabel();
    }

    private void enableEditMode(boolean isEditable) {

        tvSelectEmployee.setClickable(false);
        tvEmployeeLabel.setClickable(false);
        tvSelectMachine.setClickable(isEditable);
        tvMachineLabel.setClickable(isEditable);
        tvSelectMachine.setClickable(isEditable);
        tvMachineLabel.setClickable(isEditable);
        tvSelectMachine.setClickable(isEditable);
        tvMachineLabel.setClickable(isEditable);
        tvSelectMachine.setClickable(isEditable);
        tvMachineLabel.setClickable(isEditable);

    }

    public void saveButtonClicked(View view) {
        sendSaveSuggestionRequest();
    }

    private void sendSaveSuggestionRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", sSuggestionId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_SAVE_SUGGESTION,
                Constants.BASE_SERVER_URL + "save_suggestion",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_SAVE_SUGGESTION)) {
                    sendSaveSuggestionResponse(response);
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

    private void sendSaveSuggestionResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject suggestionJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeSuggestionObject(suggestionJSONObject);

//                JSONObject connectionJSONObject = responseObject.optJSONObject("message");
//                decodeResponse.decodeConnectionObject(connectionJSONObject);

                showToast("Suggestion saved successfully.");
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
                tvTitleLabel.setText("Add Suggestion");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Suggestion");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("View Suggestion");
                break;
            default:
                tvTitleLabel.setText("View Suggestion");
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
