package com.advanced.education.admin.wisel.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.SelectStandardAdapter;
import com.advanced.education.admin.wisel.table.StandardTable;

import java.util.ArrayList;
import java.util.List;

public class SelectStandardActivity extends AppCompatActivity {

    private static final String TAG = SelectStandardActivity.class.getSimpleName();

    private SelectStandardAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;

    //TODO: Add filters standard and standard wise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_standard);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_STANDARD");
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

        showRecyclerView();

    }

    private void showRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.rvStandard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<StandardTable> standardTableList = new ArrayList<>();
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());
        standardTableList.add(new StandardTable());

        if (standardTableList != null && !standardTableList.isEmpty()) {
            adapter = new SelectStandardAdapter(this, standardTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedStandardIdList = new ArrayList<>();
        if(adapter!=null){
            selectedStandardIdList = adapter.getSelectedStandardIdList();
            Intent i = new Intent();
            i.putExtra("SELECTED_STANDARD", selectedStandardIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_STANDARD", selectedIdList);
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
