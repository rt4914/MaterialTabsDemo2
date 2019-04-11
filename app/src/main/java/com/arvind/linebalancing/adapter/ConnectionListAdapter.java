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
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.ConnectionTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.table.MachineTable;
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

public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.MyViewHolder> {

    private static final String TAG = ConnectionListAdapter.class.getSimpleName();
    
    private Context context;
    private List<ConnectionTable> connectionTableList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sConnectionId;

    public ConnectionListAdapter(Context context, List<ConnectionTable> connectionTableList) {

        this.context = context;
        this.connectionTableList = connectionTableList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_connection, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (connectionTableList.get(i) != null) {

            OperationTable operationTable = AppDatabase.getInstance(context).operationTableDao()
                    .getOperationById(connectionTableList.get(i).getOperationId());

            EmployeeTable employeeTable = AppDatabase.getInstance(context).employeeTableDao()
                    .getEmployeeById(connectionTableList.get(i).getEmployeeId());

            LineTable lineTable = AppDatabase.getInstance(context).lineTableDao()
                    .getLineById(connectionTableList.get(i).getLineId());

            MachineTable machineTable = AppDatabase.getInstance(context).machineTableDao()
                    .getMachineById(connectionTableList.get(i).getMachineId());

            if (operationTable != null && employeeTable != null) {
                myViewHolder.tvEmployeeName.setText(employeeTable.getEmployeeName());
                myViewHolder.tvOperation.setText(operationTable.getOperationName());
                myViewHolder.tvLine.setText(lineTable.getLineName());
                myViewHolder.tvMachine.setText(machineTable.getMachineName());
                myViewHolder.tvTime.setText(utilityFile.getStringFromLongTimestamp(connectionTableList
                        .get(i).getTimestamp(), Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));
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

        return connectionTableList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvOperation;
        private TextView tvOperationLabel;
        private TextView tvLine;
        private TextView tvLineLabel;
        private TextView tvMachine;
        private TextView tvMachineLabel;
        private TextView tvEmployeeName;
        private TextView tvTime;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvOperation = itemView.findViewById(R.id.tvOperation);
            tvOperationLabel = itemView.findViewById(R.id.tvOperationLabel);
            tvLine = itemView.findViewById(R.id.tvLine);
            tvLineLabel = itemView.findViewById(R.id.tvLineLabel);
            tvMachine = itemView.findViewById(R.id.tvMachine);
            tvMachineLabel = itemView.findViewById(R.id.tvMachineLabel);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvTime = itemView.findViewById(R.id.tvTime);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvOperation.setTypeface(utilityFile.getTfRegular());
            tvOperationLabel.setTypeface(utilityFile.getTfMedium());
            tvLine.setTypeface(utilityFile.getTfRegular());
            tvLineLabel.setTypeface(utilityFile.getTfMedium());
            tvMachine.setTypeface(utilityFile.getTfRegular());
            tvMachineLabel.setTypeface(utilityFile.getTfMedium());
            tvEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvTime.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, AddConnectionActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("CONNECTION_ID", connectionTableList.get(getAdapterPosition()).getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Connection", true, "DELETE", getAdapterPosition());
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
                        sConnectionId = connectionTableList.get(adapterPosition).getId();

                        Log.d(TAG, "onClick: " + sConnectionId);

                        if (sConnectionId == null) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteConnectionRequest(sConnectionId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteConnectionRequest(String sConnectionId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }


        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sConnectionId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_CONNECTION,
                Constants.BASE_SERVER_URL + "delete_connection",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_CONNECTION)) {
                    deleteConnectionResponse(response);
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

    private void deleteConnectionResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).connectionTableDao().deleteConnectionById(sConnectionId);
                connectionTableList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Connection deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteConnectionResponse: ", e);
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
