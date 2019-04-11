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
import com.arvind.linebalancing.activity.connection.LineExecutiveMapActivity;
import com.arvind.linebalancing.activity.connection.LineSupervisorMapActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.LineSupervisorMapTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyAlertDialog;
import com.arvind.linebalancing.utilities.MyDeleteDialogInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LineSupervisorListAdapter extends RecyclerView.Adapter<LineSupervisorListAdapter.MyViewHolder> {

    private static final String TAG = LineSupervisorListAdapter.class.getSimpleName();
    private Context context;
    private List<String> employeeIdList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sLineId;
    private String sLineEmployeeMapId;

    public LineSupervisorListAdapter(Context context, String sLineId, List<String> employeeIdList) {

        this.context = context;
        this.sLineId = sLineId;
        this.employeeIdList = employeeIdList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_line_employee, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (employeeIdList.get(i) != null) {

            EmployeeTable employeeTable = AppDatabase.getInstance(context).employeeTableDao()
                    .getEmployeeById(employeeIdList.get(i));


            if (employeeTable != null) {
                myViewHolder.tvEmployeeName.setVisibility(View.VISIBLE);
                myViewHolder.tvEmployeeName.setText(employeeTable.getEmployeeName());

                DesignationTable designationTable = AppDatabase.getInstance(context).designationTableDao()
                        .getDesignationById(employeeTable.getDesignationId());

                if (designationTable != null) {
                    myViewHolder.tvEmployeeDesignation.setVisibility(View.VISIBLE);
                    myViewHolder.tvEmployeeDesignation.setText("(" + designationTable.getDesignationName() + ")");
                } else {
                    myViewHolder.tvEmployeeDesignation.setVisibility(View.GONE);
                }

            } else {
                myViewHolder.tvEmployeeName.setVisibility(View.GONE);
                myViewHolder.tvEmployeeDesignation.setVisibility(View.GONE);
            }
        }
//        if ((i + 1) == lineEmployeeMapTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return employeeIdList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvEmployeeName;
        private TextView tvEmployeeDesignation;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeDesignation = itemView.findViewById(R.id.tvEmployeeDesignation);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvEmployeeDesignation.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LineSupervisorMapTable lineEmployeeMapTable = AppDatabase.getInstance(context)
                            .lineSupervisorMapTableDao().getLineSupervisorTableByBothId(sLineId, employeeIdList.get(getAdapterPosition()));

                    Intent i = new Intent(context, LineSupervisorMapActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("LINE_ID", sLineId);
                    i.putExtra("LINE_EMPLOYEE_MAP_ID", lineEmployeeMapTable.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Line Employee Connection", true, "DELETE", getAdapterPosition());
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

                        LineSupervisorMapTable lineEmployeeMapTable = AppDatabase.getInstance(context)
                                .lineSupervisorMapTableDao()
                                .getLineSupervisorTableByBothId(sLineId, employeeIdList.get(adapterPosition));

                        sLineEmployeeMapId = lineEmployeeMapTable.getId();

                        if (sLineEmployeeMapId == null || sLineEmployeeMapId.isEmpty()) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteLineEmployeeRequest(sLineEmployeeMapId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteLineEmployeeRequest(String sLineEmployeeMapId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }

        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sLineEmployeeMapId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_LINE_EMPLOYEE_CONNECTION,
                Constants.BASE_SERVER_URL + "delete_line_supervisor_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_LINE_EMPLOYEE_CONNECTION)) {
                    deleteLineEmployeeResponse(response);
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

    private void deleteLineEmployeeResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).lineSupervisorMapTableDao().deleteLineSupervisorTableById(sLineEmployeeMapId);
                employeeIdList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Line Supervisor Connection deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteLineEmployeeResponse: ", e);
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
