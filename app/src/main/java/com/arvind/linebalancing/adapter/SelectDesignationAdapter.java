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
import com.arvind.linebalancing.activity.designation.SelectDesignationActivity;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectDesignationAdapter extends RecyclerView.Adapter<SelectDesignationAdapter.MyViewHolder> {

    private static final String TAG = SelectDesignationAdapter.class.getSimpleName();

    private Context context;
    private List<DesignationTable> designationTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectDesignationAdapter(Context context, List<DesignationTable> designationTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.designationTableList = designationTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[designationTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        if (designationTableList != null && selectedIdList != null) {

            for (int i = 0; i < selectedIdList.size(); i++) {

                String sId = selectedIdList.get(i);
                for (int j = 0; j < designationTableList.size(); j++) {
                    if (sId.equals(designationTableList.get(j).getId())) {
                        isSelected[j] = true;
                    }
                }

            }

        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_designation, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        DesignationTable designationTable = designationTableList.get(i);
        myViewHolder.tvDesignationName.setText(designationTable.getDesignationName());

        if (isSelected[i]) {
            myViewHolder.cbSelectDesignation.setChecked(true);
        } else {
            myViewHolder.cbSelectDesignation.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {

        return designationTableList.size();

    }

    public ArrayList<String> getSelectedDesignationIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDesignationName;
        private CheckBox cbSelectDesignation;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDesignationName = itemView.findViewById(R.id.tvDesignationName);
            cbSelectDesignation = itemView.findViewById(R.id.cbSelectDesignation);
            view = itemView.findViewById(R.id.view);

            tvDesignationName.setTypeface(utilityFile.getTfRegular());

            if (isMultipleSelectionAllowed) {
                cbSelectDesignation.setVisibility(View.VISIBLE);
            } else {
                cbSelectDesignation.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String sDesignationId = designationTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectDesignation.setChecked(false);
                        } else {
                            cbSelectDesignation.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sDesignationId);
                        ((SelectDesignationActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectDesignation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String sDesignationId = designationTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sDesignationId);
                            cbSelectDesignation.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sDesignationId);
                            cbSelectDesignation.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sDesignationId);
                        ((SelectDesignationActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
