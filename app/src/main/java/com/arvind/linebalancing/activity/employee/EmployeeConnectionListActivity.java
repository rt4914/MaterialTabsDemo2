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
import com.arvind.linebalancing.adapter.ConnectionListAdapter;
import com.arvind.linebalancing.serverutilities.GetConnectionDataService;
import com.arvind.linebalancing.table.ConnectionTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.List;

public class EmployeeConnectionListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvConnection;

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
    private String sEmployeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        Intent intent = getIntent();

        if (intent == null) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        sEmployeeId = intent.getStringExtra("EMPLOYEE_ID");

        if (sEmployeeId == null || sEmployeeId.isEmpty()) {
            showToast(getResources().getString(R.string.unexpected_error));
            finish();
            return;
        }

        if (utilityFile.isConnectingToInternet()) {
            
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvConnection = findViewById(R.id.rvConnection);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvConnection.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        List<ConnectionTable> connectionTableList = AppDatabase.getInstance(this).connectionTableDao().getAllConnectionsByEmployeeId(sEmployeeId);

        if (connectionTableList != null && !connectionTableList.isEmpty()) {
            rvConnection.setVisibility(View.VISIBLE);
            ConnectionListAdapter adapter = new ConnectionListAdapter(this, connectionTableList);
            rvConnection.setAdapter(adapter);
        } else {
            rvConnection.setVisibility(View.GONE);
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
