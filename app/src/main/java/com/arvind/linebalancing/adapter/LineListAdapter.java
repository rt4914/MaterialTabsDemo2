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
import com.arvind.linebalancing.activity.line.AddLineActivity;
import com.arvind.linebalancing.activity.line.AddLineEfficiencyActivity;
import com.arvind.linebalancing.serverutilities.GeneralVolleyRequest;
import com.arvind.linebalancing.serverutilities.IVolleyResponse;
import com.arvind.linebalancing.table.LineEfficiencyTable;
import com.arvind.linebalancing.table.LineTable;
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

public class LineListAdapter extends RecyclerView.Adapter<LineListAdapter.MyViewHolder> {

    private static final String TAG = LineListAdapter.class.getSimpleName();
    private Context context;
    private List<LineTable> lineTableList;

    private ProgressDialog pdLoading = null;
    private Toast mToast = null;

    private GeneralVolleyRequest mGeneralVolleyRequest;
    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    private int iPosition;
    private String sLineId;

    public LineListAdapter(Context context, List<LineTable> lineTableList) {

        this.context = context;
        this.lineTableList = lineTableList;

        utilityFile = new UtilityFile(context);

        volleySetup();
        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_line, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

        if (lineTableList.get(i) != null) {
            myViewHolder.tvLineName.setText(lineTableList.get(i).getLineName());
            myViewHolder.tvLineNo.setText("Line No.: " + lineTableList.get(i).getLineNo());
        }
//        if ((i + 1) == lineTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return lineTableList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvLineName;
        private TextView tvLineNo;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvLineName = itemView.findViewById(R.id.tvLineName);
            tvLineNo = itemView.findViewById(R.id.tvLineNo);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvLineName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<String> optionArrayList = new ArrayList<>();

                    optionArrayList.add("View Line");

                    LineEfficiencyTable lineEfficiencyTable = AppDatabase.getInstance(context).lineEfficiencyTableDao()
                            .getLineEfficiencyTableById(lineTableList.get(getAdapterPosition()).getId());

                    if (lineEfficiencyTable != null) {
                        optionArrayList.add("Add Line Efficiency");
                    }
                    alertDialogSetup(getAdapterPosition());
                    myAlertDialog.createAlertBox("Select Option", true, optionArrayList, "OPTIONS");

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Line", true, "DELETE", getAdapterPosition());
                    return true;

                }
            });

        }

    }

    private void alertDialogSetup(final int adapterPosition) {

        final String sLineId = lineTableList.get(adapterPosition).getId();
        Log.d(TAG, "alertDialogSetup: " + sLineId);
        final String sLineName = lineTableList.get(adapterPosition).getLineName();

        MyAlertDialogInterface myAlertDialogInterface = new MyAlertDialogInterface() {
            @Override
            public void getSelectedItemIndex(int index, String sAlertDialogType) {
                if (sAlertDialogType.matches("OPTIONS")) {

                    switch (index) {
                        case 0:
                            Intent i = new Intent(context, AddLineActivity.class);
                            i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                            i.putExtra("LINE_ID", lineTableList.get(adapterPosition).getId());
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(i);
                            break;

                        case 1:
                            Intent intent = new Intent(context, AddLineEfficiencyActivity.class);
                            intent.putExtra("MODE", Constants.ACTIVITY_MODE_ADD);
                            intent.putExtra("LINE_ID", sLineId);
                            intent.putExtra("LINE_NAME", sLineName);
                            context.startActivity(intent);
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

                if (sAlertDialogType.matches("DELETE")) {

                    if (value) {
                        iPosition = adapterPosition;
                        sLineId = lineTableList.get(adapterPosition).getId();

                        Log.d(TAG, "onClick: " + sLineId);

                        if (sLineId == null) {
                            showToast(context.getResources().getString(R.string.unexpected_error));
                            return;
                        }

                        sendDeleteLineRequest(sLineId);

                    }

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

    private void sendDeleteLineRequest(String sLineId) {

        if (!utilityFile.isConnectingToInternet()) {
            showToast(context.getResources().getString(R.string.no_internet_connection));
            return;
        }


        showProgressDialog();

        HashMap<String, String> params = new HashMap<>();

        params.put("id", sLineId);

        mGeneralVolleyRequest.requestToServer(Request.Method.POST, Constants.API_DELETE_LINE,
                Constants.BASE_SERVER_URL + "delete_line",
                params);

    }

    private void volleySetup() {

        IVolleyResponse mRequestCallback = new IVolleyResponse() {
            @Override
            public void notifySuccess(String requestType, String response) {

                hideProgressDialog();

                Log.d(TAG, "notifySuccess: " + response);

                if (requestType.matches(Constants.API_DELETE_LINE)) {
                    deleteLineResponse(response);
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

    private void deleteLineResponse(String sResponse) {

        try {
            JSONObject responseObject = new JSONObject(sResponse);

            int iResponseCode = responseObject.getInt("response_code");

            if (iResponseCode == 100) {

                AppDatabase.getInstance(context).lineTableDao().deleteLineById(sLineId);
                lineTableList.remove(iPosition);
                notifyItemRemoved(iPosition);
                showToast("Line deleted successfully.");

            } else {
                showToast(context.getResources().getString(R.string.unexpected_error));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteLineResponse: ", e);
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
