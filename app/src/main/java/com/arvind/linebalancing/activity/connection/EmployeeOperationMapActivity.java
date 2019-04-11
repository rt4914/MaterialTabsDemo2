package com.arvind.linebalancing.activity.connection;

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
import com.arvind.linebalancing.activity.MainActivity;
import com.arvind.linebalancing.activity.employee.SelectEmployeeActivity;
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeOperationMapTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.MachineOperationMapTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeOperationMapActivity extends AppCompatActivity {

    private static final String TAG = EmployeeOperationMapActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;

    private TextView tvOperationLabel;
    private TextView tvSelectOperation;
    private TextView tvEmployeeLabel;
    private TextView tvSelectEmployee;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private int iOperationCount = 0;
    private long lOperationMapTimestamp = 0;
    private String sCurrentMode;
    private String sEmployeeId;
    private String sEmployeeOperationMapId;
    private String sPreviousOperationId;

    private ArrayList<String> selectedEmployeeIdList;
    private ArrayList<EmployeeTable> selectedEmployeeTableList;
    private ArrayList<String> selectedOperationIdList;
    private ArrayList<String> selectedPreviousOperationIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_operation_map);

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
                    selectedEmployeeTableList.add(AppDatabase.getInstance(this).employeeTableDao().getEmployeeById(sId));
                }
                if (selectedEmployeeTableList != null && !selectedEmployeeTableList.isEmpty()) {
                    tvSelectEmployee.setText(selectedEmployeeTableList.get(0).getEmployeeName());
                } else {
                    showToast("No employee selected.");
                    tvSelectEmployee.setText("Select Employee");
                }
            } else if (requestCode == Constants.SELECT_OPERATION_REQUEST) {

                selectedOperationIdList = new ArrayList<>();
                selectedOperationIdList.addAll(data.getStringArrayListExtra("SELECTED_OPERATION"));

                if (selectedOperationIdList != null && !selectedOperationIdList.isEmpty()) {
                    tvSelectOperation.setText(selectedOperationIdList.size() + " operations selected");
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
            sEmployeeId = i.getStringExtra("EMPLOYEE_ID");
            sEmployeeOperationMapId = i.getStringExtra("EMPLOYEE_OPERATION_MAP_ID");

            Log.d(TAG, "initialDataSetup: " + sEmployeeId);
            if (!sCurrentMode.equalsIgnoreCase("add") && (sEmployeeId == null || sEmployeeId.isEmpty()) && (sEmployeeOperationMapId == null || sEmployeeOperationMapId.isEmpty())) {
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
        tvEmployeeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectEmployee.setTypeface(utilityFile.getTfMedium());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectOperation.setTypeface(utilityFile.getTfMedium());

        tvSelectEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(EmployeeOperationMapActivity.this, SelectEmployeeActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_EMPLOYEE_REQUEST);
            }
        });

        tvSelectOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(EmployeeOperationMapActivity.this, SelectOperationActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", true);
                ii.putExtra("SELECTED_OPERATION", selectedOperationIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_OPERATION_REQUEST);
            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        EmployeeOperationMapTable employeeOperationMapTable = AppDatabase.getInstance(this)
                .employeeOperationMapTableDao().getEmployeeOperationTableById(sEmployeeOperationMapId);

        if (employeeOperationMapTable != null) {

            selectedEmployeeIdList = new ArrayList<>();
            selectedOperationIdList = new ArrayList<>();
            selectedEmployeeIdList.add(employeeOperationMapTable.getEmployeeId());
            selectedOperationIdList.add(employeeOperationMapTable.getOperationId());

            sPreviousOperationId = employeeOperationMapTable.getOperationId();

            EmployeeTable employeeTable = AppDatabase.getInstance(this).employeeTableDao()
                    .getEmployeeById(employeeOperationMapTable.getEmployeeId());

            if (employeeTable != null) {
                tvSelectEmployee.setText(employeeTable.getEmployeeName());
            }

            tvSelectOperation.setText("1 operations selected");


        } else {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
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

        tvSelectEmployee.setClickable(false);
        tvEmployeeLabel.setClickable(false);
        tvSelectOperation.setClickable(isEditable);
        tvOperationLabel.setClickable(isEditable);
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

        if (selectedEmployeeIdList == null || selectedEmployeeIdList.isEmpty()) {
            showToast("Select Employee.");
            return;
        }

        if (selectedOperationIdList == null || selectedOperationIdList.isEmpty()) {
            showToast("Select Operation.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sEmployeeId != null
                && selectedOperationIdList.get(0).equals(sPreviousOperationId)) {

            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            sendUpdateRequest();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {
            sendAddRequest();
        }

    }

    private void sendUpdateRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("employeeId", sEmployeeId);
            jsonObject.put("operationId", selectedOperationIdList.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", sEmployeeOperationMapId);
            params.put("updated_data", jsonObject.toString());

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_EMPLOYEE_OPERATION_CONNECTION,
                    Constants.BASE_SERVER_URL + "update_employee_operation_connection",
                    params);

        }

    }

    private void sendAddRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("employeeId", selectedEmployeeIdList.get(0));
        params.put("operationId", selectedOperationIdList.get(iOperationCount));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_EMPLOYEE_OPERATION_CONNECTION,
                Constants.BASE_SERVER_URL + "add_employee_operation_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_EMPLOYEE_OPERATION_CONNECTION)) {
                    addEmployeeOperationMapResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_EMPLOYEE_OPERATION_CONNECTION)) {
                    updateEmployeeOperationMapResponse(response);
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

    private void addEmployeeOperationMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeOperationMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeOperationMapObject(employeeOperationMapJSONObject);

                if (iOperationCount + 1 == selectedOperationIdList.size()) {
                    showToast("Employee & Operations connection added successfully.");
                    nextActivity();
                } else {
                    iOperationCount++;
                    sendAddRequest();
                }

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateEmployeeOperationMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeOperationMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeOperationMapObject(employeeOperationMapJSONObject);

                showToast("Employee & Operations connection updated successfully.");

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
                tvTitleLabel.setText("Employee Op. Connect");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Employee Op. Connect");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Employee Op. Connect");
                break;
            default:
                tvTitleLabel.setText("Employee Op. Connect");
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
