package com.arvind.linebalancing.activity.connection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.MainActivity;
import com.arvind.linebalancing.activity.machine.SelectMachineActivity;
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.MachineOperationMapTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MachineOperationMapActivity extends AppCompatActivity {

    private static final String TAG = MachineOperationMapActivity.class.getSimpleName();
    
    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;

    private TextView tvOperationLabel;
    private TextView tvSelectOperation;
    private TextView tvMachineLabel;
    private TextView tvSelectMachine;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private int iOperationCount = 0;
    private long lOperationMapTimestamp = 0;
    private String sCurrentMode;
    private String sMachineId;
    private String sPreviousOperationId;
    private String sMachineOperationMapId;
    private long lPreviousRateTimestamp;

    private ArrayList<String> selectedMachineIdList;
    private ArrayList<MachineTable> selectedMachineTableList;
    private ArrayList<String> selectedOperationIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_operation_map);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && resultCode == RESULT_OK) {

            if (requestCode == Constants.SELECT_MACHINE_REQUEST) {

                selectedMachineIdList = new ArrayList<>();
                selectedMachineIdList.addAll(data.getStringArrayListExtra("SELECTED_MACHINE"));

                selectedMachineTableList = new ArrayList<>();

                for (String sId : selectedMachineIdList) {
                    selectedMachineTableList.add(AppDatabase.getInstance(this).machineTableDao().getMachineById(sId));
                }
                if (selectedMachineTableList != null && !selectedMachineTableList.isEmpty()) {
                    tvSelectMachine.setText(selectedMachineTableList.get(0).getMachineName());
                } else {
                    showToast("No machine selected.");
                    tvSelectMachine.setText("Select Machine");
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
            sMachineId = i.getStringExtra("MACHINE_ID");
            sMachineOperationMapId = i.getStringExtra("MACHINE_OPERATION_MAP_ID");

            Log.d(TAG, "initialDataSetup: " + sMachineId);
            if (!sCurrentMode.equalsIgnoreCase("add") && (sMachineId == null || sMachineId.isEmpty())&& (sMachineOperationMapId == null || sMachineOperationMapId.isEmpty())) {
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

        tvMachineLabel = findViewById(R.id.tvMachineLabel);
        tvSelectMachine = findViewById(R.id.tvSelectMachine);
        tvOperationLabel = findViewById(R.id.tvOperationLabel);
        tvSelectOperation = findViewById(R.id.tvSelectOperation);
        tvMachineLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectMachine.setTypeface(utilityFile.getTfMedium());
        tvOperationLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectOperation.setTypeface(utilityFile.getTfMedium());

        tvSelectMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(MachineOperationMapActivity.this, SelectMachineActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_MACHINE", selectedMachineIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_MACHINE_REQUEST);
            }
        });

        tvSelectOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(MachineOperationMapActivity.this, SelectOperationActivity.class);
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

        MachineOperationMapTable machineOperationMapTable = AppDatabase.getInstance(this)
                .machineOperationMapTableDao().getMachineOperationTableById(sMachineOperationMapId);

        if (machineOperationMapTable != null) {

            selectedMachineIdList = new ArrayList<>();
            selectedOperationIdList = new ArrayList<>();
            selectedMachineIdList.add(machineOperationMapTable.getMachineId());
            selectedOperationIdList.add(machineOperationMapTable.getOperationId());

            sPreviousOperationId = machineOperationMapTable.getOperationId();

            MachineTable machineTable = AppDatabase.getInstance(this).machineTableDao()
                    .getMachineById(machineOperationMapTable.getMachineId());

            if (machineTable != null) {
                tvSelectMachine.setText(machineTable.getMachineName());
            }

//            OperationTable operationTable = AppDatabase.getInstance(this).operationTableDao()
//                    .getOperationById(machineOperationMapTable.getOperationId());

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

        tvSelectMachine.setClickable(false);
        tvMachineLabel.setClickable(false);
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

        if (selectedMachineIdList == null || selectedMachineIdList.isEmpty()) {
            showToast("Select Machine.");
            return;
        }

        if (selectedOperationIdList == null || selectedOperationIdList.isEmpty()) {
            showToast("Select Operation.");
            return;
        }

        Log.d(TAG, "verifyDetails: " + sMachineId);
        Log.d(TAG, "verifyDetails: " + selectedOperationIdList.get(0));
        Log.d(TAG, "verifyDetails: " + sPreviousOperationId);

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sMachineId != null
                && selectedOperationIdList.get(0).equals(sPreviousOperationId)) {

            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

//            JSONArray jsonArray = new JSONArray();
//            for (String s : selectedOperationIdList) {
//                jsonArray.put(s);
//            }
            sendUpdateRequest();

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

//            JSONArray jsonArray = new JSONArray();
//            for (String s : selectedOperationIdList) {
//                jsonArray.put(s);
//            }
            sendAddRequest();

        }

    }

    private void sendUpdateRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("machineId", sMachineId);
            jsonObject.put("operationId", selectedOperationIdList.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", sMachineOperationMapId);
            params.put("updated_data", jsonObject.toString());

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_MACHINE_OPERATION_CONNECTION,
                    Constants.BASE_SERVER_URL + "update_machine_operation_connection",
                    params);

        }

    }

    private void sendAddRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("machineId", selectedMachineIdList.get(0));
        params.put("operationId", selectedOperationIdList.get(iOperationCount));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_MACHINE_OPERATION_CONNECTION,
                Constants.BASE_SERVER_URL + "add_machine_operation_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_MACHINE_OPERATION_CONNECTION)) {
                    addMachineOperationMapResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_MACHINE_OPERATION_CONNECTION)) {
                    updateMachineOperationMapResponse(response);
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

    private void addMachineOperationMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject machineOperationMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeMachineOperationMapObject(machineOperationMapJSONObject);

                if (iOperationCount + 1 == selectedOperationIdList.size()) {
                    showToast("Machine & Operations connection added successfully.");
                    nextActivity();
                } else {
                    iOperationCount++;
                    sendAddRequest();
                }

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateMachineOperationMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject machineOperationMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeMachineOperationMapObject(machineOperationMapJSONObject);

                showToast("Machine & Operations connection updated successfully.");

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
                tvTitleLabel.setText("Machine Op. Connect");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Machine Op. Connect");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Machine Op. Connect");
                break;
            default:
                tvTitleLabel.setText("Machine Op. Connect");
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
