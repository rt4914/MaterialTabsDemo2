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
import com.arvind.linebalancing.activity.line.SelectLineActivity;
import com.arvind.linebalancing.activity.machine.SelectMachineActivity;
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.ConnectionTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.table.MachineTable;
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

public class AddConnectionActivity extends AppCompatActivity {

    private static final String TAG = AddConnectionActivity.class.getSimpleName();

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
    private String sConnectionId;
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
        setContentView(R.layout.activity_add_connection);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && resultCode == RESULT_OK) {

            if (requestCode == Constants.SELECT_LINE_REQUEST) {

                selectedLineIdList = new ArrayList<>();
                selectedLineIdList.addAll(data.getStringArrayListExtra("SELECTED_LINE"));

                ArrayList<LineTable> selectedLineTableList = new ArrayList<>();

                for (String sId : selectedLineIdList) {
                    selectedLineTableList.add(AppDatabase.getInstance(this).lineTableDao().getLineById(sId));
                }
                if (selectedLineTableList != null && !selectedLineTableList.isEmpty()) {
                    tvSelectLine.setText(selectedLineTableList.get(0).getLineName());
                } else {
                    showToast("No line selected.");
                    tvSelectLine.setText("Select Line");
                }
            } else if (requestCode == Constants.SELECT_EMPLOYEE_REQUEST) {

                selectedEmployeeIdList = new ArrayList<>();
                selectedEmployeeIdList.addAll(data.getStringArrayListExtra("SELECTED_EMPLOYEE"));

                ArrayList<EmployeeTable> selectedEmployeeTableList = new ArrayList<>();

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

                ArrayList<OperationTable> selectedOperationTableList = new ArrayList<>();

                for (String sId : selectedOperationIdList) {
                    selectedOperationTableList.add(AppDatabase.getInstance(this).operationTableDao().getOperationById(sId));
                }
                if (selectedOperationTableList != null && !selectedOperationTableList.isEmpty()) {
                    tvSelectOperation.setText(selectedOperationTableList.get(0).getOperationName());
                } else {
                    showToast("No operation selected.");
                    tvSelectOperation.setText("Select Operation");
                }

            } else if (requestCode == Constants.SELECT_MACHINE_REQUEST) {

                selectedMachineIdList = new ArrayList<>();
                selectedMachineIdList.addAll(data.getStringArrayListExtra("SELECTED_MACHINE"));

                ArrayList<MachineTable> selectedMachineTableList = new ArrayList<>();

                for (String sId : selectedMachineIdList) {
                    selectedMachineTableList.add(AppDatabase.getInstance(this).machineTableDao().getMachineById(sId));
                }
                if (selectedMachineTableList != null && !selectedMachineTableList.isEmpty()) {
                    tvSelectMachine.setText(selectedMachineTableList.get(0).getMachineName());
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
            sConnectionId = i.getStringExtra("CONNECTION_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sConnectionId == null || sConnectionId.isEmpty())) {
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

        tvSelectEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddConnectionActivity.this, SelectEmployeeActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_EMPLOYEE_REQUEST);
            }
        });

        tvSelectOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddConnectionActivity.this, SelectOperationActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_OPERATION", selectedOperationIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_OPERATION_REQUEST);
            }
        });

        tvSelectMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddConnectionActivity.this, SelectMachineActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_MACHINE", selectedMachineIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_MACHINE_REQUEST);
            }
        });

        tvSelectLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddConnectionActivity.this, SelectLineActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_LINE", selectedLineIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_LINE_REQUEST);
            }
        });

        tvSelectTime.setOnClickListener(new View.OnClickListener() {
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

        ConnectionTable connectionTable = AppDatabase.getInstance(this).connectionTableDao().getConnectionById(sConnectionId);

        if (connectionTable != null) {

            sPreviousLineId = connectionTable.getLineId();
            sPreviousOperationId = connectionTable.getOperationId();
            sPreviousMachineId = connectionTable.getMachineId();
            sPreviousEmployeeId = connectionTable.getEmployeeId();
            lPreviousTimestamp = connectionTable.getTimestamp();

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

    public void datePickerSetup() {

        MyDatePickerInterface myDatePickerInterface = new MyDatePickerInterface() {
            @Override
            public void getCalendarTime(long lSelectedTimestamp, String sRequestType) {

                if (sRequestType.matches("SELECT_DATE")) {

                    lTimestamp = lSelectedTimestamp;
                    tvSelectTime.setText(utilityFile.getStringFromLongTimestamp(lTimestamp, Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));
                }

            }

        };

        myDatePicker = new MyDatePicker(this, myDatePickerInterface);

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

        if (selectedLineIdList == null || selectedLineIdList.isEmpty()) {
            showToast("Select Line.");
            return;
        }

        if (selectedMachineIdList == null || selectedMachineIdList.isEmpty()) {
            showToast("Select Machine.");
            return;
        }

        if (selectedOperationIdList == null || selectedOperationIdList.isEmpty()) {
            showToast("Select Operation.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sConnectionId != null && sPreviousEmployeeId != null && sPreviousLineId != null
                && sPreviousMachineId != null && sPreviousOperationId != null
                && selectedEmployeeIdList.get(0).matches(sPreviousEmployeeId) && selectedLineIdList.get(0).matches(sPreviousLineId)
                && selectedMachineIdList.get(0).matches(sPreviousMachineId) && selectedOperationIdList.get(0).matches(sPreviousOperationId)) {

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
            jsonObject.put("employeeId", selectedEmployeeIdList.get(0));
            jsonObject.put("lineId", selectedLineIdList.get(0));
            jsonObject.put("machineId", selectedMachineIdList.get(0));
            jsonObject.put("operationId", selectedOperationIdList.get(0));
            jsonObject.put("timestamp", lTimestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", sConnectionId);
            params.put("updated_data", jsonObject.toString());

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_CONNECTION,
                    Constants.BASE_SERVER_URL + "update_connection",
                    params);

        }

    }

    private void sendAddRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("employeeId", selectedEmployeeIdList.get(0));
        params.put("lineId", selectedLineIdList.get(0));
        params.put("machineId", selectedMachineIdList.get(0));
        params.put("operationId", selectedOperationIdList.get(0));
        params.put("timestamp", String.valueOf(lTimestamp));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_CONNECTION,
                Constants.BASE_SERVER_URL + "add_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_CONNECTION)) {
                    addConnectionResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_CONNECTION)) {
                    updateConnectionResponse(response);
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

    private void addConnectionResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject connectionJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeConnectionObject(connectionJSONObject);

                showToast("Connection added successfully.");
                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateConnectionResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject connectionJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeConnectionObject(connectionJSONObject);

                showToast("Connection updated successfully.");

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
                tvTitleLabel.setText("Add Connection");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Connection");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("View Connection");
                break;
            default:
                tvTitleLabel.setText("View Connection");
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
