package com.arvind.linebalancing.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arvind.linebalancing.R;
import com.arvind.linebalancing.activity.operation.SelectOperationActivity;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectOperationAdapter extends RecyclerView.Adapter<SelectOperationAdapter.MyViewHolder> {

    private static final String TAG = SelectOperationAdapter.class.getSimpleName();

    private Context context;
    private List<OperationTable> operationTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectOperationAdapter(Context context, List<OperationTable> operationTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.operationTableList = operationTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[operationTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        if (operationTableList != null && selectedIdList != null) {

            for (int i = 0; i < selectedIdList.size(); i++) {

                String sId = selectedIdList.get(i);
                for (int j = 0; j < operationTableList.size(); j++) {
                    if (sId.equals(operationTableList.get(j).getId())) {
                        isSelected[j] = true;
                    }
                }

            }

        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_operation, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        OperationTable operationTable = operationTableList.get(i);
        myViewHolder.tvOperationName.setText(operationTable.getOperationName());

        if (isSelected[i]) {
            myViewHolder.cbSelectOperation.setChecked(true);
        } else {
            myViewHolder.cbSelectOperation.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {

        return operationTableList.size();

    }

    public ArrayList<String> getSelectedOperationIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOperationName;
        private CheckBox cbSelectOperation;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOperationName = itemView.findViewById(R.id.tvOperationName);
            cbSelectOperation = itemView.findViewById(R.id.cbSelectOperation);
            view = itemView.findViewById(R.id.view);

            tvOperationName.setTypeface(utilityFile.getTfRegular());

            if (isMultipleSelectionAllowed) {
                cbSelectOperation.setVisibility(View.VISIBLE);
            } else {
                cbSelectOperation.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String sOperationId = operationTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectOperation.setChecked(false);
                        } else {
                            cbSelectOperation.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sOperationId);
                        ((SelectOperationActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectOperation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String sOperationId = operationTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sOperationId);
                            cbSelectOperation.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sOperationId);
                            cbSelectOperation.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sOperationId);
                        ((SelectOperationActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
