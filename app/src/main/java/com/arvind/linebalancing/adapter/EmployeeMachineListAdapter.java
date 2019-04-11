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
import com.arvind.linebalancing.activity.connection.EmployeeMachineMapActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeMachineMapTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyAlertDialog;
import com.arvind.linebalancing.utilities.MyDeleteDialogInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class EmployeeMachineListAdapter extends RecyclerView.Adapter<EmployeeMachineListAdapter.MyViewHolder> {

    private static final String TAG = EmployeeMachineListAdapter.class.getSimpleName();
    private Context context;
    private List<String> machineIdList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sEmployeeId;
    private String sEmployeeMachineMapId;

    public EmployeeMachineListAdapter(Context context, String sEmployeeId, List<String> machineIdList) {

        this.context = context;
        this.sEmployeeId = sEmployeeId;
        this.machineIdList = machineIdList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_employee_machine, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (machineIdList.get(i) != null) {

            MachineTable machineTable = AppDatabase.getInstance(context).machineTableDao()
                    .getMachineById(machineIdList.get(i));
            if (machineTable != null) {
                myViewHolder.tvMachineName.setVisibility(View.VISIBLE);
                myViewHolder.tvMachineName.setText(machineTable.getMachineName());
            } else {
                myViewHolder.tvMachineName.setVisibility(View.GONE);
            }
        }
//        if ((i + 1) == employeeMachineMapTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return machineIdList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvMachineName;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvMachineName = itemView.findViewById(R.id.tvMachineName);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvMachineName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EmployeeMachineMapTable employeeMachineMapTable = AppDatabase.getInstance(context)
                            .employeeMachineMapTableDao().getEmployeeMachineTableByBothId(sEmployeeId, machineIdList.get(getAdapterPosition()));

                    Intent i = new Intent(context, EmployeeMachineMapActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("EMPLOYEE_ID", sEmployeeId);
                    i.putExtra("EMPLOYEE_MACHINE_MAP_ID", employeeMachineMapTable.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Employee Machine Connection", true, "DELETE", getAdapterPosition());
                    return true;

                }
            });

        }

    }

    public void deleteDialogSetup() {

        MyDeleteDialogInterface myDeleteDialogInterface = new MyDeleteDialogInterface() {
            @Override
            public void isDeleted(boolean value, String sAlertDialogType, int adapterPosition) {

                if(sAlertDialogType.matches("DELETE")){

                    if (value) {
                        iPosition = adapterPosition;

                        EmployeeMachineMapTable employeeMachineMapTable = AppDatabase.getInstance(context)
                                .employeeMachineMapTableDao()
                                .getEmployeeMachineTableByBothId(sEmployeeId, machineIdList.get(adapterPosition));

                        sEmployeeMachineMapId = employeeMachineMapTable.getId();

                        if (sEmployeeMachineMapId == null || sEmployeeMachineMapId.isEmpty()) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteEmployeeMachineRequest(sEmployeeMachineMapId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteEmployeeMachineRequest(String sEmployeeMachineMapId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }
        
        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sEmployeeMachineMapId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_EMPLOYEE_MACHINE_CONNECTION,
                Constants.BASE_SERVER_URL + "delete_employee_machine_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_EMPLOYEE_MACHINE_CONNECTION)) {
                    deleteEmployeeMachineResponse(response);
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

    private void deleteEmployeeMachineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).employeeMachineMapTableDao().deleteEmployeeMachineTableById(sEmployeeMachineMapId);
                machineIdList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Employee Machine Connection deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteEmployeeMachineResponse: ", e);
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
