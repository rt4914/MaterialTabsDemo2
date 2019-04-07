package com.advanced.education.admin.wisel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.activity.AddCourseActivity;
import com.advanced.education.admin.wisel.activity.AddStudentActivity;
import com.advanced.education.admin.wisel.activity.MainActivity;
import com.advanced.education.admin.wisel.table.CourseTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.MyAlertDialog;
import com.advanced.education.admin.wisel.utilities.MyDeleteDialogInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.MyViewHolder> {

    private Context context;
    private List<CourseTable> courseTableList;

    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    public CourseListAdapter(Context context, List<CourseTable> courseTableList) {

        this.context = context;
        this.courseTableList = courseTableList;

        utilityFile = new UtilityFile(context);

        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_course_main, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

//        if ((i + 1) == courseTableList.size()) {
//            myViewHolder.vDivider.setVisibility(View.GONE);
//        } else {
//            myViewHolder.vDivider.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public int getItemCount() {

        return 10;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMain;
        private TextView tvCourseName;
        private TextView tvStandardName;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvStandardName = itemView.findViewById(R.id.tvStandardName);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvCourseName.setTypeface(utilityFile.getTfRegular());
            tvStandardName.setTypeface(utilityFile.getTfMedium());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, AddCourseActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete course", true, "DELETE");
                    return true;

                }
            });

        }

    }

    public void deleteDialogSetup() {

        MyDeleteDialogInterface myDeleteDialogInterface = new MyDeleteDialogInterface() {
            @Override
            public void isDeleted(boolean value, String sAlertDialogType) {

                if(sAlertDialogType.matches("DELETE")){

                }

            }
        };

        myAlertDialog = new MyAlertDialog(context, myDeleteDialogInterface);

    }

}
