package com.arvind.linebalancing.activity.connection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.arvind.linebalancing.activity.employee.SelectEmployeeActivity;
import com.arvind.linebalancing.activity.machine.SelectMachineActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeMachineMapTable;
import com.arvind.linebalancing.table.EmployeeMachineMapTable;
import com.arvind.linebalancing.table.EmployeeOperationMapTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyDatePicker;
import com.arvind.linebalancing.utilities.MyDatePickerInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeMachineMapActivity extends AppCompatActivity {

    private static final String TAG = EmployeeMachineMapActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
   
    private TextView tvMachineLabel;
    private TextView tvSelectMachine;
    private TextView tvEmployeeLabel;
    private TextView tvSelectEmployee;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    int iMachineCount = 0;
    private long lMachineMapTimestamp = 0;
    private String sCurrentMode;
    private String sEmployeeId;
    private String sEmployeeMachineMapId;
    private String sPreviousMachineId;

    private ArrayList<String> selectedEmployeeIdList;
    private ArrayList<EmployeeTable> selectedEmployeeTableList;
    private ArrayList<String> selectedMachineIdList;
    private ArrayList<String> selectedPreviousMachineIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_machine_map);

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
            } else if (requestCode == Constants.SELECT_MACHINE_REQUEST) {

                selectedMachineIdList = new ArrayList<>();
                selectedMachineIdList.addAll(data.getStringArrayListExtra("SELECTED_MACHINE"));

                if (selectedMachineIdList != null && !selectedMachineIdList.isEmpty()) {
                    tvSelectMachine.setText(selectedMachineIdList.size() + " machines selected");
                } else {
                    showToast("No machine selected.");
                    tvSelectMachine.setText("Select Machine");
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
            sEmployeeMachineMapId = i.getStringExtra("EMPLOYEE_MACHINE_MAP_ID");

            Log.d(TAG, "initialDataSetup: " + sEmployeeId);
            if (!sCurrentMode.equalsIgnoreCase("add") && (sEmployeeId == null || sEmployeeId.isEmpty())&& (sEmployeeMachineMapId == null || sEmployeeMachineMapId.isEmpty())) {
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
        tvMachineLabel = findViewById(R.id.tvMachineLabel);
        tvSelectMachine = findViewById(R.id.tvSelectMachine);
        tvEmployeeLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectEmployee.setTypeface(utilityFile.getTfMedium());
        tvMachineLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectMachine.setTypeface(utilityFile.getTfMedium());

        tvSelectEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(EmployeeMachineMapActivity.this, SelectEmployeeActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_EMPLOYEE_REQUEST);
            }
        });

        tvSelectMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(EmployeeMachineMapActivity.this, SelectMachineActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", true);
                ii.putExtra("SELECTED_MACHINE", selectedMachineIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_MACHINE_REQUEST);
            }
        });
        
        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        EmployeeMachineMapTable employeeMachineMapTable = AppDatabase.getInstance(this)
                .employeeMachineMapTableDao().getEmployeeMachineTableById(sEmployeeMachineMapId);

        if (employeeMachineMapTable != null) {

            selectedEmployeeIdList = new ArrayList<>();
            selectedMachineIdList = new ArrayList<>();
            selectedEmployeeIdList.add(employeeMachineMapTable.getEmployeeId());
            selectedMachineIdList.add(employeeMachineMapTable.getMachineId());

            sPreviousMachineId = employeeMachineMapTable.getMachineId();

            EmployeeTable employeeTable = AppDatabase.getInstance(this).employeeTableDao()
                    .getEmployeeById(employeeMachineMapTable.getEmployeeId());

            if (employeeTable != null) {
                tvSelectEmployee.setText(employeeTable.getEmployeeName());
            }

            tvSelectMachine.setText("1 machines selected");


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
        tvSelectMachine.setClickable(isEditable);
        tvMachineLabel.setClickable(isEditable);
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
        
        if (selectedMachineIdList == null || selectedMachineIdList.isEmpty()) {
            showToast("Select Machine.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sEmployeeId != null
                && selectedMachineIdList.get(0).equals(sPreviousMachineId)) {

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
            jsonObject.put("machineId", selectedMachineIdList.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", sEmployeeMachineMapId);
            params.put("updated_data", jsonObject.toString());

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_EMPLOYEE_MACHINE_CONNECTION,
                    Constants.BASE_SERVER_URL + "update_employee_machine_connection",
                    params);

        }

    }

    private void sendAddRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("employeeId", selectedEmployeeIdList.get(0));
        params.put("machineId", selectedMachineIdList.get(iMachineCount));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_EMPLOYEE_MACHINE_CONNECTION,
                Constants.BASE_SERVER_URL + "add_employee_machine_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_EMPLOYEE_MACHINE_CONNECTION)) {
                    addEmployeeMachineMapResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_EMPLOYEE_MACHINE_CONNECTION)) {
                    updateEmployeeMachineMapResponse(response);
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

    private void addEmployeeMachineMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeMachineMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeMachineMapObject(employeeMachineMapJSONObject);

                if (iMachineCount + 1 == selectedMachineIdList.size()) {
                    showToast("Employee & Machines connection added successfully.");
                    nextActivity();
                } else {
                    iMachineCount++;
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

    private void updateEmployeeMachineMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeMachineMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeMachineMapObject(employeeMachineMapJSONObject);

                showToast("Employee & Machines connection updated successfully.");

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
                tvTitleLabel.setText("Employee Mac. Connect");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Employee Mac. Connect");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Employee Mac. Connect");
                break;
            default:
                tvTitleLabel.setText("Employee Mac. Connect");
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
