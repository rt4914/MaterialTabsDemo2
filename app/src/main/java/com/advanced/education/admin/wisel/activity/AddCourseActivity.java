package com.advanced.education.admin.wisel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.table.CourseTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.MyAlertDialog;
import com.advanced.education.admin.wisel.utilities.MyAlertDialogInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class AddCourseActivity extends AppCompatActivity {

    private final static String TAG = AddCourseActivity.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvBasicDetailsLabel;
    private TextView tvAdditionalDetailsLabel;
    private TextView tvCourseNameLabel;
    private TextView tvCourseCodeLabel;
    private TextView tvStandardLabel;
    private TextView tvActionLabel;
    private TextView tvThresholdAttendanceLabel;
    private TextView tvOtherInformationLabel;
    private TextView tvSelectStandard;
    private TextView tvSelectAction;
    private EditText etCourseName;
    private EditText etCourseCode;
    private EditText etThresholdAttendance;
    private EditText etOtherInformation;

    private Toast mToast = null;

    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private String sCurrentMode;

    private String sCourseName = "Mathematics";
    private String sCourseCode = "MATH101";
    private String sStandardText = "1 Standard Selected";
    private String sAction = "Present (Default)";
    private double dThresholdAttendance = 75;
    private String sOtherInformation = "";

    private List<String> actionList;
    private ArrayList<String> selectedStandardIdList;
    private List<CourseTable> selectedStandardTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == Constants.SELECT_STANDARD_REQUEST) {

            selectedStandardIdList = new ArrayList<>();
            selectedStandardIdList.addAll(data.getStringArrayListExtra("SELECTED_STANDARD"));

            selectedStandardTableList = new ArrayList<>();

            for (String sId : selectedStandardIdList) {
                selectedStandardTableList.add(new CourseTable());
            }
            if (selectedStandardTableList != null && !selectedStandardTableList.isEmpty()) {
                if (selectedStandardTableList.size() > 1) {
                    tvSelectStandard.setText(selectedStandardTableList.size() + " standards selected");
                } else {
                    tvSelectStandard.setText(selectedStandardTableList.size() + " standard selected");
                }
            } else {
                showToast("No standard selected.");
                tvSelectStandard.setText("Select standard");
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initialDataSetup() {

        Intent i = getIntent();
        if (i != null) {
            sCurrentMode = i.getStringExtra("MODE");
        }

        utilityFile = new UtilityFile(this);

        actionList = new ArrayList<>();
        actionList.add("Present (Default)");
        actionList.add("Absent");
        actionList.add("Leave");
        selectedStandardIdList = new ArrayList<>();

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        ivSave = findViewById(R.id.ivSave);
        tvBasicDetailsLabel = findViewById(R.id.tvBasicDetailsLabel);
        tvAdditionalDetailsLabel = findViewById(R.id.tvAdditionalDetailsLabel);
        tvCourseNameLabel = findViewById(R.id.tvCourseNameLabel);
        tvCourseCodeLabel = findViewById(R.id.tvCourseCodeLabel);
        tvStandardLabel = findViewById(R.id.tvStandardLabel);
        tvActionLabel = findViewById(R.id.tvActionLabel);
        tvThresholdAttendanceLabel = findViewById(R.id.tvThresholdAttendanceLabel);
        tvOtherInformationLabel = findViewById(R.id.tvOtherInformationLabel);
        etCourseName = findViewById(R.id.etCourseName);
        etCourseCode = findViewById(R.id.etCourseCode);
        tvSelectStandard = findViewById(R.id.tvSelectStandard);
        tvSelectAction = findViewById(R.id.tvSelectAction);
        etThresholdAttendance = findViewById(R.id.etThresholdAttendance);
        etOtherInformation = findViewById(R.id.etOtherInformation);

        tvTitleLabel.setTypeface(utilityFile.getTfBold());
        tvBasicDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvAdditionalDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvCourseNameLabel.setTypeface(utilityFile.getTfRegular());
        tvCourseCodeLabel.setTypeface(utilityFile.getTfRegular());
        tvStandardLabel.setTypeface(utilityFile.getTfRegular());
        tvActionLabel.setTypeface(utilityFile.getTfRegular());
        tvThresholdAttendanceLabel.setTypeface(utilityFile.getTfRegular());
        tvOtherInformationLabel.setTypeface(utilityFile.getTfRegular());
        etCourseName.setTypeface(utilityFile.getTfRegular());
        etCourseCode.setTypeface(utilityFile.getTfRegular());
        tvSelectStandard.setTypeface(utilityFile.getTfRegular());
        tvSelectAction.setTypeface(utilityFile.getTfRegular());
        etThresholdAttendance.setTypeface(utilityFile.getTfRegular());
        etOtherInformation.setTypeface(utilityFile.getTfRegular());

        alertDialogSetup();

        tvSelectStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(AddCourseActivity.this, SelectStandardActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                ii.putExtra("SELECTED_STANDARD", selectedStandardIdList);
                startActivityForResult(ii, Constants.SELECT_STANDARD_REQUEST);

            }
        });

        tvSelectAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myAlertDialog.createAlertBox("Select action", true, actionList, "ACTION");

            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW) || sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        etCourseName.setText("" + sCourseName);
        etCourseCode.setText("" + sCourseCode);
        tvSelectStandard.setText("" + sStandardText);
        tvSelectAction.setText("" + sAction);
        etThresholdAttendance.setText("" + dThresholdAttendance + "%");
        etOtherInformation.setText("" + sOtherInformation);

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            enableEditMode(true);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_accent_24dp));
        } else {
            enableEditMode(false);
            ivSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_accent_24dp));
        }

        titleLabel();

    }

    private void enableEditMode(boolean isEditable) {

        etCourseName.setEnabled(isEditable);
        etCourseCode.setEnabled(isEditable);
        tvSelectStandard.setClickable(isEditable);
        tvSelectAction.setClickable(isEditable);
        etThresholdAttendance.setEnabled(isEditable);
        etOtherInformation.setEnabled(isEditable);

    }

    public void alertDialogSetup() {

        MyAlertDialogInterface myAlertDialogInterface = new MyAlertDialogInterface() {
            @Override
            public void getSelectedItemIndex(int index, String sAlertDialogType) {

                if (sAlertDialogType.matches("ACTION")) {
                    String sGender = actionList.get(index);
                    tvSelectAction.setText("" + sGender);
                }

            }
        };
        myAlertDialog = new MyAlertDialog(this, myAlertDialogInterface);

    }

    public void saveButtonClicked(View view) {

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_ADD)) {
            //Add Data to server
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            sCurrentMode = Constants.ACTIVITY_MODE_VIEW;
            setInitialData();
        } else if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW)) {
            sCurrentMode = Constants.ACTIVITY_MODE_EDIT;
            setInitialData();
        }

    }

    private void titleLabel() {

        switch (sCurrentMode) {
            case Constants.ACTIVITY_MODE_ADD:
                tvTitleLabel.setText("Add Course");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Course");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Course Detail");
                break;
            default:
                tvTitleLabel.setText("Course Detail");
                break;

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

}

