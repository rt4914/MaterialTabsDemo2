package com.advanced.education.admin.wisel.activity;

import android.content.Intent;
import android.net.Uri;
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
import com.advanced.education.admin.wisel.utilities.MyDatePicker;
import com.advanced.education.admin.wisel.utilities.MyDatePickerInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacherActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvBasicDetailsLabel;
    private CircleImageView civTeacherImage;
    private TextView tvTeacherNameLabel;
    private EditText etTeacherName;
    private TextView tvPhoneNumberLabel;
    private EditText etPhoneNumber;
    private TextView tvEmailLabel;
    private EditText etEmail;
    private TextView tvGenderLabel;
    private TextView tvSelectGender;
    private TextView tvDOBLabel;
    private TextView tvSelectDOB;
    private TextView tvAddressLabel;
    private EditText etAddress;
    private TextView tvInstituteDetailsLabel;
    private TextView tvTeacherCodeLabel;
    private EditText etTeacherCode;
    private TextView tvTeacherPositionLabel;
    private EditText etTeacherPosition;
    private TextView tvCoursesLabel;
    private TextView tvSelectCourses;
    private TextView tvAccessRightsLabel;
    private TextView tvSelectAccessRights;
    private TextView tvAdditionalDetailsLabel;
    private TextView tvOtherInformationLabel;
    private EditText etOtherInformation;

    private Toast mToast = null;

    private MyAlertDialog myAlertDialog = null;
    private MyDatePicker myDatePicker = null;
    private UtilityFile utilityFile;

    private long lDOB = 0;

    private String sCurrentMode;

    private String sTeacherName = "Mr. Rajat Talesra";
    private String sPhoneNumber = "9723124260";
    private String sEmail = "rajatt@wisel.in";
    private String sGender = "Male";
    private String sDOB = "12 Oct 1993";
    private String sAddress = "204, Shree Rang Mall, Gandhinagar, Gujarat";
    private String sTeacherCode = "TEA-RAJ001";
    private String sTeacherPosition = "HOD CE";
    private String sCoursesText = "5 courses selected";
    private String sAccessRightsText = "15 rights given";
    private String sOtherInformation = "";

    private ArrayList<String> selectedCourseIdList;
    private List<CourseTable> selectedCourseTableList;
    private List<String> documentList;
    private List<String> genderList;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == Constants.SELECT_DOCUMENT_ACTIVITY_REQUEST) {

            documentList.addAll(data.getStringArrayListExtra("SELECTED_DOCUMENTS"));

            if (documentList != null && !documentList.isEmpty()) {
                if (documentList.size() > 10) {
                    documentList = new ArrayList<>();
                    showToast("Maximum 10 images allowed.");
                } else {
                    civTeacherImage.setImageURI(Uri.parse(documentList.get(0)));
                }
            } else {
                showToast("No file selected.");
            }
        } else if (resultCode == RESULT_OK && requestCode == Constants.SELECT_COURSE_REQUEST) {

            selectedCourseIdList = new ArrayList<>();
            selectedCourseIdList.addAll(data.getStringArrayListExtra("SELECTED_COURSE"));

            selectedCourseTableList = new ArrayList<>();

            for (String sId : selectedCourseIdList) {
                selectedCourseTableList.add(new CourseTable());
            }
            if (selectedCourseTableList != null && !selectedCourseTableList.isEmpty()) {
                if (selectedCourseTableList.size() > 1) {
                    tvSelectCourses.setText(selectedCourseTableList.size() + " courses selected");
                } else {
                    tvSelectCourses.setText(selectedCourseTableList.size() + " course selected");
                }
            } else {
                showToast("No course selected.");
                tvSelectCourses.setText("Select course");
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

        selectedCourseIdList = new ArrayList<>();
        documentList = new ArrayList<>();

        genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Rather not say");

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        ivSave = findViewById(R.id.ivSave);
        tvBasicDetailsLabel = findViewById(R.id.tvBasicDetailsLabel);
        civTeacherImage = findViewById(R.id.civTeacherImage);
        tvTeacherNameLabel = findViewById(R.id.tvTeacherNameLabel);
        etTeacherName = findViewById(R.id.etTeacherName);
        tvPhoneNumberLabel = findViewById(R.id.tvPhoneNumberLabel);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvEmailLabel = findViewById(R.id.tvEmailLabel);
        etEmail = findViewById(R.id.etEmail);
        tvGenderLabel = findViewById(R.id.tvGenderLabel);
        tvSelectGender = findViewById(R.id.tvSelectGender);
        tvDOBLabel = findViewById(R.id.tvDOBLabel);
        tvSelectDOB = findViewById(R.id.tvSelectDOB);
        tvAddressLabel = findViewById(R.id.tvAddressLabel);
        etAddress = findViewById(R.id.etAddress);
        tvInstituteDetailsLabel = findViewById(R.id.tvInstituteDetailsLabel);
        tvTeacherCodeLabel = findViewById(R.id.tvTeacherCodeLabel);
        etTeacherCode = findViewById(R.id.etTeacherCode);
        tvTeacherPositionLabel = findViewById(R.id.tvTeacherPositionLabel);
        etTeacherPosition = findViewById(R.id.etTeacherPosition);
        tvCoursesLabel = findViewById(R.id.tvCoursesLabel);
        tvSelectCourses = findViewById(R.id.tvSelectCourses);
        tvAccessRightsLabel = findViewById(R.id.tvAccessRightsLabel);
        tvSelectAccessRights = findViewById(R.id.tvSelectAccessRights);
        tvAdditionalDetailsLabel = findViewById(R.id.tvAdditionalDetailsLabel);
        tvOtherInformationLabel = findViewById(R.id.tvOtherInformationLabel);
        etOtherInformation = findViewById(R.id.etOtherInformation);

        tvTitleLabel.setTypeface(utilityFile.getTfBold());
        tvBasicDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvTeacherNameLabel.setTypeface(utilityFile.getTfRegular());
        etTeacherName.setTypeface(utilityFile.getTfRegular());
        tvPhoneNumberLabel.setTypeface(utilityFile.getTfRegular());
        etPhoneNumber.setTypeface(utilityFile.getTfRegular());
        tvEmailLabel.setTypeface(utilityFile.getTfRegular());
        etEmail.setTypeface(utilityFile.getTfRegular());
        tvGenderLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectGender.setTypeface(utilityFile.getTfRegular());
        tvDOBLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectDOB.setTypeface(utilityFile.getTfRegular());
        tvAddressLabel.setTypeface(utilityFile.getTfRegular());
        etAddress.setTypeface(utilityFile.getTfRegular());
        tvInstituteDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvTeacherCodeLabel.setTypeface(utilityFile.getTfRegular());
        etTeacherCode.setTypeface(utilityFile.getTfRegular());
        tvTeacherPositionLabel.setTypeface(utilityFile.getTfRegular());
        etTeacherPosition.setTypeface(utilityFile.getTfRegular());
        tvCoursesLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectCourses.setTypeface(utilityFile.getTfRegular());
        tvAccessRightsLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectAccessRights.setTypeface(utilityFile.getTfRegular());
        tvAdditionalDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvOtherInformationLabel.setTypeface(utilityFile.getTfRegular());
        etOtherInformation.setTypeface(utilityFile.getTfRegular());

        alertDialogSetup();

        datePickerSetup();

        tvSelectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myAlertDialog.createAlertBox("Select gender", true, genderList, "GENDER");

            }
        });

        tvSelectDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDatePicker.showDatePicker("DOB");

            }
        });

        tvSelectCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(AddTeacherActivity.this, SelectCourseActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", true);
                ii.putExtra("SELECTED_COURSE", selectedCourseIdList);
                startActivityForResult(ii, Constants.SELECT_COURSE_REQUEST);

            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW) || sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        etTeacherName.setText("" + sTeacherName);
        etPhoneNumber.setText("" + sPhoneNumber);
        etEmail.setText("" + sEmail);
        tvSelectGender.setText("" + sGender);
        tvDOBLabel.setText("" + sDOB);
        etAddress.setText("" + sAddress);
        etTeacherCode.setText("" + sTeacherCode);
        etTeacherPosition.setText("" + sTeacherPosition);
        tvSelectCourses.setText("" + sCoursesText);
        tvSelectAccessRights.setText("" + sAccessRightsText);
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

        civTeacherImage.setClickable(isEditable);
        etTeacherName.setEnabled(isEditable);
        etPhoneNumber.setEnabled(isEditable);
        etEmail.setEnabled(isEditable);
        tvSelectGender.setClickable(isEditable);
        tvSelectDOB.setClickable(isEditable);
        etAddress.setEnabled(isEditable);
        etTeacherCode.setEnabled(isEditable);
        etTeacherPosition.setEnabled(isEditable);
        tvSelectCourses.setClickable(isEditable);
        tvSelectAccessRights.setClickable(isEditable);
        etOtherInformation.setEnabled(isEditable);

    }

    private void titleLabel() {

        switch (sCurrentMode) {
            case Constants.ACTIVITY_MODE_ADD:
                tvTitleLabel.setText("Add Teacher");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Teacher");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Teacher Detail");
                break;
            default:
                tvTitleLabel.setText("Teacher Detail");
                break;

        }

    }

    public void alertDialogSetup() {

        MyAlertDialogInterface myAlertDialogInterface = new MyAlertDialogInterface() {
            @Override
            public void getSelectedItemIndex(int index, String sAlertDialogType) {

                if (sAlertDialogType.matches("GENDER")) {
                    String sGender = genderList.get(index);
                    tvSelectGender.setText("" + sGender);
                }


            }
        };
        myAlertDialog = new MyAlertDialog(this, myAlertDialogInterface);

    }

    public void datePickerSetup() {

        MyDatePickerInterface myDatePickerInterface = new MyDatePickerInterface() {
            @Override
            public void getCalendarTime(long lSelectedTimestamp, String sRequestType) {

                if (sRequestType.matches("DOB")) {

                    lDOB = lSelectedTimestamp;
                    tvSelectDOB.setText(utilityFile.getStringFromLongTimestamp(lSelectedTimestamp, Constants.DATE_FORMAT));

                }

            }

        };

        myDatePicker = new MyDatePicker(this, myDatePickerInterface);

    }

    public void addImageClicked(View view) {

        Intent i = new Intent(this, SelectMediaActivity.class);
        i.putExtra("MULTIPLE_ALLOWED", false);
        i.putExtra("TAKE_PHOTO_ALLOWED", true);
        i.putExtra("GALLERY_IMAGE_ALLOWED", true);
        startActivityForResult(i, Constants.SELECT_DOCUMENT_ACTIVITY_REQUEST);

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

