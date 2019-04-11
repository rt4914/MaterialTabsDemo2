package com.arvind.linebalancing.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arvind.linebalancing.BuildConfig;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.connection.AddConnectionActivity;
import com.arvind.linebalancing.activity.connection.EmployeeMachineMapActivity;
import com.arvind.linebalancing.activity.connection.EmployeeOperationMapActivity;
import com.arvind.linebalancing.activity.connection.LineEmployeeMapActivity;
import com.arvind.linebalancing.activity.connection.LineExecutiveMapActivity;
import com.arvind.linebalancing.activity.connection.LineSupervisorMapActivity;
import com.arvind.linebalancing.activity.connection.MachineOperationMapActivity;
import com.arvind.linebalancing.activity.designation.AddDesignationActivity;
import com.arvind.linebalancing.activity.designation.DesignationListActivity;
import com.arvind.linebalancing.activity.employee.AddEmployeeActivity;
import com.arvind.linebalancing.activity.employee.EmployeeListActivity;
import com.arvind.linebalancing.activity.line.AddLineActivity;
import com.arvind.linebalancing.activity.line.LineListActivity;
import com.arvind.linebalancing.activity.machine.AddMachineActivity;
import com.arvind.linebalancing.activity.machine.MachineListActivity;
import com.arvind.linebalancing.activity.operation.AddOperationActivity;
import com.arvind.linebalancing.activity.operation.OperationListActivity;
import com.arvind.linebalancing.activity.stitch.AddStitchActivity;
import com.arvind.linebalancing.activity.stitch.StitchListActivity;
import com.arvind.linebalancing.adapter.SuggestionListAdapter;
import com.arvind.linebalancing.datastorage.SecurePreferences;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.GetSuggestionDataService;
import com.arvind.linebalancing.table.SuggestionTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.CustomTypefaceSpan;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawer;

    private ImageView ivBanner;
    private TextView tvStitchCount;
    private TextView tvStitchesLabel;
    private TextView tvMachineCount;
    private TextView tvMachinesLabel;
    private TextView tvOperationCount;
    private TextView tvOperationsLabel;
    private TextView tvLineCount;
    private TextView tvLinesLabel;

    private TextView tvEmployeesLabel;
    private TextView tvEmployeeCount;
    private TextView tvDesignationsLabel;
    private TextView tvDesignationCount;

    private TextView tvFooterLabel1;
    private TextView tvFooterLabel2;
    private TextView tvFooterLabel3;
    private TextView tvFooterLabel4;

    private Dialog myDialog;
    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest = null;
    private SecurePreferences mSecurePreferences;

    private UtilityFile utilityFile;

    private int currentPosition;

    private String sAdminId = null;
    private String sAdminName = null;
    private String sAdminEmail = null;
    private String sAdminImage = null;

    //TODO: Add image cropper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    15);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setAnalytics();
        setAnalyticsData();
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

        String sSecurePreferencesKey = BuildConfig.SECURE_PREFERNECES_KEY;
        mSecurePreferences = new SecurePreferences(this, Constants.SP_PREFERENCES_NAME, sSecurePreferencesKey, true);
        String sSignIn = mSecurePreferences.getString(Constants.SP_KEY_LOGGED_IN);

        if (sSignIn == null || sSignIn.isEmpty() || sSignIn.equalsIgnoreCase("false")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return;

        }

        if (utilityFile.isConnectingToInternet()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("SUGGESTION_LIST"));

            showProgressDialog();
            Intent intent = new Intent(this, GetSuggestionDataService.class);
            startService(intent);

        } else {
            showToast(getResources().getString(R.string.no_internet_connection));
        }

        initializeUI();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            showSuggestionRecyclerView();
        }
    };

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

        setAnalytics();
        showSuggestionRecyclerView();

    }

    private void setAnalytics() {

        tvStitchCount = findViewById(R.id.tvStitchCount);
        tvStitchesLabel = findViewById(R.id.tvStitchesLabel);
        tvMachineCount = findViewById(R.id.tvMachineCount);
        tvMachinesLabel = findViewById(R.id.tvMachinesLabel);

        tvOperationCount = findViewById(R.id.tvOperationCount);
        tvOperationsLabel = findViewById(R.id.tvOperationsLabel);
        tvLineCount = findViewById(R.id.tvLineCount);
        tvLinesLabel = findViewById(R.id.tvLinesLabel);

        tvEmployeesLabel = findViewById(R.id.tvEmployeesLabel);
        tvEmployeeCount = findViewById(R.id.tvEmployeeCount);
        tvDesignationsLabel = findViewById(R.id.tvDesignationsLabel);
        tvDesignationCount = findViewById(R.id.tvDesignationCount);

        tvFooterLabel1 = findViewById(R.id.tvFooterLabel1);
        tvFooterLabel2 = findViewById(R.id.tvFooterLabel2);
        tvFooterLabel3 = findViewById(R.id.tvFooterLabel3);
        tvFooterLabel4 = findViewById(R.id.tvFooterLabel4);

        tvStitchCount.setTypeface(utilityFile.getTfSemiBold());
        tvStitchesLabel.setTypeface(utilityFile.getTfMedium());
        tvMachineCount.setTypeface(utilityFile.getTfSemiBold());
        tvMachinesLabel.setTypeface(utilityFile.getTfMedium());
        tvOperationCount.setTypeface(utilityFile.getTfSemiBold());
        tvOperationsLabel.setTypeface(utilityFile.getTfMedium());
        tvLineCount.setTypeface(utilityFile.getTfSemiBold());
        tvLinesLabel.setTypeface(utilityFile.getTfMedium());

        tvEmployeesLabel.setTypeface(utilityFile.getTfRegular());
        tvEmployeeCount.setTypeface(utilityFile.getTfMedium());
        tvDesignationsLabel.setTypeface(utilityFile.getTfRegular());
        tvDesignationCount.setTypeface(utilityFile.getTfMedium());

        tvFooterLabel1.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel2.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel3.setTypeface(utilityFile.getTfMedium());
        tvFooterLabel4.setTypeface(utilityFile.getTfMedium());

        setAnalyticsData();

    }

    private void setAnalyticsData() {

        int iDesignationCount = AppDatabase.getInstance(this).designationTableDao().getAllDesignations().size();
        int iLineCount = AppDatabase.getInstance(this).lineTableDao().getAllLines().size();
        int iOperationCount = AppDatabase.getInstance(this).operationTableDao().getAllOperations().size();
        int iMachineCount = AppDatabase.getInstance(this).machineTableDao().getAllMachines().size();
        int iEmployeeCount = AppDatabase.getInstance(this).employeeTableDao().getAllEmployees().size();
        int iStitchCount = AppDatabase.getInstance(this).stitchTableDao().getAllStitches().size();

        tvDesignationCount.setText(String.valueOf(iDesignationCount));
        tvEmployeeCount.setText(String.valueOf(iEmployeeCount));
        tvLineCount.setText(String.valueOf(iLineCount));
        tvMachineCount.setText(String.valueOf(iMachineCount));
        tvOperationCount.setText(String.valueOf(iOperationCount));
        tvStitchCount.setText(String.valueOf(iStitchCount));

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

        CircleImageView civAdminImage = header.findViewById(R.id.civAdminImage);
        TextView tvAdminName = header.findViewById(R.id.tvAdminName);
        TextView tvAdminEmail = header.findViewById(R.id.tvAdminEmail);
        tvAdminName.setTypeface(utilityFile.getTfRegular());
        tvAdminEmail.setTypeface(utilityFile.getTfMedium());

        if (mSecurePreferences.getString(Constants.SP_KEY_ADMIN_NAME) != null) {
            sAdminEmail = mSecurePreferences.getString(Constants.SP_KEY_ADMIN_EMAIL);
            sAdminImage = mSecurePreferences.getString(Constants.SP_KEY_ADMIN_IMAGE);
            sAdminName = mSecurePreferences.getString(Constants.SP_KEY_ADMIN_NAME);
        }

    }

    private void showSuggestionRecyclerView() {

        TextView tvSuggestions = findViewById(R.id.tvSuggestions);
        tvSuggestions.setTypeface(utilityFile.getTfMedium());

        RecyclerView rvSuggestion = findViewById(R.id.rvSuggestion);
        rvSuggestion.setLayoutManager(new LinearLayoutManager(this));

        List<SuggestionTable> suggestionTableList = AppDatabase.getInstance(this).suggestionTableDao().getAllSuggestions();

        if (suggestionTableList != null && !suggestionTableList.isEmpty()) {
            rvSuggestion.setVisibility(View.VISIBLE);
            SuggestionListAdapter adapter = new SuggestionListAdapter(this, suggestionTableList);
            rvSuggestion.setAdapter(adapter);
        } else {
            rvSuggestion.setVisibility(View.GONE);
        }

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
        TextView tvAddStitch = myDialog.findViewById(R.id.tvAddStitch);
        TextView tvAddMachine = myDialog.findViewById(R.id.tvAddMachine);
        TextView tvAddLine = myDialog.findViewById(R.id.tvAddLine);
        TextView tvAddOperation = myDialog.findViewById(R.id.tvAddOperation);
        TextView tvAddEmployee = myDialog.findViewById(R.id.tvAddEmployee);
        TextView tvAddDesignation = myDialog.findViewById(R.id.tvAddDesignation);
        TextView tvAddConnection = myDialog.findViewById(R.id.tvAddConnection);

        tvAddOptionLabel.setTypeface(utilityFile.getTfMedium());
        tvAddStitch.setTypeface(utilityFile.getTfRegular());
        tvAddMachine.setTypeface(utilityFile.getTfRegular());
        tvAddLine.setTypeface(utilityFile.getTfRegular());
        tvAddOperation.setTypeface(utilityFile.getTfRegular());
        tvAddEmployee.setTypeface(utilityFile.getTfRegular());
        tvAddConnection.setTypeface(utilityFile.getTfRegular());
        tvAddDesignation.setTypeface(utilityFile.getTfRegular());

        ivClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        tvAddStitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddStitchActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddMachineActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddLineActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddOperationActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddEmployeeActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddDesignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddDesignationActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AddConnectionActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    public void stitchButtonClicked(View view) {

        Intent i = new Intent(this, StitchListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void machineButtonClicked(View view) {

        Intent i = new Intent(this, MachineListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void operationButtonClicked(View view) {

        Intent i = new Intent(this, OperationListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void lineButtonClicked(View view) {

        Intent i = new Intent(this, LineListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void employeeButtonClicked(View view) {

        Intent i = new Intent(this, EmployeeListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void designationButtonClicked(View view) {

        Intent i = new Intent(this, DesignationListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    public void fabAddConnectionsClicked(View view) {

        myDialog.setContentView(R.layout.custom_pop_up_add_connection);

        ImageView ivClosePopUp = myDialog.findViewById(R.id.ivClosePopUp);
        TextView tvAddOptionLabel = myDialog.findViewById(R.id.tvAddOptionLabel);
        TextView tvAddMacOpConnection = myDialog.findViewById(R.id.tvAddMacOpConnection);
        TextView tvAddEmpOpConnection = myDialog.findViewById(R.id.tvAddEmpOpConnection);
        TextView tvAddEmpMacConnection = myDialog.findViewById(R.id.tvAddEmpMacConnection);
        TextView tvAddLineExeConnection = myDialog.findViewById(R.id.tvAddLineExeConnection);
        TextView tvAddLineSupConnection = myDialog.findViewById(R.id.tvAddLineSupConnection);
        TextView tvAddLineEmpConnection = myDialog.findViewById(R.id.tvAddLineEmpConnection);

        tvAddOptionLabel.setTypeface(utilityFile.getTfMedium());
        tvAddMacOpConnection.setTypeface(utilityFile.getTfRegular());
        tvAddEmpOpConnection.setTypeface(utilityFile.getTfRegular());
        tvAddEmpMacConnection.setTypeface(utilityFile.getTfRegular());
        tvAddLineExeConnection.setTypeface(utilityFile.getTfRegular());
        tvAddLineSupConnection.setTypeface(utilityFile.getTfRegular());
        tvAddLineEmpConnection.setTypeface(utilityFile.getTfRegular());

        ivClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        tvAddMacOpConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, MachineOperationMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddEmpOpConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, EmployeeOperationMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddEmpMacConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, EmployeeMachineMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddLineExeConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, LineExecutiveMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddLineEmpConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, LineEmployeeMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        tvAddLineSupConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, LineSupervisorMapActivity.class);
                i.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                myDialog.dismiss();

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }
}

