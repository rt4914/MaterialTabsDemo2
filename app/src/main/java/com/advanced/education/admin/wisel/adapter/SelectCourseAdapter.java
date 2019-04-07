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
import com.advanced.education.admin.wisel.activity.SelectCourseActivity;
import com.advanced.education.admin.wisel.table.CourseTable;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectCourseAdapter extends RecyclerView.Adapter<SelectCourseAdapter.MyViewHolder> {

    private static final String TAG = SelectCourseAdapter.class.getSimpleName();

    private Context context;
    private List<CourseTable> courseTableList;
    private boolean isMultipleSelectionAllowed;

    private UtilityFile utilityFile;

    private boolean[] isSelected;
    private HashSet<String> selectedIdSet;

    public SelectCourseAdapter(Context context, List<CourseTable> courseTableList, ArrayList<String> selectedIdList, boolean isMultipleSelectionAllowed) {

        this.context = context;
        this.courseTableList = courseTableList;
        this.isMultipleSelectionAllowed = isMultipleSelectionAllowed;

        utilityFile = new UtilityFile(context);

        selectedIdSet = new HashSet<>();
        selectedIdSet.addAll(selectedIdList);

        isSelected = new boolean[courseTableList.size()];

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

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_course, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        //TODO: Get course id
        String sCourseId = "";
        //TODO: Remove this line
        sCourseId = "" + i;

        if (isSelected[i]) {
            myViewHolder.cbSelectCourse.setChecked(true);
        } else {
            myViewHolder.cbSelectCourse.setChecked(false);
        }

        if ((i + 1) == courseTableList.size()) {
            myViewHolder.view.setVisibility(View.GONE);
        } else {
            myViewHolder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {

        return courseTableList.size();

    }

    public ArrayList<String> getSelectedCourseIdList() {

        return new ArrayList<>(selectedIdSet);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCourseName;
        private CheckBox cbSelectCourse;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            cbSelectCourse = itemView.findViewById(R.id.cbSelectCourse);
            view = itemView.findViewById(R.id.view);

            tvCourseName.setTypeface(utilityFile.getTfRegular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Get course id
                    String sCourseId = "";
                    //TODO: Remove this line
                    sCourseId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (isSelected[getAdapterPosition()]) {
                            cbSelectCourse.setChecked(false);
                        } else {
                            cbSelectCourse.setChecked(true);
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sCourseId);
                        ((SelectCourseActivity) context).goToPreviousActivity();
                    }

                }
            });

            cbSelectCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //TODO: Get course id
                    String sCourseId = "";
                    //TODO: Remove this line
                    sCourseId = "" + getAdapterPosition();

                    if (isMultipleSelectionAllowed) {
                        if (!b) {
                            selectedIdSet.remove(sCourseId);
                            cbSelectCourse.setChecked(false);
                            isSelected[getAdapterPosition()] = false;
                        } else {
                            selectedIdSet.add(sCourseId);
                            cbSelectCourse.setChecked(true);
                            isSelected[getAdapterPosition()] = true;
                        }
                    } else {
                        selectedIdSet.clear();
                        selectedIdSet.add(sCourseId);
                        ((SelectCourseActivity) context).goToPreviousActivity();
                    }

                }
            });

        }

    }

}
