package com.advanced.education.admin.wisel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.StandardListAdapter;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

public class StandardListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvStandard;

    private UtilityFile utilityFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_list);

        initialDataSetup();

    }

    private void initialDataSetup() {

        utilityFile = new UtilityFile(this);

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvStandard = findViewById(R.id.rvStandard);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvStandard.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        StandardListAdapter adapter = new StandardListAdapter(this,null);
        rvStandard.setAdapter(adapter);

    }

    public void backButtonClicked(View view) {

        finish();

    }

}
