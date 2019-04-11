package com.arvind.linebalancing.activity.line;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arvind.linebalancing.R;
import com.arvind.linebalancing.adapter.SelectLineAdapter;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class SelectLineActivity extends AppCompatActivity {

    private static final String TAG = SelectLineActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;

    private UtilityFile utilityFile;
    private SelectLineAdapter adapter;

    private boolean isMultipleSelectionAllowed = true;

    private ArrayList<String> selectedIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_line);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        Intent i = getIntent();
        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            selectedIdList = i.getStringArrayListExtra("SELECTED_LINE");
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

        RecyclerView recyclerView = findViewById(R.id.rvLine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LineTable> lineTableList = AppDatabase.getInstance(this).lineTableDao().getAllLines();

        if (lineTableList != null && !lineTableList.isEmpty()) {
            adapter = new SelectLineAdapter(this, lineTableList, selectedIdList, isMultipleSelectionAllowed);
            recyclerView.setAdapter(adapter);
        } else {

        }

    }

    public void goToPreviousActivity(){

        ArrayList<String> selectedLineIdList = new ArrayList<>();
        if(adapter!=null){
            selectedLineIdList = adapter.getSelectedLineIdList();

            Intent i = new Intent();
            i.putExtra("SELECTED_LINE", selectedLineIdList);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else{
            Intent i = new Intent();
            i.putExtra("SELECTED_LINE", selectedIdList);
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
