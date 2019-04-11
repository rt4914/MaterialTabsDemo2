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
import com.arvind.linebalancing.activity.machine.SelectMachineActivity;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectMachineAdapter extends RecyclerView.Adapter<SelectMachineAdapter.MyViewHolder> {

    private static final String TAG = SelectMachineAdapter.class.getSimpleName();

    private Context context;
    private List<MachineTable> machineTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectMachineAdapter(Context context, List<MachineTable> machineTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.machineTableList = machineTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[machineTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        if (machineTableList != null && selectedIdList != null) {

            for (int i = 0; i < selectedIdList.size(); i++) {

                String sId = selectedIdList.get(i);
                for (int j = 0; j < machineTableList.size(); j++) {
                    if (sId.equals(machineTableList.get(j).getId())) {
                        isSelected[j] = true;
                    }
                }

            }

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_machine, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        MachineTable machineTable = machineTableList.get(i);
        myViewHolder.tvMachineName.setText(machineTable.getMachineName());
        myViewHolder.tvAssignedName.setText(machineTable.getAssignedName());

        if (isSelected[i]) {
            myViewHolder.cbSelectMachine.setChecked(true);
        } else {
            myViewHolder.cbSelectMachine.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {

        return machineTableList.size();

    }

    public ArrayList<String> getSelectedMachineIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMachineName;
        private TextView tvAssignedName;
        private CheckBox cbSelectMachine;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMachineName = itemView.findViewById(R.id.tvMachineName);
            tvAssignedName = itemView.findViewById(R.id.tvAssignedName);
            cbSelectMachine = itemView.findViewById(R.id.cbSelectMachine);
            view = itemView.findViewById(R.id.view);

            tvMachineName.setTypeface(utilityFile.getTfRegular());
            tvAssignedName.setTypeface(utilityFile.getTfRegular());

            if (isMultipleSelectionAllowed) {
                cbSelectMachine.setVisibility(View.VISIBLE);
            } else {
                cbSelectMachine.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String sMachineId = machineTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectMachine.setChecked(false);
                        } else {
                            cbSelectMachine.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sMachineId);
                        ((SelectMachineActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectMachine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String sMachineId = machineTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sMachineId);
                            cbSelectMachine.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sMachineId);
                            cbSelectMachine.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sMachineId);
                        ((SelectMachineActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
