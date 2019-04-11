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
import com.arvind.linebalancing.activity.line.SelectLineActivity;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectLineAdapter extends RecyclerView.Adapter<SelectLineAdapter.MyViewHolder> {

    private static final String TAG = SelectLineAdapter.class.getSimpleName();

    private Context context;
    private List<LineTable> lineTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectLineAdapter(Context context, List<LineTable> lineTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.lineTableList = lineTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[lineTableList.size()];

        for (int i = 0; i < selectedIdList.size(); i++) {
            isSelected[i] = false;
        }

        if (lineTableList != null && selectedIdList != null) {

            for (int i = 0; i < selectedIdList.size(); i++) {

                String sId = selectedIdList.get(i);
                for (int j = 0; j < lineTableList.size(); j++) {
                    if (sId.equals(lineTableList.get(j).getId())) {
                        isSelected[j] = true;
                    }
                }

            }

        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_line, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        LineTable lineTable = lineTableList.get(i);
        myViewHolder.tvLineName.setText(lineTable.getLineName());
        myViewHolder.tvLineNo.setText(lineTable.getLineNo());

        if (isSelected[i]) {
            myViewHolder.cbSelectLine.setChecked(true);
        } else {
            myViewHolder.cbSelectLine.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {

        return lineTableList.size();

    }

    public ArrayList<String> getSelectedLineIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLineName;
        private TextView tvLineNo;
        private CheckBox cbSelectLine;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLineName = itemView.findViewById(R.id.tvLineName);
            tvLineNo = itemView.findViewById(R.id.tvLineNo);
            cbSelectLine = itemView.findViewById(R.id.cbSelectLine);
            view = itemView.findViewById(R.id.view);

            tvLineNo.setTypeface(utilityFile.getTfRegular());

            if (isMultipleSelectionAllowed) {
                cbSelectLine.setVisibility(View.VISIBLE);
            } else {
                cbSelectLine.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String sLineId = lineTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectLine.setChecked(false);
                        } else {
                            cbSelectLine.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sLineId);
                        ((SelectLineActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String sLineId = lineTableList.get(getAdapterPosition()).getId();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sLineId);
                            cbSelectLine.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sLineId);
                            cbSelectLine.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sLineId);
                        ((SelectLineActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
