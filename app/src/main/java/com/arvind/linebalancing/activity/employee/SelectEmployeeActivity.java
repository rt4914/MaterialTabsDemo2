package com.arvind.linebalancing.activity.employee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arvind.linebalancing.R;
import com.arvind.linebalancing.adapter.SelectDesignationAdapter;
import com.arvind.linebalancing.adapter.SelectEmployeeAdapter;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class SelectEmployeeActivity extends AppCompatActivity {

    private static final String TAG = SelectEmployeeActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;

    private UtilityFile utilityFile;
    private SelectEmployeeAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_employee);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_EMPLOYEE");
            if(!isMultipleSelectionAllowed){
                selectedIdList = new ArrayList<>();
            }
        }

        if(selectedIdList==null){
            selectedIdList = new ArrayList<>();
        }

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        ivSave = findViewById(R.id.ivSave);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        showRecyclerView();

    }

    private void showRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.rvEmployee);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<EmployeeTable> employeeTableList = AppDatabase.getInstance(this).employeeTableDao().getAllEmployees();

        if (employeeTableList != null && !employeeTableList.isEmpty()) {
            adapter = new SelectEmployeeAdapter(this, employeeTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedEmployeeIdList = new ArrayList<>();
        if(adapter!=null){
            selectedEmployeeIdList = adapter.getSelectedEmployeeIdList();

            Intent i = new Intent();
            i.putExtra("SELECTED_EMPLOYEE", selectedEmployeeIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_EMPLOYEE", selectedIdList);
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        }

    }

    public void saveButtonClicked(View view) {

        goToPreviousActivity();

    }

    public void backButtonClicked(View view){
        finish();
    }

}
