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
import com.advanced.education.admin.wisel.table.StandardTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.MyAlertDialog;
import com.advanced.education.admin.wisel.utilities.MyAlertDialogInterface;
import com.advanced.education.admin.wisel.utilities.MyDatePicker;
import com.advanced.education.admin.wisel.utilities.MyDatePickerInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStudentActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private ImageView ivSave;
    private TextView tvBasicDetailsLabel;
    private CircleImageView civStudentImage;
    private TextView tvStudentNameLabel;
    private EditText etStudentName;
    private TextView tvStudentPhoneNumberLabel;
    private EditText etStudentPhoneNumber;
    private TextView tvStudentEmailLabel;
    private EditText etStudentEmail;
    private TextView tvGenderLabel;
    private TextView tvSelectGender;
    private TextView tvDOBLabel;
    private TextView tvSelectDOB;
    private TextView tvAddressLabel;
    private EditText etAddress;
    private TextView tvInstituteDetailsLabel;
    private TextView tvStudentIDLabel;
    private EditText etStudentID;
    private TextView tvStudentEnrollmentNumberLabel;
    private EditText etStudentEnrollmentNumber;
    private TextView tvStandardLabel;
    private TextView tvSelectStandard;
    private TextView tvStudentAccessRightsLabel;
    private TextView tvSelectStudentAccessRights;
    private TextView tvParent1DetailsLabel;
    private TextView tvParentName1Label;
    private EditText etParentName1;
    private TextView tvParentPhoneNumber1Label;
    private EditText etParentPhoneNumber1;
    private TextView tvParentEmail1Label;
    private EditText etParentEmail1;
    private TextView tvParent1AccessRightsLabel;
    private TextView tvSelectParent1AccessRights;
    private TextView tvParent2DetailsLabel;
    private TextView tvParentName2Label;
    private EditText etParentName2;
    private TextView tvParentPhoneNumber2Label;
    private EditText etParentPhoneNumber2;
    private TextView tvParentEmail2Label;
    private EditText etParentEmail2;
    private TextView tvParent2AccessRightsLabel;
    private TextView tvSelectParent2AccessRights;
    private TextView tvAdditionalDetailsLabel;
    private TextView tvOtherInformationLabel;
    private EditText etOtherInformation;

    private Toast mToast = null;

    private MyAlertDialog myAlertDialog = null;
    private MyDatePicker myDatePicker = null;
    private UtilityFile utilityFile;

    private long lDOB = 0;

    private String sCurrentMode;

    private String sStudentName = "Saurav Pratihar";
    private String sStudentPhoneNumber = "9723124260";
    private String sEmail = "sauravp@wisel.in";
    private String sGender = "Male";
    private String sDOB = "08 July 1995";
    private String sAddress = "204, Shree Rang Mall, Gandhinagar, Gujarat";
    private String sStudentId = "201901005";
    private String sParentPhoneNumber = "9723124260";
    private String sStandardText = "1 standard selected";
    private String sStudentAccessRightsText = "15 rights given";
    private String sOtherInformation = "";

    private ArrayList<String> selectedStandardIdList;
    private List<StandardTable> selectedStandardTableList;
    private List<String> documentList;
    private List<String> genderList;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

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
                    civStudentImage.setImageURI(Uri.parse(documentList.get(0)));
                }
            } else {
                showToast("No file selected.");
            }
        } else if (resultCode == RESULT_OK && requestCode == Constants.SELECT_STANDARD_REQUEST) {

            selectedStandardIdList = new ArrayList<>();
            selectedStandardIdList.addAll(data.getStringArrayListExtra("SELECTED_STANDARD"));

            selectedStandardTableList = new ArrayList<>();

            for (String sId : selectedStandardIdList) {
                selectedStandardTableList.add(new StandardTable());
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

        selectedStandardIdList = new ArrayList<>();
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
        civStudentImage = findViewById(R.id.civStudentImage);
        tvStudentNameLabel = findViewById(R.id.tvStudentNameLabel);
        etStudentName = findViewById(R.id.etStudentName);
        tvStudentPhoneNumberLabel = findViewById(R.id.tvStudentPhoneNumberLabel);
        etStudentPhoneNumber = findViewById(R.id.etStudentPhoneNumber);
        tvStudentEmailLabel = findViewById(R.id.tvStudentEmailLabel);
        etStudentEmail = findViewById(R.id.etStudentEmail);
        tvGenderLabel = findViewById(R.id.tvGenderLabel);
        tvSelectGender = findViewById(R.id.tvSelectGender);
        tvDOBLabel = findViewById(R.id.tvDOBLabel);
        tvSelectDOB = findViewById(R.id.tvSelectDOB);
        tvAddressLabel = findViewById(R.id.tvAddressLabel);
        etAddress = findViewById(R.id.etAddress);

        tvInstituteDetailsLabel = findViewById(R.id.tvInstituteDetailsLabel);
        tvStudentIDLabel = findViewById(R.id.tvStudentIDLabel);
        etStudentID = findViewById(R.id.etStudentID);
        tvStudentEnrollmentNumberLabel = findViewById(R.id.tvStudentEnrollmentNumberLabel);
        etStudentEnrollmentNumber = findViewById(R.id.etStudentEnrollmentNumber);
        tvStandardLabel = findViewById(R.id.tvStandardLabel);
        tvSelectStandard = findViewById(R.id.tvSelectStandard);
        tvStudentAccessRightsLabel = findViewById(R.id.tvStudentAccessRightsLabel);
        tvSelectStudentAccessRights = findViewById(R.id.tvSelectStudentAccessRights);

        tvParent1DetailsLabel = findViewById(R.id.tvParent1DetailsLabel);
        tvParentName1Label = findViewById(R.id.tvParentName1Label);
        etParentName1 = findViewById(R.id.etParentName1);
        tvParentPhoneNumber1Label = findViewById(R.id.tvParentPhoneNumber1Label);
        etParentPhoneNumber1 = findViewById(R.id.etParentPhoneNumber1);
        tvParentEmail1Label = findViewById(R.id.tvParentEmail1Label);
        etParentEmail1 = findViewById(R.id.etParentEmail1);
        tvParent1AccessRightsLabel = findViewById(R.id.tvParent1AccessRightsLabel);
        tvSelectParent1AccessRights = findViewById(R.id.tvSelectParent1AccessRights);

        tvParent2DetailsLabel = findViewById(R.id.tvParent2DetailsLabel);
        tvParentName2Label = findViewById(R.id.tvParentName2Label);
        etParentName2 = findViewById(R.id.etParentName2);
        tvParentPhoneNumber2Label = findViewById(R.id.tvParentPhoneNumber2Label);
        etParentPhoneNumber2 = findViewById(R.id.etParentPhoneNumber2);
        tvParentEmail2Label = findViewById(R.id.tvParentEmail2Label);
        etParentEmail2 = findViewById(R.id.etParentEmail2);
        tvParent2AccessRightsLabel = findViewById(R.id.tvParent2AccessRightsLabel);
        tvSelectParent2AccessRights = findViewById(R.id.tvSelectParent2AccessRights);

        tvAdditionalDetailsLabel = findViewById(R.id.tvAdditionalDetailsLabel);
        tvOtherInformationLabel = findViewById(R.id.tvOtherInformationLabel);
        etOtherInformation = findViewById(R.id.etOtherInformation);

        tvTitleLabel.setTypeface(utilityFile.getTfBold());

        tvBasicDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvStudentNameLabel.setTypeface(utilityFile.getTfRegular());
        etStudentName.setTypeface(utilityFile.getTfRegular());
        tvStudentPhoneNumberLabel.setTypeface(utilityFile.getTfRegular());
        etStudentPhoneNumber.setTypeface(utilityFile.getTfRegular());
        tvStudentEmailLabel.setTypeface(utilityFile.getTfRegular());
        etStudentEmail.setTypeface(utilityFile.getTfRegular());
        tvGenderLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectGender.setTypeface(utilityFile.getTfRegular());
        tvDOBLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectDOB.setTypeface(utilityFile.getTfRegular());
        tvAddressLabel.setTypeface(utilityFile.getTfRegular());
        etAddress.setTypeface(utilityFile.getTfRegular());

        tvInstituteDetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvStudentIDLabel.setTypeface(utilityFile.getTfRegular());
        etStudentID.setTypeface(utilityFile.getTfRegular());
        tvStudentEnrollmentNumberLabel.setTypeface(utilityFile.getTfRegular());
        etStudentEnrollmentNumber.setTypeface(utilityFile.getTfRegular());
        tvStandardLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectStandard.setTypeface(utilityFile.getTfRegular());
        tvStudentAccessRightsLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectStudentAccessRights.setTypeface(utilityFile.getTfRegular());

        tvParent1DetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvParentName1Label.setTypeface(utilityFile.getTfRegular());
        etParentName1.setTypeface(utilityFile.getTfRegular());
        tvParentPhoneNumber1Label.setTypeface(utilityFile.getTfRegular());
        etParentPhoneNumber1.setTypeface(utilityFile.getTfRegular());
        tvParentEmail1Label.setTypeface(utilityFile.getTfRegular());
        etParentEmail1.setTypeface(utilityFile.getTfRegular());
        tvParent1AccessRightsLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectParent1AccessRights.setTypeface(utilityFile.getTfRegular());

        tvParent2DetailsLabel.setTypeface(utilityFile.getTfMedium());
        tvParentName2Label.setTypeface(utilityFile.getTfRegular());
        etParentName2.setTypeface(utilityFile.getTfRegular());
        tvParentPhoneNumber2Label.setTypeface(utilityFile.getTfRegular());
        etParentPhoneNumber2.setTypeface(utilityFile.getTfRegular());
        tvParentEmail2Label.setTypeface(utilityFile.getTfRegular());
        etParentEmail2.setTypeface(utilityFile.getTfRegular());
        tvParent2AccessRightsLabel.setTypeface(utilityFile.getTfRegular());
        tvSelectParent2AccessRights.setTypeface(utilityFile.getTfRegular());

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

        tvSelectStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(AddStudentActivity.this, SelectStandardActivity.class);
                ii.putExtra("MULTIPLE_ALLOWED", false);
                //  ii.putExtra("SELECTED_STANDARD", selectedStandardIdList);
                startActivityForResult(ii, Constants.SELECT_STANDARD_REQUEST);

            }
        });

        if (sCurrentMode.matches(Constants.ACTIVITY_MODE_VIEW) || sCurrentMode.matches(Constants.ACTIVITY_MODE_EDIT)) {
            setInitialData();
        }

    }

    private void setInitialData() {

        etStudentName.setText("" + sStudentName);
        etStudentPhoneNumber.setText("" + sStudentPhoneNumber);
        etStudentEmail.setText("" + sEmail);
        tvSelectGender.setText("" + sGender);
        tvDOBLabel.setText("" + sDOB);
        etAddress.setText("" + sAddress);
        etStudentID.setText("" + sStudentId);
        etParentPhoneNumber1.setText("" + sParentPhoneNumber);
        tvSelectStandard.setText("" + sStandardText);
        tvSelectStudentAccessRights.setText("" + sStudentAccessRightsText);
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

        civStudentImage.setClickable(isEditable);
        etStudentName.setEnabled(isEditable);
        etStudentPhoneNumber.setEnabled(isEditable);
        etStudentEmail.setEnabled(isEditable);
        tvSelectGender.setClickable(isEditable);
        tvSelectDOB.setClickable(isEditable);
        etAddress.setEnabled(isEditable);
        etStudentID.setEnabled(isEditable);
        etParentPhoneNumber1.setEnabled(isEditable);
        tvSelectStandard.setClickable(isEditable);
        tvSelectStudentAccessRights.setClickable(isEditable);
        etOtherInformation.setEnabled(isEditable);

    }


    private void titleLabel() {

        switch (sCurrentMode) {
            case Constants.ACTIVITY_MODE_ADD:
                tvTitleLabel.setText("Add Student");
                break;
            case Constants.ACTIVITY_MODE_EDIT:
                tvTitleLabel.setText("Edit Student");
                break;
            case Constants.ACTIVITY_MODE_VIEW:
                tvTitleLabel.setText("Student Detail");
                break;
            default:
                tvTitleLabel.setText("Student Detail");
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

