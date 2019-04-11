package com.arvind.linebalancing.activity.line;

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

import com.arvind.linebalancing.adapter.LineEmployeeListAdapter;
import com.arvind.linebalancing.adapter.LineExecutiveListAdapter;
import com.arvind.linebalancing.adapter.LineSupervisorListAdapter;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.LineEfficiencyTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddLineActivity extends AppCompatActivity {

    private static final String TAG = AddLineActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvLineNameLabel;
    private EditText etLineName;
    private TextView tvLineNoLabel;
    private EditText etLineNo;
    private TextView tvLineEfficiencyLabel;
    private TextView etLineEfficiency;
    private TextView tvSupervisorLabel;
    private RecyclerView rvSupervisor;
    private TextView tvExecutiveLabel;
    private TextView tvEmployeeLabel;
    private RecyclerView rvExecutive;
    private RecyclerView rvEmployee;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sLineId;
    private String sPreviousLineName;
    private String sPreviousLineNo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_line);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sLineId = i.getStringExtra("LINE_ID");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sLineId == null || sLineId.isEmpty())) {
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

        tvLineNameLabel = findViewById(R.id.tvLineNameLabel);
        etLineName = findViewById(R.id.etLineName);
        tvLineNoLabel = findViewById(R.id.tvLineNoLabel);
        etLineNo = findViewById(R.id.etLineNo);
        tvLineEfficiencyLabel = findViewById(R.id.tvLineEfficiencyLabel);
        etLineEfficiency = findViewById(R.id.etLineEfficiency);
        tvSupervisorLabel = findViewById(R.id.tvSupervisorLabel);
        tvExecutiveLabel = findViewById(R.id.tvExecutiveLabel);
        tvEmployeeLabel = findViewById(R.id.tvEmployeeLabel);

        rvSupervisor = findViewById(R.id.rvSupervisor);
        rvExecutive = findViewById(R.id.rvExecutive);
        rvEmployee = findViewById(R.id.rvEmployee);

        rvExecutive.setLayoutManager(new LinearLayoutManager(this));
        rvEmployee.setLayoutManager(new LinearLayoutManager(this));
        rvSupervisor.setLayoutManager(new LinearLayoutManager(this));

        tvExecutiveLabel.setVisibility(View.GONE);
        tvEmployeeLabel.setVisibility(View.GONE);
        tvSupervisorLabel.setVisibility(View.GONE);
        rvSupervisor.setVisibility(View.GONE);
        rvExecutive.setVisibility(View.GONE);
        rvEmployee.setVisibility(View.GONE);
        tvLineEfficiencyLabel.setVisibility(View.GONE);
        etLineEfficiency.setVisibility(View.GONE);

        tvLineNameLabel.setTypeface(utilityFile.getTfMedium());
        etLineName.setTypeface(utilityFile.getTfRegular());
        tvLineNoLabel.setTypeface(utilityFile.getTfMedium());
        tvLineEfficiencyLabel.setTypeface(utilityFile.getTfMedium());
        etLineNo.setTypeface(utilityFile.getTfRegular());
        etLineEfficiency.setTypeface(utilityFile.getTfRegular());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        }

    }

    private void setInitialData() {
        
        LineTable lineTable = AppDatabase.getInstance(this).lineTableDao()
                .getLineById(sLineId);

        if (lineTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sPreviousLineName = lineTable.getLineName();
        sPreviousLineNo = lineTable.getLineNo();
        etLineName.setText(lineTable.getLineName());
        etLineNo.setText(lineTable.getLineNo());
        
        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            setExecutives();
//            setEmployees();
            setSupervisors();
            setLineEfficiency();
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
//            ivSave.setVisibility(View.GONE);
        }

        titleLabel();

    }

    private void setLineEfficiency() {

        tvLineEfficiencyLabel.setVisibility(View.VISIBLE);
        etLineEfficiency.setVisibility(View.VISIBLE);

        final LineEfficiencyTable lineEfficiencyTable = AppDatabase.getInstance(this).lineEfficiencyTableDao().getLineEfficiencyTableById(sLineId);

        if (lineEfficiencyTable == null || lineEfficiencyTable.getLineEfficiency() < 0) {
            tvLineEfficiencyLabel.setVisibility(View.GONE);
            etLineEfficiency.setVisibility(View.GONE);
        } else {
            etLineEfficiency.setText(String.valueOf(lineEfficiencyTable.getLineEfficiency()));

            etLineEfficiency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddLineActivity.this, AddLineEfficiencyActivity.class);
                    intent.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    intent.putExtra("LINE_EFFICIENCY_ID", lineEfficiencyTable.getId());
                    intent.putExtra("LINE_ID", sLineId);
                    intent.putExtra("LINE_NAME", etLineName.getText().toString());
                    startActivity(intent);
                }
            });
        }

    }

    private void setExecutives() {

        rvExecutive.setVisibility(View.VISIBLE);
        tvExecutiveLabel.setVisibility(View.VISIBLE);

        ArrayList<String> executiveIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .lineExecutiveMapTableDao().getAllExecutiveByLineId(sLineId));

        if (!executiveIdArrayList.isEmpty()) {
            LineExecutiveListAdapter adapter= new LineExecutiveListAdapter(this, sLineId, executiveIdArrayList);
            rvExecutive.setAdapter(adapter);
        } else {
            rvExecutive.setVisibility(View.GONE);
            tvExecutiveLabel.setVisibility(View.GONE);
        }

    }

    private void setEmployees() {

        rvEmployee.setVisibility(View.VISIBLE);
        tvEmployeeLabel.setVisibility(View.VISIBLE);

        ArrayList<String> executiveIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .lineEmployeeMapTableDao().getAllEmployeeByLineId(sLineId));

        if (!executiveIdArrayList.isEmpty()) {
            LineEmployeeListAdapter adapter= new LineEmployeeListAdapter(this, sLineId, executiveIdArrayList);
            rvEmployee.setAdapter(adapter);
        } else {
            rvEmployee.setVisibility(View.GONE);
            tvEmployeeLabel.setVisibility(View.GONE);
        }

    }

    private void setSupervisors() {

        rvSupervisor.setVisibility(View.VISIBLE);
        tvSupervisorLabel.setVisibility(View.VISIBLE);

        ArrayList<String> supervisorIdArrayList = new ArrayList<>(AppDatabase.getInstance(this)
                .lineSupervisorMapTableDao().getAllSupervisorByLineId(sLineId));

        if (!supervisorIdArrayList.isEmpty()) {
            LineSupervisorListAdapter adapter = new LineSupervisorListAdapter(this, sLineId, supervisorIdArrayList);
            rvSupervisor.setAdapter(adapter);
        } else {
            rvSupervisor.setVisibility(View.GONE);
            tvSupervisorLabel.setVisibility(View.GONE);
        }

    }
    
    private void enableEditMode(boolean isEditable) {

        etLineName.setEnabled(isEditable);
        tvLineNameLabel.setClickable(isEditable);
        etLineNo.setEnabled(false);
        tvLineNoLabel.setClickable(false);

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

        String sLineName = etLineName.getText().toString();

        if (sLineName == null || sLineName.isEmpty()) {
            showToast("Enter Line Name.");
            return;
        }

        String sLineNo = etLineNo.getText().toString();

        if (sLineNo == null || sLineNo.isEmpty()) {
            showToast("Enter Line No.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sPreviousLineName != null && sPreviousLineNo != null
                && sLineName.matches(sPreviousLineName) && sLineNo.matches(sPreviousLineNo)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lineName", sLineName);
                jsonObject.put("lineNo", sLineNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sLineId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_LINE,
                        Constants.BASE_SERVER_URL + "update_line",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();

            HashMap<String, String> params = new HashMap<>();
            params.put("lineName", sLineName);
            params.put("lineNo", sLineNo);

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_LINE,
                    Constants.BASE_SERVER_URL + "add_line",
                    params);

        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_LINE)) {
                    addLineResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_LINE)) {
                    updateLineResponse(response);
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

    private void addLineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineObject(lineJSONObject);

                showToast("Line Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateLineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineObject(lineJSONObject);

                showToast("Line Updated Successfully.");

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
                tvTitleLabel.setText("Add Line");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Line");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Line Detail");
                break;
            default:
                tvTitleLabel.setText("Line Detail");
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
