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
import com.arvind.linebalancing.activity.employee.AddEmployeeRatingActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.EmployeeRatingTable;
import com.arvind.linebalancing.table.EmployeeTable;
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

public class EmployeeRatingListAdapter extends RecyclerView.Adapter<EmployeeRatingListAdapter.MyViewHolder> {

    private static final String TAG = EmployeeRatingListAdapter.class.getSimpleName();
    
    private Context context;
    private List<EmployeeRatingTable> employeeRatingTableList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sEmployeeRatingId;

    public EmployeeRatingListAdapter(Context context, List<EmployeeRatingTable> employeeRatingTableList) {

        this.context = context;
        this.employeeRatingTableList = employeeRatingTableList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_employee_rating, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (employeeRatingTableList.get(i) != null) {

            OperationTable operationTable = AppDatabase.getInstance(context).operationTableDao()
                    .getOperationById(employeeRatingTableList.get(i).getOperationId());

            EmployeeTable employeeTable = AppDatabase.getInstance(context).employeeTableDao()
                    .getEmployeeById(employeeRatingTableList.get(i).getEmployeeId());

            if (operationTable != null && employeeTable != null) {
                myViewHolder.tvEmployeeName.setText(employeeTable.getEmployeeName());
                myViewHolder.tvEmployeeOperation.setText("in " + operationTable.getOperationName());
                myViewHolder.tvSAMValue.setText("Rating: " + employeeRatingTableList.get(i).getSamValue() + "");
                myViewHolder.tvRatingTime.setText(utilityFile.getStringFromLongTimestamp(employeeRatingTableList
                        .get(i).getRatingTimestamp(), Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));
            }
        }
//        if ((i + 1) == employeeTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return employeeRatingTableList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvEmployeeOperation;
        private TextView tvEmployeeName;
        private TextView tvSAMValue;
        private TextView tvRatingTime;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvEmployeeOperation = itemView.findViewById(R.id.tvEmployeeOperation);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvSAMValue = itemView.findViewById(R.id.tvSAMValue);
            tvRatingTime = itemView.findViewById(R.id.tvRatingTime);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvEmployeeOperation.setTypeface(utilityFile.getTfRegular());
            tvEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvSAMValue.setTypeface(utilityFile.getTfRegular());
            tvRatingTime.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, AddEmployeeRatingActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("EMPLOYEE_RATING_ID", employeeRatingTableList.get(getAdapterPosition()).getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Employee Rating", true, "DELETE", getAdapterPosition());
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
                        sEmployeeRatingId = employeeRatingTableList.get(adapterPosition).getId();

                        Log.d(TAG, "onClick: " + sEmployeeRatingId);

                        if (sEmployeeRatingId == null) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteEmployeeRatingRequest(sEmployeeRatingId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteEmployeeRatingRequest(String sEmployeeRatingId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }


        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sEmployeeRatingId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_EMPLOYEE_RATING,
                Constants.BASE_SERVER_URL + "delete_employee_rating",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_EMPLOYEE_RATING)) {
                    deleteEmployeeRatingResponse(response);
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

    private void deleteEmployeeRatingResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).employeeRatingTableDao().deleteEmployeeRatingById(sEmployeeRatingId);
                employeeRatingTableList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Employee Rating deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteEmployeeRatingResponse: ", e);
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
