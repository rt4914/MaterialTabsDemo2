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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.ViewSuggestionActivity;
import com.arvind.linebalancing.serverutilities.DecodeResponse;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.SuggestionTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.MyAlertDialog;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.MyViewHolder> {

    private static final String TAG = SuggestionListAdapter.class.getSimpleName();

    private Context context;
    private List<SuggestionTable> suggestionTableList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sSuggestionId;

    public SuggestionListAdapter(Context context, List<SuggestionTable> suggestionTableList) {

        this.context = context;
        this.suggestionTableList = suggestionTableList;

        utilityFile = new UtilityFile(context);

        volleySetup();
//        deleteDialogSetup();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (suggestionTableList.get(i) != null) {

            SuggestionTable suggestionTable = suggestionTableList.get(i);

//            OperationTable operationTable = AppDatabase.getInstance(context).operationTableDao()
//                    .getOperationById(suggestionTableList.get(i).getOperationId());
//
//            EmployeeTable absentEmployeeTable = AppDatabase.getInstance(context).employeeTableDao()
//                    .getEmployeeById(suggestionTableList.get(i).getAbsentEmployeeId());
//
//            EmployeeTable assignedEmployeeTable = AppDatabase.getInstance(context).employeeTableDao()
//                    .getEmployeeById(suggestionTableList.get(i).getAssignedEmployeeId());
//
//            LineTable lineTable = AppDatabase.getInstance(context).lineTableDao()
//                    .getLineById(suggestionTableList.get(i).getLineId());
//
//            MachineTable machineTable = AppDatabase.getInstance(context).machineTableDao()
//                    .getMachineById(suggestionTableList.get(i).getMachineId());

//            if (absentEmployeeTable != null) {
            myViewHolder.tvAbsentEmployeeName.setText(suggestionTable.getAbsentEmployeeId());

            myViewHolder.tvAssignedEmployeeName.setText(suggestionTable.getAssignedEmployeeId());

            myViewHolder.tvLineEfficiency.setText(String.valueOf(suggestionTableList.get(i).getLineEfficiency()));

            myViewHolder.tvOperation.setText(suggestionTable.getOperationId());

            myViewHolder.tvLine.setText(suggestionTable.getLineId());

            myViewHolder.tvMachine.setText(suggestionTable.getMachineId());

            myViewHolder.tvTime.setText(utilityFile.getStringFromLongTimestamp(suggestionTableList
                    .get(i).getTimestamp(), Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT));

        }
//        if ((i + 1) == employeeTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return suggestionTableList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSave;
        private LinearLayout llMain;
        private TextView tvOperation;
        private TextView tvOperationLabel;
        private TextView tvLine;
        private TextView tvLineLabel;
        private TextView tvMachine;
        private TextView tvMachineLabel;
        private TextView tvAssignedEmployeeNameLabel;
        private TextView tvAssignedEmployeeName;
        private TextView tvAbsentEmployeeNameLabel;
        private TextView tvAbsentEmployeeName;
        private TextView tvLineEfficiencyLabel;
        private TextView tvLineEfficiency;
        private TextView tvTime;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivSave = itemView.findViewById(R.id.ivSave);
            llMain = itemView.findViewById(R.id.llMain);
            tvOperation = itemView.findViewById(R.id.tvOperation);
            tvOperationLabel = itemView.findViewById(R.id.tvOperationLabel);
            tvLine = itemView.findViewById(R.id.tvLine);
            tvLineLabel = itemView.findViewById(R.id.tvLineLabel);
            tvMachine = itemView.findViewById(R.id.tvMachine);
            tvMachineLabel = itemView.findViewById(R.id.tvMachineLabel);
            tvAssignedEmployeeNameLabel = itemView.findViewById(R.id.tvAssignedEmployeeNameLabel);
            tvAssignedEmployeeName = itemView.findViewById(R.id.tvAssignedEmployeeName);
            tvAbsentEmployeeNameLabel = itemView.findViewById(R.id.tvAbsentEmployeeNameLabel);
            tvAbsentEmployeeName = itemView.findViewById(R.id.tvAbsentEmployeeName);
            tvLineEfficiency = itemView.findViewById(R.id.tvLineEfficiency);
            tvLineEfficiencyLabel = itemView.findViewById(R.id.tvLineEfficiencyLabel);
            tvTime = itemView.findViewById(R.id.tvTime);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvOperation.setTypeface(utilityFile.getTfRegular());
            tvOperationLabel.setTypeface(utilityFile.getTfMedium());
            tvLine.setTypeface(utilityFile.getTfRegular());
            tvLineLabel.setTypeface(utilityFile.getTfMedium());
            tvMachine.setTypeface(utilityFile.getTfRegular());
            tvMachineLabel.setTypeface(utilityFile.getTfMedium());
            tvAssignedEmployeeNameLabel.setTypeface(utilityFile.getTfRegular());
            tvAssignedEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvAbsentEmployeeNameLabel.setTypeface(utilityFile.getTfRegular());
            tvAbsentEmployeeName.setTypeface(utilityFile.getTfRegular());
            tvLineEfficiencyLabel.setTypeface(utilityFile.getTfRegular());
            tvLineEfficiency.setTypeface(utilityFile.getTfRegular());
            tvTime.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, ViewSuggestionActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    i.putExtra("CONNECTION_ID", suggestionTableList.get(getAdapterPosition()).getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);

                }
            });

            ivSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iPosition = getAdapterPosition();
                    sSuggestionId = suggestionTableList.get(getAdapterPosition()).getId();

                    sendSaveSuggestionRequest();

                }
            });
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//
//                    myAlertDialog.createDeleteAlertBox("Delete Suggestion", true, "DELETE", getAdapterPosition());
//                    return true;
//
//                }
//            });

        }

    }

    private void sendSaveSuggestionRequest() {

        Log.d(TAG, "sendSaveSuggestionRequest: " + sSuggestionId);

        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", sSuggestionId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_SAVE_SUGGESTION,
                Constants.BASE_SERVER_URL + "save_suggestion",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_SAVE_SUGGESTION)) {
                    sendSaveSuggestionResponse(response);
                } else {
                    showToast(context.getResources().getString(R.string.unexpected_error));
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                hideProgressDialog();
                showToast(context.getResources().getString(R.string.unexpected_error));

            }
        };

        mGeneralVolleyRequest = new GeneralVolleyRequest(context, mRequestCallback);

    }

    private void sendSaveSuggestionResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                DecodeResponse decodeResponse = new DecodeResponse(context);

                JSONObject connectionJSONObject = responseObject.optJSONObject("message");
                decodeResponse.decodeConnectionObject(connectionJSONObject);

                AppDatabase.getInstance(context).suggestionTableDao().deleteSuggestionById(sSuggestionId);
                suggestionTableList.remove(iPosition);
                notifyItemRemoved(iPosition);

                showToast("Suggestion saved successfully. Check connections for assigned employee.");

            } else {
                showToast(context.getResources().getString(R.string.unknown_response_code) + " " + iResponseCode);
            }

        } catch (JSONException e) {
            showToast(context.getResources().getString(R.string.invalid_json));
        }

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, "" + sDisplayMessage, Toast.LENGTH_LONG);
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
