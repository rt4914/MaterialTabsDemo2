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
import com.arvind.linebalancing.serverutilities.GetConnectionDataService;
import com.arvind.linebalancing.serverutilities.GetEmployeeDataService;
import com.arvind.linebalancing.serverutilities.GetLineDataService;
import com.arvind.linebalancing.serverutilities.GetMachineDataService;
import com.arvind.linebalancing.serverutilities.GetOperationDataService;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvEmployee;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private UtilityFile utilityFile;

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
        setContentView(R.layout.activity_employee_list);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        if (utilityFile.isConnectingToInternet()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("EMPLOYEE_LIST"));

            showProgressDialog();
            Intent intent = new Intent(this, GetEmployeeDataService.class);
            startService(intent);

            Intent intent1 = new Intent(this, GetConnectionDataService.class);
            startService(intent1);

            Intent intent2 = new Intent(this, GetLineDataService.class);
            startService(intent2);

            Intent intent3 = new Intent(this, GetMachineDataService.class);
            startService(intent3);

            Intent intent4 = new Intent(this, GetOperationDataService.class);
            startService(intent4);
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvEmployee = findViewById(R.id.rvEmployee);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvEmployee.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        List<EmployeeTable> employeeTableList = AppDatabase.getInstance(this).employeeTableDao().getAllEmployees();

        if (employeeTableList != null && !employeeTableList.isEmpty()) {
            rvEmployee.setVisibility(View.VISIBLE);
            EmployeeListAdapter adapter = new EmployeeListAdapter(this, employeeTableList);
            rvEmployee.setAdapter(adapter);
        } else {
            rvEmployee.setVisibility(View.GONE);
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
