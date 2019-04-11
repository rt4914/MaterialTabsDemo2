package com.arvind.linebalancing.activity.employee;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arvind.linebalancing.R;
import com.arvind.linebalancing.adapter.EmployeeListAdapter;
import com.arvind.linebalancing.adapter.EmployeeRatingListAdapter;
import com.arvind.linebalancing.serverutilities.GetEmployeeDataService;
import com.arvind.linebalancing.serverutilities.GetEmployeeRatingDataService;
import com.arvind.linebalancing.table.EmployeeRatingTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.List;

public class EmployeeRatingListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvEmployeeRating;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private UtilityFile utilityFile;

    private String sEmployeeId;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            showRecyclerView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_rating_list);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        Intent i = getIntent();

        if (i == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sEmployeeId = i.getStringExtra("EMPLOYEE_ID");

        if (sEmployeeId == null || sEmployeeId.isEmpty()) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        if (utilityFile.isConnectingToInternet()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("EMPLOYEE_RATING_LIST"));

            showProgressDialog();
            Intent intent = new Intent(this, GetEmployeeRatingDataService.class);
            startService(intent);
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvEmployeeRating = findViewById(R.id.rvEmployeeRating);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvEmployeeRating.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        List<EmployeeRatingTable> employeeRatingTableList = AppDatabase.getInstance(this)
                .employeeRatingTableDao().getAllEmployeeRatingsByEmployeeId(sEmployeeId);

        if (employeeRatingTableList != null && !employeeRatingTableList.isEmpty()) {
            rvEmployeeRating.setVisibility(View.VISIBLE);
            EmployeeRatingListAdapter adapter = new EmployeeRatingListAdapter(this, employeeRatingTableList);
            rvEmployeeRating.setAdapter(adapter);
        } else {
            rvEmployeeRating.setVisibility(View.GONE);
        }

    }

    public void backButtonClicked(View view) {

        finish();

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

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

}
