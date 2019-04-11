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
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.LineExecutiveMapTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LineExecutiveMapActivity extends AppCompatActivity {

    private static final String TAG = LineExecutiveMapActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;

    private TextView tvExecutiveLabel;
    private TextView tvSelectExecutive;
    private TextView tvLineLabel;
    private TextView tvSelectLine;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private int iEmployeeCount = 0;
    private long lEmployeeMapTimestamp = 0;
    private String sCurrentMode;
    private String sLineId;
    private String sLineEmployeeMapId;
    private String sPreviousEmployeeId;

    private ArrayList<String> selectedLineIdList;
    private ArrayList<LineTable> selectedLineTableList;
    private ArrayList<String> selectedEmployeeIdList;
    private ArrayList<String> selectedPreviousEmployeeIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_executive_map);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && resultCode == RESULT_OK) {

            if (requestCode == Constants.SELECT_LINE_REQUEST) {

                selectedLineIdList = new ArrayList<>();
                selectedLineIdList.addAll(data.getStringArrayListExtra("SELECTED_LINE"));

                selectedLineTableList = new ArrayList<>();

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

                if (selectedEmployeeIdList != null && !selectedEmployeeIdList.isEmpty()) {
                    tvSelectExecutive.setText(selectedEmployeeIdList.size() + " executives selected");
                } else {
                    showToast("No executive selected.");
                    tvSelectExecutive.setText("Select Executive");
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sLineId = i.getStringExtra("LINE_ID");
            sLineEmployeeMapId = i.getStringExtra("LINE_EMPLOYEE_MAP_ID");

            Log.d(TAG, "initialDataSetup: " + sLineId);
            if (!sCurrentMode.equalsIgnoreCase("add") && (sLineId == null || sLineId.isEmpty()) && (sLineEmployeeMapId == null || sLineEmployeeMapId.isEmpty())) {
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

        tvLineLabel = findViewById(R.id.tvLineLabel);
        tvSelectLine = findViewById(R.id.tvSelectLine);
        tvExecutiveLabel = findViewById(R.id.tvExecutiveLabel);
        tvSelectExecutive = findViewById(R.id.tvSelectExecutive);
        tvLineLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectLine.setTypeface(utilityFile.getTfMedium());
        tvExecutiveLabel.setTypeface(utilityFile.getTfMedium());
        tvSelectExecutive.setTypeface(utilityFile.getTfMedium());

        tvSelectLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(LineExecutiveMapActivity.this, SelectLineActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_LINE", selectedLineIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_LINE_REQUEST);
            }
        });

        tvSelectExecutive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(LineExecutiveMapActivity.this, SelectEmployeeActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", true);
                ii.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
                ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(ii, Constants.SELECT_EMPLOYEE_REQUEST);
            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        LineExecutiveMapTable lineEmployeeMapTable = AppDatabase.getInstance(this)
                .lineExecutiveMapTableDao().getLineExecutiveTableById(sLineEmployeeMapId);

        if (lineEmployeeMapTable != null) {

            selectedLineIdList = new ArrayList<>();
            selectedEmployeeIdList = new ArrayList<>();
            selectedLineIdList.add(lineEmployeeMapTable.getLineId());
            selectedEmployeeIdList.add(lineEmployeeMapTable.getEmployeeId());

            sPreviousEmployeeId = lineEmployeeMapTable.getEmployeeId();

            LineTable lineTable = AppDatabase.getInstance(this).lineTableDao()
                    .getLineById(lineEmployeeMapTable.getLineId());

            if (lineTable != null) {
                tvSelectLine.setText(lineTable.getLineName());
            }

            tvSelectExecutive.setText("1 executives selected");


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

        tvSelectLine.setClickable(false);
        tvLineLabel.setClickable(false);
        tvSelectExecutive.setClickable(isEditable);
        tvExecutiveLabel.setClickable(isEditable);
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

        if (selectedLineIdList == null || selectedLineIdList.isEmpty()) {
            showToast("Select Line.");
            return;
        }

        if (selectedEmployeeIdList == null || selectedEmployeeIdList.isEmpty()) {
            showToast("Select Employee.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sLineId != null
                && selectedEmployeeIdList.get(0).equals(sPreviousEmployeeId)) {

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
            jsonObject.put("lineId", sLineId);
            jsonObject.put("employeeId", selectedEmployeeIdList.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject.length() > 0) {

            showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", sLineEmployeeMapId);
            params.put("updated_data", jsonObject.toString());

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_LINE_EMPLOYEE_CONNECTION,
                    Constants.BASE_SERVER_URL + "update_line_executive_connection",
                    params);

        }

    }

    private void sendAddRequest() {

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("lineId", selectedLineIdList.get(0));
        params.put("employeeId", selectedEmployeeIdList.get(iEmployeeCount));

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_LINE_EMPLOYEE_CONNECTION,
                Constants.BASE_SERVER_URL + "add_line_executive_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_LINE_EMPLOYEE_CONNECTION)) {
                    addLineEmployeeMapResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_LINE_EMPLOYEE_CONNECTION)) {
                    updateLineEmployeeMapResponse(response);
                } else {
                    showToast(getResources().getString(R.string.unexpected_error));
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                Log.d(TAG, "notifyError: " + error.getMessage());

                hideProgressDialog();
                showToast(getResources().getString(R.string.volley_server_error));

            }
        };

        mGeneralVolleyRequest = new GeneralVolleyRequest(this, mRequestCallback);

    }

    private void addLineEmployeeMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineExecutiveMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineExecutiveMapObject(lineExecutiveMapJSONObject);

                if (iEmployeeCount + 1 == selectedEmployeeIdList.size()) {
                    showToast("Line & Executive connection added successfully.");
                    nextActivity();
                } else {
                    iEmployeeCount++;
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

    private void updateLineEmployeeMapResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineExecutiveMapJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineExecutiveMapObject(lineExecutiveMapJSONObject);

                showToast("Line & Executive connection updated successfully.");

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
                tvTitleLabel.setText("Line Exe. Connect");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Line Exe. Connect");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Line Exe. Connect");
                break;
            default:
                tvTitleLabel.setText("Line Exe. Connect");
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
