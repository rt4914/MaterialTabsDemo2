package com.advanced.education.admin.wisel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.activity.SelectStandardActivity;
import com.advanced.education.admin.wisel.table.StandardTable;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectStandardAdapter extends RecyclerView.Adapter<SelectStandardAdapter.MyViewHolder> {

    private static final String TAG = SelectStandardAdapter.class.getSimpleName();

    private Context context;
    private List<StandardTable> standardTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectStandardAdapter(Context context, List<StandardTable> standardTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.standardTableList = standardTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[standardTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        for (int i = 0; i < selectedIdList.size(); i++) {
            String sId = selectedIdList.get(i);
            int index = Integer.parseInt(sId);
            isSelected[index] = true;
        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_standard, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        //TODO: Get standard id
        String sStandardId = "";
        //TODO: Remove this line
        sStandardId = "" + i;

        if (isSelected[i]) {
            myViewHolder.cbSelectStandard.setChecked(true);
        } else {
            myViewHolder.cbSelectStandard.setChecked(false);
        }

        if ((i + 1) == standardTableList.size()) {
            myViewHolder.view.setVisibility(View.GONE);
        } else {
            myViewHolder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {

        return standardTableList.size();

    }

    public ArrayList<String> getSelectedStandardIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStandardName;
        private CheckBox cbSelectStandard;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStandardName = itemView.findViewById(R.id.tvStandardName);
            cbSelectStandard = itemView.findViewById(R.id.cbSelectStandard);
            view = itemView.findViewById(R.id.view);

            tvStandardName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Get standard id
                    String sStandardId = "";
                    //TODO: Remove this line
                    sStandardId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectStandard.setChecked(false);
                        } else {
                            cbSelectStandard.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sStandardId);
                        ((SelectStandardActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectStandard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //TODO: Get standard id
                    String sStandardId = "";
                    //TODO: Remove this line
                    sStandardId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sStandardId);
                            cbSelectStandard.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sStandardId);
                            cbSelectStandard.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sStandardId);
                        ((SelectStandardActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
