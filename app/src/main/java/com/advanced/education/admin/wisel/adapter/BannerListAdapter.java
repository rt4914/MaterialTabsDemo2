package com.advanced.education.admin.wisel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.table.BannerTable;
import com.advanced.education.admin.wisel.utilities.MyAlertDialog;
import com.advanced.education.admin.wisel.utilities.MyDeleteDialogInterface;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.List;

public class BannerListAdapter extends RecyclerView.Adapter<BannerListAdapter.MyViewHolder> {

    private Context context;
    private List<BannerTable> BannerTableList;

    private MyAlertDialog myAlertDialog = null;
    private UtilityFile utilityFile;

    public BannerListAdapter(Context context, List<BannerTable> BannerTableList) {

        this.context = context;
        this.BannerTableList = BannerTableList;

        utilityFile = new UtilityFile(context);

        deleteDialogSetup();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_banner_main, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i % 2 == 0) {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.llMain.setBackgroundColor(context.getResources().getColor(R.color.whiteLightBackground));
        }

//        if ((i + 1) == BannerTableList.size()) {
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
        private ImageView ivBanner;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.llMain);
            ivBanner = itemView.findViewById(R.id.ivBanner);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Intent i = new Intent(context, AddBannerActivity.class);
//                    i.putExtra("MODE", Constants.ACTIVITY_MODE_VIEW);
//                    context.startActivity(i);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myAlertDialog.createDeleteAlertBox("Delete Banner", true, "DELETE");
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
