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
import com.advanced.education.admin.wisel.activity.SelectTeacherActivity;
import com.advanced.education.admin.wisel.table.TeacherTable;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectTeacherAdapter extends RecyclerView.Adapter<SelectTeacherAdapter.MyViewHolder> {

    private static final String TAG = SelectTeacherAdapter.class.getSimpleName();

    private Context context;
    private List<TeacherTable> teacherTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectTeacherAdapter(Context context, List<TeacherTable> teacherTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.teacherTableList = teacherTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[teacherTableList.size()];

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

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_teacher, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        //TODO: Get teacher id
        String sTeacherId = "";
        //TODO: Remove this line
        sTeacherId = "" + i;

        if (isSelected[i]) {
            myViewHolder.cbSelectTeacher.setChecked(true);
        } else {
            myViewHolder.cbSelectTeacher.setChecked(false);
        }

        if ((i + 1) == teacherTableList.size()) {
            myViewHolder.view.setVisibility(View.GONE);
        } else {
            myViewHolder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {

        return teacherTableList.size();

    }

    public ArrayList<String> getSelectedTeacherIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTeacherName;
        private CheckBox cbSelectTeacher;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            cbSelectTeacher = itemView.findViewById(R.id.cbSelectTeacher);
            view = itemView.findViewById(R.id.view);

            tvTeacherName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Get teacher id
                    String sTeacherId = "";
                    //TODO: Remove this line
                    sTeacherId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectTeacher.setChecked(false);
                        } else {
                            cbSelectTeacher.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sTeacherId);
                        ((SelectTeacherActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //TODO: Get teacher id
                    String sTeacherId = "";
                    //TODO: Remove this line
                    sTeacherId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sTeacherId);
                            cbSelectTeacher.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sTeacherId);
                            cbSelectTeacher.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sTeacherId);
                        ((SelectTeacherActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
