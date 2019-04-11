package com.arvind.linebalancing.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.connection.AddConnectionActivity;
import com.arvind.linebalancing.activity.employee.AddEmployeeActivity;
import com.arvind.linebalancing.activity.employee.AddEmployeeRatingActivity;
import com.arvind.linebalancing.activity.employee.EmployeeConnectionListActivity;
import com.arvind.linebalancing.activity.employee.EmployeeRatingListActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyAlertDialog;
import com.arvind.linebalancing.utilities.MyAlertDialogInterface;
import com.arvind.linebalancing.utilities.MyDeleteDialogInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder> {

    private static final String TAG = EmployeeListAdapter.class.getSimpleName();
    private Context context;
    private List<EmployeeTable> employeeTableList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sEmployeeId;

    public EmployeeListAdapter(Context context, List<EmployeeTable> employeeTableList) {

        this.context = context;
        this.employeeTableList = employeeTableList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_employee, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (employeeTableList.get(i) != null) {
            myViewHolder.tvEmployeeName.setText(employeeTableList.get(i).getEmployeeName());

            DesignationTable designationTable = AppDatabase.getInstance(context).designationTableDao()
                    .getDesignationById(employeeTableList.get(i).getDesignationId());

            if (designationTable != null) {
                myViewHolder.tvEmployeeDesignation.setText("(" + designationTable.getDesignationName() + ")");
            }

            myViewHolder.tvEmployeeNo.setText("Employee No.: " + employeeTableList.get(i).getEmployeeNo());
        }
//        if ((i + 1) == employeeTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return employeeTableList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvEmployeeNo;
        private TextView tvEmployeeName;
        private TextView tvEmployeeDesignation;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvEmployeeNo = itemView.findViewById(R.id.tvEmployeeNo);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeDesignation = itemView.findViewById(R.id.tvEmployeeDesignation);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvEmployeeNo.setTypeface(utilityFile.getTfRegular());
            tvEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvEmployeeDesignation.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<String> optionArrayList = new ArrayList<>();

                    optionArrayList.add("View Employee");
                    optionArrayList.add("Add Employee Rating");
                    optionArrayList.add("Employee Rating List");
                    optionArrayList.add("View Connection List");

                    alertDialogSetup(getAdapterPosition());
                    myAlertDialog.createAlertBox("Select Option", true, optionArrayList, "OPTIONS");


                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Employee", true, "DELETE", getAdapterPosition());
                    return true;

                }
            });

        }

    }

    private void alertDialogSetup(int adapterPosition) {

        final String sEmployeeId = employeeTableList.get(adapterPosition).getId();

        MyAlertDialogInterface myAlertDialogInterface = new MyAlertDialogInterface() {
            @Override
            public void getSelectedItemIndex(int index, String sAlertDialogType) {
                if (sAlertDialogType.matches("OPTIONS")) {

                    switch (index) {
                        case 0:
                            Intent intent = new Intent(context, AddEmployeeActivity.class);
                            intent.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                            intent.putExtra("EMPLOYEE_ID", sEmployeeId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);
                            break;

                        case 1:
                            Intent intent1 = new Intent(context, AddEmployeeRatingActivity.class);
                            intent1.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent1);
                            break;

                        case 2:
                            Intent intent2 = new Intent(context, EmployeeRatingListActivity.class);
                            intent2.putExtra("EMPLOYEE_ID", sEmployeeId);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent2);
                            break;

                        case 3:
                            Intent intent3 = new Intent(context, EmployeeConnectionListActivity.class);
                            intent3.putExtra("EMPLOYEE_ID", sEmployeeId);
                            intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent3);
                            break;

                        default:
                            break;

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myAlertDialogInterface);

    }

    public void deleteDialogSetup() {

        MyDeleteDialogInterface myDeleteDialogInterface = new MyDeleteDialogInterface() {
            @Override
            public void isDeleted(boolean value, String sAlertDialogType, int adapterPosition) {

                if(sAlertDialogType.matches("DELETE")){

                    if (value) {
                        iPosition = adapterPosition;
                        sEmployeeId = employeeTableList.get(adapterPosition).getId();

                        Log.d(TAG, "onClick: " + sEmployeeId);

                        if (sEmployeeId == null) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteEmployeeRequest(sEmployeeId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteEmployeeRequest(String sEmployeeId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }

        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sEmployeeId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_EMPLOYEE,
                Constants.BASE_SERVER_URL + "delete_employee",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_EMPLOYEE)) {
                    deleteEmployeeResponse(response);
                } else {
                    showToast(context.getResources().getString(R.string.unexpected_error));
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                hideProgressDialog();
                showToast(context.getResources().getString(R.string.volley_server_error));

            }
        };

        mGeneralVolleyRequest = new GeneralVolleyRequest(context, mRequestCallback);

    }

    private void deleteEmployeeResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).employeeTableDao().deleteEmployeeById(sEmployeeId);
                employeeTableList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Employee deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteEmployeeResponse: ", e);
            showToast(context.getResources().getString(R.string.unexpected_error));
        }

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void setProgressDialog() {

        if (pdLoading == null) {

            pdLoading = new ProgressDialog(context);
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

}
