package com.arvind.linebalancing.activity.employee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.arvind.linebalancing.activity.designation.SelectDesignationActivity;
import com.arvind.linebalancing.adapter.EmployeeMachineListAdapter;
import com.arvind.linebalancing.adapter.EmployeeOperationListAdapter;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEmployeeActivity extends AppCompatActivity {

    private static final String TAG = AddEmployeeActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvEmployeeNameLabel;
    private EditText etEmployeeName;
    private TextView tvEmployeeNoLabel;
    private EditText etEmployeeNo;
    private TextView tvEmployeeEmailLabel;
    private EditText etEmployeeEmail;
    private TextView tvEmployeePasswordLabel;
    private EditText etEmployeePassword;
    private TextView tvDesignationLabel;
    private TextView tvSelectDesignation;
    private TextView tvMachineLabel;
    private RecyclerView rvMachine;
    private TextView tvOperationLabel;
    private RecyclerView rvOperation;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sEmployeeId;
    private String sPreviousName;
    private String sPreviousPassword;
    private String sPreviousNo;
    private String sPreviousEmail;
    private String sPreviousDesignationId;

    private ArrayList<String> selectedDesignationIdList;
    private ArrayList<DesignationTable> selectedDesignationTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == Constants.SELECT_DESIGNATION_REQUEST) {

                selectedDesignationIdList = new ArrayList<>();
                selectedDesignationIdList.addAll(data.getStringArrayListExtra("SELECTED_DESIGNATION"));

                selectedDesignationTableList = new ArrayList<>();

                for (String sId : selectedDesignationIdList) {
                    Log.d(TAG, "onActivityResult: " + sId);
                    selectedDesignationTableList.add(AppDatabase.getInstance(this).designationTableDao().getDesignationById(sId));
                }
                if (selectedDesignationTableList != null && !selectedDesignationTableList.isEmpty()) {
                    tvSelectDesignation.setText(selectedDesignationTableList.get(0).getDesignationName());
                } else {
                    showToast("No designation selected.");
                    tvSelectDesignation.setText("Select Designation");
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

            if (!sCurrentMode.equalsIgnoreCase("add") && (sEmployeeId == null || sEmployeeId.isEmpty())) {
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

        tvEmployeeNameLabel = findViewById(R.id.tvEmployeeNameLabel);
        etEmployeeName = findViewById(R.id.etEmployeeName);
        tvEmployeeNoLabel = findViewById(R.id.tvEmployeeNoLabel);
        etEmployeeNo = findViewById(R.id.etEmployeeNo);
        tvEmployeeEmailLabel = findViewById(R.id.tvEmployeeEmailLabel);
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        tvEmployeePasswordLabel = findViewById(R.id.tvEmployeePasswordLabel);
        etEmployeePassword = findViewById(R.id.etEmployeePassword);
        tvDesignationLabel = findViewById(R.id.tvDesignationLabel);
        tvMachineLabel = findViewById(R.id.tvMachineLabel);
        tvOperationLabel = findViewById(R.id.tvOperationLabel);
        tvSelectDesignation = findViewById(R.id.tvSelectDesignation);

        rvMachine = findViewById(R.id.rvMachine);
        rvOperation = findViewById(R.id.rvOperation);

        rvOperation.setLayoutManager(new LinearLayoutManager(this));
        rvMachine.setLayoutManager(new LinearLayoutManager(this));

        tvOperationLabel.setVisibility(View.GONE);
        tvMachineLabel.setVisibility(View.GONE);
        rvMachine.setVisibility(View.GONE);
        rvOperation.setVisibility(View.GONE);

        tvEmployeeNameLabel.setTypeface(utilityFile.getTfMedium());
        etEmployeeName.setTypeface(utilityFile.getTfRegular());
        tvEmployeeNoLabel.setTypeface(utilityFile.getTfMedium());
        etEmployeeNo.setTypeface(utilityFile.getTfRegular());
        tvEmployeeEmailLabel.setTypeface(utilityFile.getTfMedium());
        etEmployeeEmail.setTypeface(utilityFile.getTfRegular());
        tvEmployeePasswordLabel.setTypeface(utilityFile.getTfMedium());
        etEmployeePassword.setTypeface(utilityFile.getTfRegular());
        tvDesignationLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectDesignation.setTypeface(utilityFile.getTfMedium());
        tvMachineLabel.setTypeface(utilityFile.getTfMedium());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());

        tvSelectDesignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(AddEmployeeActivity.this, SelectDesignationActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_DESIGNATION", selectedDesignationIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_DESIGNATION_REQUEST);

            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        EmployeeTable employeeTable = AppDatabase.getInstance(this).employeeTableDao()
                .getEmployeeById(sEmployeeId);

        if (employeeTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sPreviousName = employeeTable.getEmployeeName();
        etEmployeeName.setText(employeeTable.getEmployeeName());

        sPreviousEmail = employeeTable.getEmployeeEmail();
        sPreviousNo = employeeTable.getEmployeeNo();
        etEmployeeNo.setText(employeeTable.getEmployeeNo());

        String sDesignationId = employeeTable.getDesignationId();
        sPreviousDesignationId = sDesignationId;
        DesignationTable designationTable = AppDatabase.getInstance(this).designationTableDao().getDesignationById(sDesignationId);

        selectedDesignationIdList = new ArrayList<>();
        selectedDesignationTableList = new ArrayList<>();

        selectedDesignationIdList.add(sDesignationId);
        selectedDesignationTableList.add(designationTable);

        tvSelectDesignation.setText(designationTable.getDesignationName());
        etEmployeeEmail.setText(employeeTable.getEmployeeEmail());

        etEmployeePassword.setVisibility(View.GONE);
        tvEmployeePasswordLabel.setVisibility(View.GONE);

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            setOperations();
            setMachines();
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
//            ivSave.setVisibility(View.GONE);
        }

        titleLabel();

    }

    private void setOperations() {

        rvOperation.setVisibility(View.VISIBLE);
        tvOperationLabel.setVisibility(View.VISIBLE);

        ArrayList<String> operationIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .employeeOperationMapTableDao().getAllOperationByEmployeeId(sEmployeeId));

        if (!operationIdArrayList.isEmpty()) {
            EmployeeOperationListAdapter adapter= new EmployeeOperationListAdapter(this, sEmployeeId, operationIdArrayList);
            rvOperation.setAdapter(adapter);
        } else {
            rvOperation.setVisibility(View.GONE);
            tvOperationLabel.setVisibility(View.GONE);
        }

    }

    private void setMachines() {

        rvMachine.setVisibility(View.VISIBLE);
        tvMachineLabel.setVisibility(View.VISIBLE);

        ArrayList<String> machineIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .employeeMachineMapTableDao().getAllMachineByEmployeeId(sEmployeeId));

        if (!machineIdArrayList.isEmpty()) {
            EmployeeMachineListAdapter adapter = new EmployeeMachineListAdapter(this, sEmployeeId, machineIdArrayList);
            rvMachine.setAdapter(adapter);
        } else {
            rvMachine.setVisibility(View.GONE);
            tvMachineLabel.setVisibility(View.GONE);
        }

    }

    private void enableEditMode(boolean isEditable) {

        etEmployeeName.setEnabled(isEditable);
        tvEmployeeNameLabel.setClickable(isEditable);
        etEmployeePassword.setEnabled(isEditable);
        tvEmployeePasswordLabel.setClickable(isEditable);
        etEmployeeEmail.setEnabled(isEditable);
        tvEmployeeEmailLabel.setClickable(isEditable);
        etEmployeeNo.setEnabled(false);
        tvEmployeeNoLabel.setClickable(false);
        tvSelectDesignation.setClickable(isEditable);
        tvDesignationLabel.setClickable(isEditable);

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

        String sEmployeeName = etEmployeeName.getText().toString();

        if (sEmployeeName == null || sEmployeeName.isEmpty()) {
            showToast("Enter Employee Name.");
            return;
        }

        String sEmployeeNo = etEmployeeNo.getText().toString();

        if (sEmployeeNo == null || sEmployeeNo.isEmpty()) {
            showToast("Enter Employee No.");
            return;
        }

        String sEmployeeEmail = etEmployeeEmail.getText().toString();

        if (sEmployeeEmail == null || sEmployeeEmail.isEmpty()) {
            showToast("Enter Employee Email.");
            return;
        }

        String sEmployeePassword = etEmployeePassword.getText().toString();

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            if (sEmployeePassword == null || sEmployeePassword.isEmpty()) {
                showToast("Enter Employee Password.");
                return;
            }

            if (!utilityFile.isPasswordValid(sEmployeePassword)) {
                showToast("Invalid! required more than 5 characters");
                return;
            }

        }

        if (selectedDesignationIdList == null || selectedDesignationIdList.isEmpty()) {
            showToast("Select Designation.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)
                && sPreviousDesignationId != null && sPreviousEmail != null && sPreviousName != null
                && sPreviousNo != null && selectedDesignationIdList.get(0).matches(sPreviousDesignationId)
                && sEmployeeNo.matches(sPreviousNo) && sEmployeeName.matches(sPreviousName)
                && sEmployeeEmail.matches(sPreviousEmail)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("employeeNo", sEmployeeNo);
                jsonObject.put("employeeName", sEmployeeName);
                jsonObject.put("employeeEmail", sEmployeeEmail);
                jsonObject.put("designationId", selectedDesignationIdList.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sEmployeeId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_EMPLOYEE,
                        Constants.BASE_SERVER_URL + "update_employee",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("employeeNo", sEmployeeNo);
        params.put("employeeName", sEmployeeName);
        params.put("employeeEmail", sEmployeeEmail);
        params.put("employeePassword", sEmployeePassword);
        params.put("designationId", selectedDesignationIdList.get(0));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_EMPLOYEE,
                Constants.BASE_SERVER_URL + "add_employee",
                params);

        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_EMPLOYEE)) {
                    addEmployeeResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_EMPLOYEE)) {
                    updateEmployeeResponse(response);
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

    private void addEmployeeResponse(String sResponse) {
        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeObject(employeeJSONObject);

                showToast("Employee Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateEmployeeResponse(String sResponse) {
        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject employeeJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeEmployeeObject(employeeJSONObject);

                showToast("Employee Updated Successfully.");

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
                tvTitleLabel.setText("Add Employee");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Employee");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Employee Detail");
                break;
            default:
                tvTitleLabel.setText("Employee Detail");
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
