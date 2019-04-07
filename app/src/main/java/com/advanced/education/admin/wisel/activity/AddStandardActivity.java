package com.advanced.education.admin.wisel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.table.TeacherTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class AddStandardActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvBasicDetailsLabel;
    private TextView tvStandardNameLabel;
    private EditText etStandardName;
    private TextView tvClassTeacherLabel;
    private TextView tvSelectClassTeacher;
    private TextView tvAdditionalDetailsLabel;
    private TextView tvOtherInformationLabel;
    private EditText etOtherInformation;

    private Toast mToast = null;

    private UtilityFile utilityFile;

    private String sCurrentMode;

    private String sStandardName = "Mathematics";
    private String sTeacherText = "1 Teacher Selected";
    private String sOtherInformation = "No other information";

    private ArrayList<String> selectedClassTeacherIdList;
    private List<TeacherTable> selectedClassTeacherTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_standard);

        initialDataSetup();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == Constants.SELECT_TEACHER_REQUEST) {

            selectedClassTeacherIdList = new ArrayList<>();
            selectedClassTeacherIdList.addAll(data.getStringArrayListExtra("SELECTED_TEACHER"));

            selectedClassTeacherTableList = new ArrayList<>();

            for (String sId : selectedClassTeacherIdList) {
                selectedClassTeacherTableList.add(new TeacherTable());
            }
            if (selectedClassTeacherTableList != null && !selectedClassTeacherTableList.isEmpty()) {
                if (selectedClassTeacherTableList.size() > 1) {
                    tvSelectClassTeacher.setText(selectedClassTeacherTableList.size() + " class teachers selected");
                } else {
                    tvSelectClassTeacher.setText(selectedClassTeacherTableList.size() + " class teacher selected");
                }
            } else {
                showToast("No class teacher selected.");
                tvSelectClassTeacher.setText("Select class teacher");
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if(i!=null){
            sCurrentMode = i.getStringExtra("MODE");
        }

        utilityFile = new UtilityFile(this);

        selectedClassTeacherIdList = new ArrayList<>();

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        ivSave = findViewById(R.id.ivSave);
        tvBasicDetailsLabel = findViewById(R.id.tvBasicDetailsLabel);
        tvStandardNameLabel = findViewById(R.id.tvStandardNameLabel);
        etStandardName = findViewById(R.id.etStandardName);
        tvClassTeacherLabel = findViewById(R.id.tvClassTeacherLabel);
        tvSelectClassTeacher = findViewById(R.id.tvSelectClassTeacher);
        tvAdditionalDetailsLabel = findViewById(R.id.tvAdditionalDetailsLabel);
        tvOtherInformationLabel = findViewById(R.id.tvOtherInformationLabel);
        etOtherInformation = findViewById(R.id.etOtherInformation);

        tvTitleLabel.setTypeface(utilityFile.getTfBold());
        tvBasicDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvAdditionalDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvStandardNameLabel.setTypeface(utilityFile.getTfRegular());
        etStandardName.setTypeface(utilityFile.getTfRegular());
        tvOtherInformationLabel.setTypeface(utilityFile.getTfRegular());
        etOtherInformation.setTypeface(utilityFile.getTfRegular());
        tvClassTeacherLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectClassTeacher.setTypeface(utilityFile.getTfRegular());

        tvSelectClassTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(AddStandardActivity.this, SelectTeacherActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", true);
                ii.putExtra("SELECTED_TEACHER", selectedClassTeacherIdList);
                startActivityForResult(ii, Constants.SELECT_TEACHER_REQUEST);

            }
        });

        if(sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)|| sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)){
            setInitialData();
        }

    }

    private void setInitialData(){

        etStandardName.setText(""+sStandardName);
        tvSelectClassTeacher.setText(""+sTeacherText);
        etOtherInformation.setText(""+sOtherInformation);

        if(sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)){
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        }
        else {
            enableEditMode(false);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
        }

        titleLabel();

    }

    private void enableEditMode(boolean isEditable){

        etStandardName.setEnabled(isEditable);
        tvSelectClassTeacher.setClickable(isEditable);
        etOtherInformation.setEnabled(isEditable);

    }


    private void titleLabel() {

        switch (sCurrentMode) {
            case Constants.ACTIVITY_MODE_ADD:
                tvTitleLabel.setText("Add Standard");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Standard");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Standard Detail");
                break;
            default:
                tvTitleLabel.setText("Standard Detail");
                break;

        }

    }

    public void saveButtonClicked(View view){

        if(sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)){
            //Add Data to server
        }
        else if(sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)){
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
        }else if(sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)){
            sCurrentMode = Constants.ACTIVITY_MODE_EDIT;
            setInitialData();
        }

    }

    public void backButtonClicked(View view){

        finish();

    }


    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

}

