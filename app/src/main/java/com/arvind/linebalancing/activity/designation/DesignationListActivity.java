package com.arvind.linebalancing.activity.designation;

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
import com.arvind.linebalancing.adapter.DesignationListAdapter;
import com.arvind.linebalancing.adapter.DesignationListAdapter;
import com.arvind.linebalancing.serverutilities.GetDesignationDataService;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.List;

public class DesignationListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvDesignation;
    
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
        setContentView(R.layout.activity_designation_list);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        if (utilityFile.isConnectingToInternet()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("DESIGNATION_LIST"));

            showProgressDialog();
            Intent intent = new Intent(this, GetDesignationDataService.class);
            startService(intent);
        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvDesignation = findViewById(R.id.rvDesignation);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvDesignation.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        List<DesignationTable> designationTableList = AppDatabase.getInstance(this).designationTableDao().getAllDesignations();

        if (designationTableList != null && !designationTableList.isEmpty()) {
            rvDesignation.setVisibility(View.VISIBLE);
            DesignationListAdapter adapter = new DesignationListAdapter(this, designationTableList);
            rvDesignation.setAdapter(adapter);
        } else {
            rvDesignation.setVisibility(View.GONE);
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
