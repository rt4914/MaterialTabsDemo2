package com.arvind.linebalancing.activity.employee;

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
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeRatingTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyDatePicker;
import com.arvind.linebalancing.utilities.MyDatePickerInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEmployeeRatingActivity extends AppCompatActivity {

    private static final String TAG = AddEmployeeRatingActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvSAMValueLabel;
    private EditText etSAMValue;
    private TextView tvEmployeeNoLabel;
    private EditText etEmployeeNo;
    private TextView tvOperationLabel;
    private TextView tvSelectOperation;
    private TextView tvEmployeeLabel;
    private TextView tvSelectEmployee;
    private TextView tvRatingTimeLabel;
    private TextView tvSelectRatingTime;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private MyDatePicker myDatePicker;
    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private long lRatingTimestamp = 0;
    private String sCurrentMode;
    private String sEmployeeRatingId;
    private String sPreviousSAMValue;
    private String sPreviousOperationId;
    private String sPreviousEmployeeId;
    private long lPreviousRateTimestamp;

    private ArrayList<String> selectedEmployeeIdList;
    private ArrayList<EmployeeTable> selectedEmployeeTableList;
    private ArrayList<String> selectedOperationIdList;
    private ArrayList<OperationTable> selectedOperationTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_rating);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && resultCode == RESULT_OK) {

            if (requestCode == Constants.SELECT_EMPLOYEE_REQUEST) {

                selectedEmployeeIdList = new ArrayList<>();
                selectedEmployeeIdList.addAll(data.getStringArrayListExtra("SELECTED_EMPLOYEE"));

                selectedEmployeeTableList = new ArrayList<>();

                for (String sId : selectedEmployeeIdList) {
                    Log.d(TAG, "onActivityResult: EM " + sId);
                    selectedEmployeeTableList.add(AppDatabase.getInstance(this).employeeTableDao().getEmployeeById(sId));
                }
                if (selectedEmployeeTableList != null && !selectedEmployeeTableList.isEmpty()) {
                    tvSelectEmployee.setText(selectedEmployeeTableList.get(0).getEmployeeName());
                } else {
                    showToast("No employee selected.");
                    tvSelectEmployee.setText("Select Employee");
                }
            } else if (Constants.SELECT_OPERATION_REQUEST == requestCode) {

                selectedOperationIdList = new ArrayList<>();
                selectedOperationIdList.addAll(data.getStringArrayListExtra("SELECTED_OPERATION"));

                selectedOperationTableList = new ArrayList<>();

                for (String sId : selectedOperationIdList) {
                    Log.d(TAG, "onActivityResult: OP " + sId);
                    selectedOperationTableList.add(AppDatabase.getInstance(this).operationTableDao().getOperationById(sId));
                }
                if (selectedOperationTableList != null && !selectedOperationTableList.isEmpty()) {
                    tvSelectOperation.setText(selectedOperationTableList.get(0).getOperationName());
                } else {
                    showToast("No operation selected.");
                    tvSelectOperation.setText("Select Operation");
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sEmployeeRatingId = i.getStringExtra("EMPLOYEE_RATING_ID");

            Log.d(TAG, "initialDataSetup: " + sEmployeeRatingId);
            if (!sCurrentMode.equalsIgnoreCase("add") && (sEmployeeRatingId == null || sEmployeeRatingId.isEmpty())) {
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

        tvSAMValueLabel = findViewById(R.id.tvSAMValueLabel);
        etSAMValue = findViewById(R.id.etSAMValue);

        tvEmployeeLabel = findViewById(R.id.tvEmployeeLabel);
        tvSelectEmployee = findViewById(R.id.tvSelectEmployee);
        tvOperationLabel = findViewById(R.id.tvOperationLabel);
        tvSelectOperation = findViewById(R.id.tvSelectOperation);
        tvRatingTimeLabel = findViewById(R.id.tvRatingTimeLabel);
        tvSelectRatingTime = findViewById(R.id.tvSelectRatingTime);

        tvSAMValueLabel.setTypeface(utilityFile.getTfMedium());
        etSAMValue.setTypeface(utilityFile.getTfRegular());
        tvEmployeeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectEmployee.setTypeface(utilityFile.getTfMedium());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectOperation.setTypeface(utilityFile.getTfMedium());
        tvRatingTimeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectRatingTime.setTypeface(utilityFile.getTfMedium());

        tvSelectEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddEmployeeRatingActivity.this, SelectEmployeeActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_EMPLOYEE_REQUEST);
            }
        });

        tvSelectOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddEmployeeRatingActivity.this, SelectOperationActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_OPERATION", selectedOperationIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_OPERATION_REQUEST);
            }
        });

        tvSelectRatingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDatePicker.showDatePicker("SELECT_DATE");

            }
        });

        datePickerSetup();

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        EmployeeRatingTable employeeRatingTable = AppDatabase.getInstance(this).employeeRatingTableDao().getEmployeeRatingById(sEmployeeRatingId);

        if (employeeRatingTable != null) {

            sPreviousSAMValue = String.valueOf(employeeRatingTable.getSamValue());
            etSAMValue.setText(employeeRatingTable.getSamValue() + "");

            String sOperationId = employeeRatingTable.getOperationId();
            sPreviousOperationId = sOperationId;
            OperationTable operationTable = AppDatabase.getInstance(this).operationTableDao().getOperationById(sOperationId);
            if (operationTable != null) {
                tvSelectOperation.setText(operationTable.getOperationName());
                selectedOperationIdList = new ArrayList<>();
                selectedOperationTableList = new ArrayList<>();

                selectedOperationIdList.add(sOperationId);
                selectedOperationTableList.add(operationTable);
            }


            String sEmployeeId = employeeRatingTable.getEmployeeId();
            sPreviousEmployeeId = sEmployeeId;
            EmployeeTable employeeTable = AppDatabase.getInstance(this).employeeTableDao().getEmployeeById(sEmployeeId);
            if (employeeTable != null) {
                tvSelectEmployee.setText(employeeTable.getEmployeeName());
                selectedEmployeeTableList = new ArrayList<>();
                selectedEmployeeIdList = new ArrayList<>();

                selectedEmployeeIdList.add(sEmployeeId);
                selectedEmployeeTableList.add(employeeTable);
            }

            lRatingTimestamp = employeeRatingTable.getRatingTimestamp();
            lPreviousRateTimestamp = lRatingTimestamp;
            tvSelectRatingTime.setText(utilityFile.getStringFromLongTimestamp(lRatingTimestamp,
                    Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));

        }

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

        etSAMValue.setEnabled(isEditable);
        tvSAMValueLabel.setClickable(isEditable);
        tvSelectEmployee.setClickable(false);
        tvEmployeeLabel.setClickable(false);
        tvSelectOperation.setClickable(isEditable);
        tvOperationLabel.setClickable(isEditable);
        tvRatingTimeLabel.setClickable(isEditable);
        tvSelectRatingTime.setClickable(isEditable);

    }

    public void datePickerSetup() {

        MyDatePickerInterface myDatePickerInterface = new MyDatePickerInterface() {
            @Override
            public void getCalendarTime(long lSelectedTimestamp, String sRequestType) {

                if (sRequestType.matches("SELECT_DATE")) {

                    lRatingTimestamp = lSelectedTimestamp;
                    tvSelectRatingTime.setText(utilityFile.getStringFromLongTimestamp(lRatingTimestamp, Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));
                }

            }

        };

        myDatePicker = new MyDatePicker(this, myDatePickerInterface);

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

        if (selectedEmployeeTableList == null || selectedEmployeeTableList.isEmpty()) {
            showToast("Select Employee.");
            return;
        }

        String sSAMValue = etSAMValue.getText().toString();

        if (sSAMValue == null || sSAMValue.isEmpty()) {
            showToast("Enter SAM Value.");
            return;
        }

        if (selectedOperationTableList == null || selectedOperationTableList.isEmpty()) {
            showToast("Select Operation.");
            return;
        }

        if (lRatingTimestamp == 0) {
            showToast("Select Rating Time.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)
                && sPreviousSAMValue != null && sPreviousOperationId != null && sPreviousEmployeeId != null
                && lPreviousRateTimestamp != 0 && selectedEmployeeIdList.get(0).matches(sPreviousEmployeeId)
                && sSAMValue.matches(sPreviousSAMValue) && lPreviousRateTimestamp == lRatingTimestamp
                && selectedOperationIdList.get(0).matches(sPreviousOperationId)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("rating", sSAMValue);
                jsonObject.put("employeeId", selectedEmployeeIdList.get(0));
                jsonObject.put("operationId", selectedOperationIdList.get(0));
                jsonObject.put("ratingTimestamp", String.valueOf(lRatingTimestamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sEmployeeRatingId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_EMPLOYEE_RATING,
                        Constants.BASE_SERVER_URL + "update_employee_rating",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("rating", sSAMValue);
            params.put("employeeId", selectedEmployeeIdList.get(0));
            params.put("operationId", selectedOperationIdList.get(0));
            params.put("ratingTimestamp", String.valueOf(lRatingTimestamp));

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_EMPLOYEE_RATING,
                    Constants.BASE_SERVER_URL + "add_employee_rating",
                    params);
        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_EMPLOYEE_RATING)) {
                    addEmployeeRatingResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_EMPLOYEE_RATING)) {
                    updateEmployeeRatingResponse(response);
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

    private void addEmployeeRatingResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeRatingJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeRatingObject(employeeRatingJSONObject);

                showToast("Employee Rating Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateEmployeeRatingResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeRatingJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeRatingObject(employeeRatingJSONObject);

                showToast("Employee Rating Updated Successfully.");

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
                tvTitleLabel.setText("Add Rating");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Rating");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Rating Detail");
                break;
            default:
                tvTitleLabel.setText("Rating Detail");
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
