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
import com.advanced.education.admin.wisel.activity.AddStandardActivity;
import com.advanced.education.admin.wisel.activity.AddStudentActivity;
import com.advanced.education.admin.wisel.table.StudentTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.MyAlertDialog;
import com.advanced.education.admin.wisel.utilities.MyDeleteDialogInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {

    private Context context;
    private List<StudentTable> studentTableList;

    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    public StudentListAdapter(Context context, List<StudentTable> studentTableList) {

        this.context = context;
        this.studentTableList = studentTableList;

        utilityFile = new UtilityFile(context);

        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_student_main, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

//        if ((i + 1) == studentTableList.size()) {
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
        private TextView tvStudentName;
        private TextView tvStandardName;
        private View vDivider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStandardName = itemView.findViewById(R.id.tvStandardName);
            vDivider = itemView.findViewById(R.id.vDivider);

            tvStudentName.setTypeface(utilityFile.getTfRegular());
            tvStandardName.setTypeface(utilityFile.getTfMedium());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, AddStudentActivity.class);
                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete student", true, "DELETE");
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
