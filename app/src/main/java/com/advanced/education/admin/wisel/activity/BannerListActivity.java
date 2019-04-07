package com.advanced.education.admin.wisel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.adapter.BannerListAdapter;
import com.advanced.education.admin.wisel.table.CourseTable;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.util.ArrayList;
import java.util.List;

public class BannerListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitleLabel;
    private RecyclerView rvBanner;

    private Dialog myDialogBanner;
    private Toast mToast = null;

    private UtilityFile utilityFile;

    private List<String> documentList;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == Constants.SELECT_DOCUMENT_ACTIVITY_REQUEST) {

            documentList.addAll(data.getStringArrayListExtra("SELECTED_DOCUMENTS"));

            if (documentList != null && !documentList.isEmpty()) {
                if (documentList.size() > 10) {
                    documentList = new ArrayList<>();
                    showToast("Maximum 10 images allowed.");
                } else {
                    showBannerPopUp();
                }
            } else {
                showToast("No file selected.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initialDataSetup() {

        myDialogBanner = new Dialog(this);

        utilityFile = new UtilityFile(this);

        documentList = new ArrayList<>();

        initialUISetup();

    }

    private void initialUISetup() {

        ivBack = findViewById(R.id.ivBack);
        tvTitleLabel = findViewById(R.id.tvTitleLabel);
        rvBanner = findViewById(R.id.rvBanner);

        tvTitleLabel.setTypeface(utilityFile.getTfSemiBold());

        rvBanner.setLayoutManager(new LinearLayoutManager(this));

        showRecyclerView();

    }

    private void showRecyclerView() {

        BannerListAdapter adapter = new BannerListAdapter(this, null);
        rvBanner.setAdapter(adapter);

    }

    public void backButtonClicked(View view) {

        finish();

    }

    public void fabAddBannerClicked(View view) {

        Intent i = new Intent(this, SelectMediaActivity.class);
        i.putExtra("MULTIPLE_ALLOWED", false);
        i.putExtra("TAKE_PHOTO_ALLOWED", true);
        i.putExtra("GALLERY_IMAGE_ALLOWED", true);
        startActivityForResult(i, Constants.SELECT_DOCUMENT_ACTIVITY_REQUEST);

    }

    private void showBannerPopUp(){

        myDialogBanner.setContentView(R.layout.custom_pop_up_banner);

        ImageView ivClosePopUp = myDialogBanner.findViewById(R.id.ivClosePopUp);
        TextView tvBannerLabel = myDialogBanner.findViewById(R.id.tvBannerLabel);
        TextView tvBannerDescription = myDialogBanner.findViewById(R.id.tvBannerDescription);
        ImageView ivAddBanner = myDialogBanner.findViewById(R.id.ivAddBanner);
        Button bSave = myDialogBanner.findViewById(R.id.bSave);
        Button bCancel = myDialogBanner.findViewById(R.id.bCancel);

        ivAddBanner.setImageURI(Uri.parse(documentList.get(0)));

        tvBannerLabel.setTypeface(utilityFile.getTfMedium());
        tvBannerDescription.setTypeface(utilityFile.getTfRegular());
        bSave.setTypeface(utilityFile.getTfRegular());
        bCancel.setTypeface(utilityFile.getTfRegular());

        ivClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogBanner.dismiss();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDialogBanner.dismiss();

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myDialogBanner.dismiss();

            }
        });

        myDialogBanner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialogBanner.show();

    }

    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

}
