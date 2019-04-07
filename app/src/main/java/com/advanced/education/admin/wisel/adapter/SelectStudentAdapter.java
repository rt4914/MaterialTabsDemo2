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
import com.advanced.education.admin.wisel.activity.SelectStudentActivity;
import com.advanced.education.admin.wisel.table.StudentTable;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectStudentAdapter extends RecyclerView.Adapter<SelectStudentAdapter.MyViewHolder> {

    private static final String TAG = SelectStudentAdapter.class.getSimpleName();

    private Context context;
    private List<StudentTable> studentTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectStudentAdapter(Context context, List<StudentTable> studentTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.studentTableList = studentTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[studentTableList.size()];

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

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_student, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        //TODO: Get student id
        String sStudentId = "";
        //TODO: Remove this line
        sStudentId = "" + i;

        if (isSelected[i]) {
            myViewHolder.cbSelectStudent.setChecked(true);
        } else {
            myViewHolder.cbSelectStudent.setChecked(false);
        }

        if ((i + 1) == studentTableList.size()) {
            myViewHolder.view.setVisibility(View.GONE);
        } else {
            myViewHolder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {

        return studentTableList.size();

    }

    public ArrayList<String> getSelectedStudentIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStudentName;
        private CheckBox cbSelectStudent;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            cbSelectStudent = itemView.findViewById(R.id.cbSelectStudent);
            view = itemView.findViewById(R.id.view);

            tvStudentName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Get student id
                    String sStudentId = "";
                    //TODO: Remove this line
                    sStudentId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectStudent.setChecked(false);
                        } else {
                            cbSelectStudent.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sStudentId);
                        ((SelectStudentActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //TODO: Get student id
                    String sStudentId = "";
                    //TODO: Remove this line
                    sStudentId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sStudentId);
                            cbSelectStudent.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sStudentId);
                            cbSelectStudent.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sStudentId);
                        ((SelectStudentActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
