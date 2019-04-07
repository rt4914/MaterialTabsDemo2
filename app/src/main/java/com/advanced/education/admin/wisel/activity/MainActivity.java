package com.advanced.education.admin.wisel.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.BuildConfig;
import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.datastorage.SecurePreferences;
import com.advanced.education.admin.wisel.serverutilities.GeneralVolleyRequest;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.CustomTypefaceSpan;
import com.advanced.education.admin.wisel.utilities.UtilityFile;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawer;

    private ImageView ivBanner;
    private TextView tvCourseCount;
    private TextView tvCoursesLabel;
    private TextView tvStandardCount;
    private TextView tvStandardsLabel;
    private TextView tvTeacherCount;
    private TextView tvTeachersLabel;
    private TextView tvStudentCount;
    private TextView tvStudentsLabel;

    private TextView tvSMSPackLabel;
    private TextView tvSMSPack;
    private TextView tvStorageLabel;
    private TextView tvStorage;

    private TextView tvFooterLabel1;
    private TextView tvFooterLabel2;
    private TextView tvFooterLabel3;
    private TextView tvFooterLabel4;

    private Dialog myDialog;
    private Dialog myDialogMarquee;
    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest = null;
    private SecurePreferences mSecurePreferences;

    private UtilityFile utilityFile;

    private int currentPosition;

    private String sInstituteId = null;
    private String sTeacherId = null;
    private String sTeacherName = null;
    private String sTeacherEmail = null;
    private String sTeacherImage = null;

    //TODO: Add image cropper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    15);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    private void initializeData() {

        utilityFile = new UtilityFile(this);
        myDialog = new Dialog(this);
        myDialogMarquee = new Dialog(this);

        String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
        mSecurePreferences = new SecurePreferences(this, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);
        String sSignIn = mSecurePreferences.getString(Constants.SP_KEY_SIGN_IN);
        sTeacherId = mSecurePreferences.getString(Constants.SP_KEY_TEACHER_ID);
        sInstituteId = mSecurePreferences.getString(Constants.SP_KEY_INSTITUTE_ID);

        initializeUI();

    }

    private void initializeUI() {

        setToolbarTitle(getResources().getString(R.string.app_name));

        Toolbar toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);

        }

        View header = navigationView.getHeaderView(0);

        setNavigationHeader(header);

        ivBanner = findViewById(R.id.ivBanner);

        Glide.with(this).load("https://via.placeholder.com/700x300?text=Banner+Images").into(ivBanner);

        setMarquee();

        tvCourseCount = findViewById(R.id.tvCourseCount);
        tvCoursesLabel = findViewById(R.id.tvCoursesLabel);
        tvStandardCount = findViewById(R.id.tvStandardCount);
        tvStandardsLabel = findViewById(R.id.tvStandardsLabel);
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        tvTeachersLabel = findViewById(R.id.tvTeachersLabel);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvStudentsLabel = findViewById(R.id.tvStudentsLabel);

        tvSMSPackLabel = findViewById(R.id.tvSMSPackLabel);
        tvSMSPack = findViewById(R.id.tvSMSPack);
        tvStorageLabel = findViewById(R.id.tvStorageLabel);
        tvStorage = findViewById(R.id.tvStorage);

        tvFooterLabel1 = findViewById(R.id.tvFooterLabel1);
        tvFooterLabel2 = findViewById(R.id.tvFooterLabel2);
        tvFooterLabel3 = findViewById(R.id.tvFooterLabel3);
        tvFooterLabel4 = findViewById(R.id.tvFooterLabel4);

        tvCourseCount.setTypeface(utilityFile.getTfSemiBold());
        tvCoursesLabel.setTypeface(utilityFile.getTfMedium());
        tvStandardCount.setTypeface(utilityFile.getTfSemiBold());
        tvStandardsLabel.setTypeface(utilityFile.getTfMedium());
        tvTeacherCount.setTypeface(utilityFile.getTfSemiBold());
        tvTeachersLabel.setTypeface(utilityFile.getTfMedium());
        tvStudentCount.setTypeface(utilityFile.getTfSemiBold());
        tvStudentsLabel.setTypeface(utilityFile.getTfMedium());

        tvSMSPackLabel.setTypeface(utilityFile.getTfRegular());
        tvSMSPack.setTypeface(utilityFile.getTfMedium());
        tvStorageLabel.setTypeface(utilityFile.getTfRegular());
        tvStorage.setTypeface(utilityFile.getTfMedium());

        tvFooterLabel1.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel2.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel3.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel4.setTypeface(utilityFile.getTfMedium());

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.nav_sign_out:
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Sign Out");
                alertDialogBuilder.setMessage("Do you wish to sign out?");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
                        mSecurePreferences = new SecurePreferences(MainActivity.this, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);
                        mSecurePreferences.put(Constants.SP_KEY_SIGN_IN, "false");

//                        Intent intent = new Intent(MainActivity.this, EmailVerificationActivity.class);
//                        startActivity(intent);
//                        finish();

                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.nav_customer_care:
                final AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this);
                alertDialogBuilder2.setTitle("Customer Care");
                alertDialogBuilder2.setMessage("Do you want to call customer care?");
                alertDialogBuilder2.setCancelable(true);
                alertDialogBuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + "7434978304"));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    9);
                        } else {
                            try {
                                startActivity(callIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

                alertDialogBuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });

                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                alertDialog2.show();
                break;

            case R.id.nav_share_teacher:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String sText = "\nHello\n\nLink to the Teacher Application is: \n\n";
                    sText = sText + "https://play.google.com/store/apps/details?id=com.advanced.attendance.teacher";
                    sText = sText + "\n\nThank You";
                    i.putExtra(Intent.EXTRA_TEXT, sText);
                    startActivity(Intent.createChooser(i, "Share app using.."));
                } catch (Exception e) {
                    //e.toString();
                }
                break;

            case R.id.nav_share_parent:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String sText = "\nHello\n\nLink to the Parent Application is: \n\n";
                    sText = sText + "https://play.google.com/store/apps/details?id=com.advanced.attendance.parent";
                    sText = sText + "\n\nThank You";
                    i.putExtra(Intent.EXTRA_TEXT, sText);
                    startActivity(Intent.createChooser(i, "Share app using.."));
                } catch (Exception e) {
                    //e.toString();
                }
                break;

            default:
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "7434978304"));
                startActivity(callIntent);
            } else {
                showToast("Permission denied.");
            }
        }

        if (requestCode == 9) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "7434978304"));
                startActivity(callIntent);
            } else {
                showToast("Permission denied.");
            }
        }

        if (requestCode == 15) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Start Image Downloading
            } else {
                showToast("Permission denied.");
            }
        }


    }

    private void setNavigationHeader(View header) {

        CircleImageView civTeacherImage = header.findViewById(R.id.civTeacherImage);
        TextView tvTeacherName = header.findViewById(R.id.tvTeacherName);
        TextView tvTeacherEmail = header.findViewById(R.id.tvTeacherEmail);
        tvTeacherName.setTypeface(utilityFile.getTfRegular());
        tvTeacherEmail.setTypeface(utilityFile.getTfMedium());

        if (mSecurePreferences.getString(Constants.SP_KEY_TEACHER_NAME) != null) {
            sTeacherEmail = mSecurePreferences.getString(Constants.SP_KEY_TEACHER_EMAIL);
            sTeacherImage = mSecurePreferences.getString(Constants.SP_KEY_TEACHER_IMAGE);
            sTeacherName = mSecurePreferences.getString(Constants.SP_KEY_TEACHER_NAME);
        }

    }

    private void setMarquee() {

        TextView tvMarquee = findViewById(R.id.tvMarquee);
        tvMarquee.setTypeface(utilityFile.getTfMedium());
        tvMarquee.setSelected(true);

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void setProgressDialog() {

        if (pdLoading == null) {

            pdLoading = new ProgressDialog(this);
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);

        }
    }

    private void showProgressDialog() {

        if (pdLoading == null) {
            setProgressDialog();
        }
        if (pdLoading.isShowing()) {
            pdLoading.cancel();
        }
        pdLoading.show();

    }

    private void hideProgressDialog() {

        if (pdLoading == null) {
            setProgressDialog();
        }

        if (pdLoading.isShowing()) {
            pdLoading.cancel();
        }

    }

    private void applyFontToMenuItem(MenuItem mi) {

        Typeface font = Typeface.createFromAsset(getAssets(), Constants.FONT_TYPE_REGULAR);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);

    }

    private void setToolbarTitle(String title) {

        Typeface tfRegular = Typeface.createFromAsset(getAssets(), Constants.FONT_TYPE_REGULAR);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(tfRegular);
                    break;
                }
            }
        }

    }

    public void fabAddOptionsClicked(View view) {

        myDialog.setContentView(R.layout.custom_pop_up_add);

        ImageView ivClosePopUp = myDialog.findViewById(R.id.ivClosePopUp);
        TextView tvAddOptionLabel = myDialog.findViewById(R.id.tvAddOptionLabel);
        TextView tvAddCourse = myDialog.findViewById(R.id.tvAddCourse);
        TextView tvAddStandard = myDialog.findViewById(R.id.tvAddStandard);
        TextView tvAddStudent = myDialog.findViewById(R.id.tvAddStudent);
        TextView tvAddTeacher = myDialog.findViewById(R.id.tvAddTeacher);
        TextView tvCreateNewSession = myDialog.findViewById(R.id.tvCreateNewSession);

        tvAddOptionLabel.setTypeface(utilityFile.getTfMedium());
        tvAddCourse.setTypeface(utilityFile.getTfRegular());
        tvAddStandard.setTypeface(utilityFile.getTfRegular());
        tvAddStudent.setTypeface(utilityFile.getTfRegular());
        tvAddTeacher.setTypeface(utilityFile.getTfRegular());
        tvCreateNewSession.setTypeface(utilityFile.getTfRegular());

        ivClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        tvAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddCourseActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddStandardActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddStudentActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddTeacherActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    public void marqueeButtonClicked(View view){

        myDialogMarquee.setContentView(R.layout.custom_pop_up_marquee);

        ImageView ivClosePopUp = myDialogMarquee.findViewById(R.id.ivClosePopUp);
        TextView tvMarqueeLabel = myDialogMarquee.findViewById(R.id.tvMarqueeLabel);
        TextView tvMarqueeDescription = myDialogMarquee.findViewById(R.id.tvMarqueeDescription);
        EditText etMarquee = myDialogMarquee.findViewById(R.id.etMarquee);
        Button bSave = myDialogMarquee.findViewById(R.id.bSave);
        Button bClear = myDialogMarquee.findViewById(R.id.bClear);

        tvMarqueeLabel.setTypeface(utilityFile.getTfMedium());
        tvMarqueeDescription.setTypeface(utilityFile.getTfRegular());
        etMarquee.setTypeface(utilityFile.getTfRegular());
        bSave.setTypeface(utilityFile.getTfRegular());
        bClear.setTypeface(utilityFile.getTfRegular());

        ivClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogMarquee.dismiss();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDialogMarquee.dismiss();

            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDialogMarquee.dismiss();

            }
        });

        myDialogMarquee.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialogMarquee.show();

    }

    public void courseButtonClicked(View view) {

        Intent i = new Intent(this, CourseListActivity.class);
        startActivity(i);

    }

    public void standardButtonClicked(View view) {

        Intent i = new Intent(this, StandardListActivity.class);
        startActivity(i);

    }

    public void studentButtonClicked(View view) {

        Intent i = new Intent(this, StudentListActivity.class);
        startActivity(i);

    }

    public void teacherButtonClicked(View view) {

        Intent i = new Intent(this, TeacherListActivity.class);
        startActivity(i);

    }

    public void onBannerClicked(View view){

        Intent i = new Intent(this, BannerListActivity.class);
        startActivity(i);

    }

}

