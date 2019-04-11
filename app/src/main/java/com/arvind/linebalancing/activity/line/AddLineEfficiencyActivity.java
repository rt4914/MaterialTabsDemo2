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

public class AddLineEfficiencyActivity extends AppCompatActivity {

    private static final String TAG = AddLineEfficiencyActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvLineNameLabel;
    private EditText etLineName;
    private TextView tvLineEfficiencyLabel;
    private EditText etLineEfficiency;;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private UtilityFile utilityFile;

    private String sCurrentMode;
    private String sLineId;
    private String sPreviousLineEfficiency;
    private String sLineEfficiencyId;
    private String sLineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_line_efficiency);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
            sLineEfficiencyId = i.getStringExtra("LINE_EFFICIENCY_ID");
            Log.d(TAG, "initialDataSetup: " + sLineEfficiencyId);
            sLineId = i.getStringExtra("LINE_ID");
            sLineName = i.getStringExtra("LINE_NAME");

            if (!sCurrentMode.equalsIgnoreCase("add") && (sLineEfficiencyId == null || sLineEfficiencyId.isEmpty())) {
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
        tvLineEfficiencyLabel = findViewById(R.id.tvLineEfficiencyLabel);
        etLineEfficiency = findViewById(R.id.etLineEfficiency);

        tvLineNameLabel.setTypeface(utilityFile.getTfMedium());
        etLineName.setTypeface(utilityFile.getTfRegular());
        tvLineEfficiencyLabel.setTypeface(utilityFile.getTfMedium());
        etLineEfficiency.setTypeface(utilityFile.getTfRegular());

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            setInitialData();
        } else {
            etLineName.setText(sLineName);
        }

    }

    private void setInitialData() {

        LineEfficiencyTable lineEfficiencyTable = AppDatabase.getInstance(this).lineEfficiencyTableDao()
                .getLineEfficiencyTableByLineEfficiencyId(sLineEfficiencyId);

        if (lineEfficiencyTable == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sPreviousLineEfficiency = String.valueOf(lineEfficiencyTable.getLineEfficiency());
        etLineName.setText(sLineName);

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            setLineEfficiency();
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
//            ivSave.setVisibility(View.GONE);
        }

        titleLabel();

    }

    private void setLineEfficiency() {

        tvLineEfficiencyLabel.setVisibility(View.VISIBLE);
        etLineEfficiency.setVisibility(View.VISIBLE);

        double dLineEfficiency = AppDatabase.getInstance(this).lineEfficiencyTableDao().getLineEfficiencyById(sLineId);

        if (dLineEfficiency < 0) {
            tvLineEfficiencyLabel.setVisibility(View.GONE);
            etLineEfficiency.setVisibility(View.GONE);
        } else {
            etLineEfficiency.setText(String.valueOf(dLineEfficiency));
        }

    }

    private void enableEditMode(boolean isEditable) {

        etLineName.setEnabled(false);
        tvLineNameLabel.setClickable(false);
        etLineEfficiency.setEnabled(isEditable);
        tvLineEfficiencyLabel.setClickable(isEditable);

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

        String sLineEfficiency = etLineEfficiency.getText().toString();

        if (sLineEfficiency == null || sLineEfficiency.isEmpty()) {
            showToast("Enter Line Efficiency.");
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT) && sLineId != null && sPreviousLineEfficiency != null
                && sLineEfficiency.matches(sPreviousLineEfficiency)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
            return;
        }

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lineId", sLineId);
                jsonObject.put("lineEfficiency", sLineEfficiency);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject.length() > 0) {

                showProgressDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", sLineEfficiencyId);
                params.put("updated_data", jsonObject.toString());

                mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_UPDATE_LINE_EFFICIENCY,
                        Constants.BASE_SERVER_URL + "update_line_efficiency",
                        params);

            }

        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {

            showProgressDialog();

            HashMap<String, String> params = new HashMap<>();
            params.put("lineId", sLineId);
            params.put("lineEfficiency", sLineEfficiency);

            mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_ADD_LINE_EFFICIENCY,
                    Constants.BASE_SERVER_URL + "add_line_efficiency",
                    params);

        }

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_ADD_LINE_EFFICIENCY)) {
                    addLineEfficiencyResponse(response);
                } else if (requestType.matches(Constants.API_UPDATE_LINE_EFFICIENCY)) {
                    updateLineEfficiencyResponse(response);
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

    private void addLineEfficiencyResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineEfficiencyJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineEfficiencyObject(lineEfficiencyJSONObject);

                showToast("Line Efficiency Added Successfully.");

                nextActivity();

            } else {
                showToast(getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(getResources().getString(R.string.invalid_json));
        }

    }

    private void updateLineEfficiencyResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                JSONObject lineEfficiencyJSONObject = responseObject.optJSONObject("message");

                DecodeResponse decodeResponse = new DecodeResponse(this);
                decodeResponse.decodeLineEfficiencyObject(lineEfficiencyJSONObject);

                showToast("Line Efficiency Updated Successfully.");

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
                tvTitleLabel.setText("Add Line Efficiency");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Line Efficiency");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Line Efficiency Detail");
                break;
            default:
                tvTitleLabel.setText("Line Efficiency Detail");
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
