package com.advanced.education.admin.wisel.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.SelectTeacherAdapter;
import com.advanced.education.admin.wisel.table.TeacherTable;

import java.util.ArrayList;
import java.util.List;

public class SelectTeacherActivity extends AppCompatActivity {

    private static final String TAG = SelectTeacherActivity.class.getSimpleName();

    private SelectTeacherAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;

    //TODO: Add filters standard and teacher wise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_teacher);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_TEACHER");
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

        RecyclerView recyclerView = findViewById(R.id.rvTeacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<TeacherTable> teacherTableList = new ArrayList<>();
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());
        teacherTableList.add(new TeacherTable());

        if (teacherTableList != null && !teacherTableList.isEmpty()) {
            adapter = new SelectTeacherAdapter(this, teacherTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedTeacherIdList = new ArrayList<>();
        if(adapter!=null){
            selectedTeacherIdList = adapter.getSelectedTeacherIdList();
            Intent i = new Intent();
            i.putExtra("SELECTED_TEACHER", selectedTeacherIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_TEACHER", selectedIdList);
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
