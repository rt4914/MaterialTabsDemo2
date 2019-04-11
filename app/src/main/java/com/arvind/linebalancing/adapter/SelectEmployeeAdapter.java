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
import com.arvind.linebalancing.activity.employee.SelectEmployeeActivity;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectEmployeeAdapter extends RecyclerView.Adapter<SelectEmployeeAdapter.MyViewHolder> {

    private static final String TAG = SelectEmployeeAdapter.class.getSimpleName();

    private Context context;
    private List<EmployeeTable> employeeTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectEmployeeAdapter(Context context, List<EmployeeTable> employeeTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.employeeTableList = employeeTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[employeeTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        if (employeeTableList != null && selectedIdList != null) {

            for (int i = 0; i < selectedIdList.size(); i++) {

                String sId = selectedIdList.get(i);
                for (int j = 0; j < employeeTableList.size(); j++) {
                    if (sId.equals(employeeTableList.get(j).getId())) {
                        isSelected[j] = true;
                    }
                }

            }

        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_employee, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        EmployeeTable employeeTable = employeeTableList.get(i);
        myViewHolder.tvEmployeeName.setText(employeeTable.getEmployeeName());

        if (isSelected[i]) {
            myViewHolder.cbSelectEmployee.setChecked(true);
        } else {
            myViewHolder.cbSelectEmployee.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {

        return employeeTableList.size();

    }

    public ArrayList<String> getSelectedEmployeeIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEmployeeName;
        private CheckBox cbSelectEmployee;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            cbSelectEmployee = itemView.findViewById(R.id.cbSelectEmployee);
            view = itemView.findViewById(R.id.view);

            tvEmployeeName.setTypeface(utilityFile.getTfRegular());

            if (isMultipleSelectionAllowed) {
                cbSelectEmployee.setVisibility(View.VISIBLE);
            } else {
                cbSelectEmployee.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String sEmployeeId = employeeTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectEmployee.setChecked(false);
                        } else {
                            cbSelectEmployee.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sEmployeeId);
                        ((SelectEmployeeActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectEmployee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String sEmployeeId = employeeTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sEmployeeId);
                            cbSelectEmployee.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sEmployeeId);
                            cbSelectEmployee.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sEmployeeId);
                        ((SelectEmployeeActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
