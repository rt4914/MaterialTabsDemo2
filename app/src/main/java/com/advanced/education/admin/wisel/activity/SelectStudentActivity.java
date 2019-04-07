package com.advanced.education.admin.wisel.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.SelectStudentAdapter;
import com.advanced.education.admin.wisel.table.StudentTable;

import java.util.ArrayList;
import java.util.List;

public class SelectStudentActivity extends AppCompatActivity {

    private static final String TAG = SelectStudentActivity.class.getSimpleName();

    private SelectStudentAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;

    //TODO: Add filters standard and student wise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_STUDENT");
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

        RecyclerView recyclerView = findViewById(R.id.rvStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<StudentTable> studentTableList = new ArrayList<>();
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());
        studentTableList.add(new StudentTable());

        if (studentTableList != null && !studentTableList.isEmpty()) {
            adapter = new SelectStudentAdapter(this, studentTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedStudentIdList = new ArrayList<>();
        if(adapter!=null){
            selectedStudentIdList = adapter.getSelectedStudentIdList();
            Intent i = new Intent();
            i.putExtra("SELECTED_STUDENT", selectedStudentIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_STUDENT", selectedIdList);
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
