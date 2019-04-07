package com.advanced.education.admin.wisel.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.SelectCourseAdapter;
import com.advanced.education.admin.wisel.table.CourseTable;
import com.advanced.education.admin.wisel.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectCourseActivity extends AppCompatActivity {

    private static final String TAG = SelectCourseActivity.class.getSimpleName();

    private SelectCourseAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;

    //TODO: Add filters standard and course wise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);

        initialDataSetup();

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_COURSE");
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

        RecyclerView recyclerView = findViewById(R.id.rvCourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CourseTable> courseTableList = new ArrayList<>();
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());
        courseTableList.add(new CourseTable());

        if (courseTableList != null && !courseTableList.isEmpty()) {
            adapter = new SelectCourseAdapter(this, courseTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedCourseIdList = new ArrayList<>();
        if(adapter!=null){
            selectedCourseIdList = adapter.getSelectedCourseIdList();
            Intent i = new Intent();
            i.putExtra("SELECTED_COURSE", selectedCourseIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_COURSE", selectedIdList);
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
