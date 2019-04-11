package com.arvind.linebalancing.activity.machine;

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
import com.arvind.linebalancing.activity.connection.MachineOperationMapActivity;
import com.arvind.linebalancing.adapter.MachineOperationListAdapter;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddMachineActivity extends AppCompatActivity {

    private static final String TAG = AddMachineActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvMachineNameLabel;
    private EditText etMachineName;
    private TextView tvAssignedNameLabel;
    private EditText etAssignedName;
    private TextView tvShortNameLabel;
    private EditText etShortName;
    private TextView tvMachineIndexLabel;
    private EditText etMachineIndex;
    private TextView tvOperationLabel;
    private RecyclerView rvOperation;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sMachineId;
    private String sPreviousMachineName;
    private String sPreviousAssignedName;
    private String sPreviousShortName;
    private String sPreviousMachineIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sMachineId = i.getStringExtra("MACHINE_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sMachineId == null || sMachineId.isEmpty())) {
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

        tvMachineNameLabel = findViewById(R.id.tvMachineNameLabel);
        etMachineName = findViewById(R.id.etMachineName);
        tvAssignedNameLabel = findViewById(R.id.tvAssignedNameLabel);
        etAssignedName = findViewById(R.id.etAssignedName);
        tvShortNameLabel = findViewById(R.id.tvShortNameLabel);
        etShortName = findViewById(R.id.etShortName);
        tvOperationLabel = findViewById(R.id.tvOperationLabel);
        etMachineIndex = findViewById(R.id.etMachineIndex);
        tvMachineIndexLabel = findViewById(R.id.tvMachineIndexLabel);

        rvOperation = findViewById(R.id.rvOperation);
        rvOperation.setLayoutManager(new LinearLayoutManager(this));

        tvOperationLabel.setVisibility(View.GONE);
        rvOperation.setVisibility(View.GONE);

        tvMachineNameLabel.setTypeface(utilityFile.getTfMedium());
        etMachineName.setTypeface(utilityFile.getTfRegular());
        tvMachineIndexLabel.setTypeface(utilityFile.getTfMedium());
        etMachineIndex.setTypeface(utilityFile.getTfRegular());
        tvAssignedNameLabel.setTypeface(utilityFile.getTfMedium());
        etAssignedName.setTypeface(utilityFile.getTfRegular());
        tvShortNameLabel.setTypeface(utilityFile.getTfMedium());
        etShortName.setTypeface(utilityFile.getTfRegular());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        MachineTable machineTable = AppDatabase.getInstance(this).machineTableDao()
                .getMachineById(sMachineId);

        if (machineTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }
        sPreviousMachineName = machineTable.getMachineName();
        sPreviousAssignedName = machineTable.getAssignedName();
        sPreviousShortName = machineTable.getShortName();
        sPreviousMachineIndex = machineTable.getMachineIndex();

        etMachineName.setText(machineTable.getMachineName());
        etAssignedName.setText(machineTable.getAssignedName());
        etShortName.setText(machineTable.getShortName());
        etMachineIndex.setText(machineTable.getMachineIndex());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            setOperations();
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
//            ivSave.setVisibility(View.GONE);
        }

        titleLabel();

    }

    private void setOperations() {

        rvOperation.setVisibility(View.VISIBLE);
        tvOperationLabel.setVisibility(View.VISIBLE);

        ArrayList<String> operationIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .machineOperationMapTableDao().getAllOperationByMachineId(sMachineId));

        if (!operationIdArrayList.isEmpty()) {
            MachineOperationListAdapter adapter= new MachineOperationListAdapter(this, sMachineId, operationIdArrayList);
            rvOperation.setAdapter(adapter);
        } else {
            rvOperation.setVisibility(View.GONE);
            tvOperationLabel.setVisibility(View.GONE);
        }

    }

    private void enableEditMode(boolean isEditable) {

        etMachineName.setEnabled(isEditable);
        tvMachineNameLabel.setClickable(isEditable);
        etAssignedName.setEnabled(isEditable);
        tvAssignedNameLabel.setClickable(isEditable);
        etShortName.setEnabled(isEditable);
        tvShortNameLabel.setClickable(isEditable);
        etMachineIndex.setEnabled(isEditable);
        tvMachineIndexLabel.setClickable(isEditable);

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

        String sMachineName = etMachineName.getText().toString();

        if (sMachineName == null || sMachineName.isEmpty()) {
            showToast("Enter Machine Name.");
            return;
        }

        String sAssignedName = etAssignedName.getText().toString();

        if (sAssignedName == null || sAssignedName.isEmpty()) {
            showToast("Enter Assigned Name.");
            return;
        }

        String sShortName = etShortName.getText().toString();

        if (sShortName == null || sShortName.isEmpty()) {
            showToast("Enter Short Name.");
            return;
        }

        String sMachineIndex = etMachineIndex.getText().toString();

        if (sMachineIndex == null || sMachineIndex.isEmpty()) {
            showToast("Enter Machine Index.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)
                && sPreviousMachineName != null && sPreviousAssignedName != null && sPreviousShortName != null && sPreviousMachineIndex != null
                && sMachineName.equals(sPreviousMachineName) && sAssignedName.equals(sPreviousAssignedName)
                && sShortName.equals(sPreviousShortName) && sMachineIndex.equals(sPreviousMachineIndex)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("machineName", sMachineName);
                jsonObject.put("assignedName", sAssignedName);
                jsonObject.put("shortName", sShortName);
                jsonObject.put("machineIndex", sMachineIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sMachineId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_MACHINE,
                        Constants.BASE_SERVER_URL + "update_machine",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("machineName", sMachineName);
            params.put("assignedName", sAssignedName);
            params.put("shortName", sShortName);
            params.put("machineIndex", sMachineIndex);

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_MACHINE,
                    Constants.BASE_SERVER_URL + "add_machine",
                    params);

        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_MACHINE)) {
                    addMachineResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_MACHINE)) {
                    updateMachineResponse(response);
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

    private void addMachineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject machineJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeMachineObject(machineJSONObject);

                showToast("Machine Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateMachineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject machineJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeMachineObject(machineJSONObject);

                showToast("Machine Updated Successfully.");

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
                tvTitleLabel.setText("Add Machine");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Machine");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Machine Detail");
                break;
            default:
                tvTitleLabel.setText("Machine Detail");
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
