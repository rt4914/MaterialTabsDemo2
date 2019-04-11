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
import com.arvind.linebalancing.activity.connection.EmployeeOperationMapActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeOperationMapTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyAlertDialog;
import com.arvind.linebalancing.utilities.MyDeleteDialogInterface;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class EmployeeOperationListAdapter extends RecyclerView.Adapter<EmployeeOperationListAdapter.MyViewHolder> {

    private static final String TAG = EmployeeOperationListAdapter.class.getSimpleName();
    private Context context;
    private List<String> operationIdList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sEmployeeId;
    private String sEmployeeOperationMapId;

    public EmployeeOperationListAdapter(Context context, String sEmployeeId, List<String> operationIdList) {

        this.context = context;
        this.sEmployeeId = sEmployeeId;
        this.operationIdList = operationIdList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_employee_operation, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (operationIdList.get(i) != null) {

            OperationTable operationTable = AppDatabase.getInstance(context).operationTableDao()
                    .getOperationById(operationIdList.get(i));

            if (operationTable != null) {
                myViewHolder.tvOperationName.setVisibility(View.VISIBLE);
                myViewHolder.tvOperationName.setText(operationTable.getOperationName());
            } else {
                myViewHolder.tvOperationName.setVisibility(View.GONE);
            }
        }
//        if ((i + 1) == employeeOperationMapTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return operationIdList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvOperationName;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvOperationName = itemView.findViewById(R.id.tvOperationName);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvOperationName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EmployeeOperationMapTable employeeOperationMapTable = AppDatabase.getInstance(context)
                            .employeeOperationMapTableDao().getEmployeeOperationTableByBothId(sEmployeeId, operationIdList.get(getAdapterPosition()));

                    Intent i = new Intent(context, EmployeeOperationMapActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("EMPLOYEE_ID", sEmployeeId);
                    i.putExtra("EMPLOYEE_OPERATION_MAP_ID", employeeOperationMapTable.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Employee Operation Connection", true, "DELETE", getAdapterPosition());
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

                        EmployeeOperationMapTable employeeOperationMapTable = AppDatabase.getInstance(context)
                                .employeeOperationMapTableDao()
                                .getEmployeeOperationTableByBothId(sEmployeeId, operationIdList.get(adapterPosition));

                        sEmployeeOperationMapId = employeeOperationMapTable.getId();

                        if (sEmployeeOperationMapId == null || sEmployeeOperationMapId.isEmpty()) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteEmployeeOperationRequest(sEmployeeOperationMapId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteEmployeeOperationRequest(String sEmployeeOperationMapId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }

        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sEmployeeOperationMapId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_EMPLOYEE_OPERATION_CONNECTION,
                Constants.BASE_SERVER_URL + "delete_employee_operation_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_EMPLOYEE_OPERATION_CONNECTION)) {
                    deleteEmployeeOperationResponse(response);
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

    private void deleteEmployeeOperationResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).employeeOperationMapTableDao().deleteEmployeeOperationTableById(sEmployeeOperationMapId);
                operationIdList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Employee Operation Connection deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteEmployeeOperationResponse: ", e);
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
