package com.advanced.education.admin.wisel.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.utilities.Constants;
import com.advanced.education.admin.wisel.utilities.ImageFilePath;
import com.advanced.education.admin.wisel.utilities.UtilityFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SelectMediaActivity extends AppCompatActivity {

    private static final String TAG = SelectMediaActivity.class.getSimpleName();

    private TextView tvAddMediaLabel;
    private ImageView ivClose;
    private TextView tvTakePhoto;
    private TextView tvGalleryImage;
    private TextView tvVideo;
    private TextView tvFile;

    private Toast mToast = null;

    private File mActualImageFile = null;

    private UtilityFile utilityFile;

    private boolean isMultipleSelectionAllowed = true;
    private boolean isTakePhotoAllowed = false;
    private boolean isGalleryImageAllowed = false;
    private boolean isVideoAllowed = false;
    private boolean isFileAllowed = false;
    private String sImagePath = null;
    private ArrayList<String> documentArrayList;
    private ArrayList<Integer> permissionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);

        initialDataSetup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.SELECT_GALLERY_OPTION) {

                if (data.getClipData() != null) {

                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {

                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();

                        sImagePath = ImageFilePath.getPath(this, imageUri);
                        documentArrayList.add(sImagePath);

                        currentItem = currentItem + 1;
                    }
                    imagesSelected();

                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();

                    sImagePath = ImageFilePath.getPath(this, imageUri);
                    documentArrayList.add(sImagePath);
                    imagesSelected();
                }
            } else if (requestCode == Constants.SELECT_CAMERA_OPTION) {

                sImagePath = mActualImageFile.getAbsolutePath();
                documentArrayList.add(sImagePath);
                imagesSelected();

            } else {
                Log.d(TAG, "onActivityResult: No image selected");
            }
        } else {
            Log.d(TAG, "onActivityResult: picture selection error occurred");
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if ((requestCode == Constants.REQUEST_CALL_ACCESS
                || requestCode == Constants.REQUEST_CAMERA_ACCESS
                || requestCode == Constants.REQUEST_LOCATION_ACCESS
                || requestCode == Constants.REQUEST_READ_STORAGE_ACCESS
                || requestCode == Constants.REQUEST_WRITE_STORAGE_ACCESS)
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            permissionArrayList.remove(Integer.valueOf(requestCode));

            if (!permissionArrayList.isEmpty()) {
                utilityFile.checkPermissions(permissionArrayList);
            }

            showToast("Permission granted");

        }

    }

    private void initialDataSetup() {

        Intent i = getIntent();

        if (i != null) {
            isMultipleSelectionAllowed = i.getBooleanExtra("MULTIPLE_ALLOWED", false);
            isTakePhotoAllowed = i.getBooleanExtra("TAKE_PHOTO_ALLOWED", false);
            isGalleryImageAllowed = i.getBooleanExtra("GALLERY_IMAGE_ALLOWED", false);
            isVideoAllowed = i.getBooleanExtra("VIDEO_ALLOWED", false);
            isFileAllowed = i.getBooleanExtra("FILE_ALLOWED", false);
        }

        if (!isTakePhotoAllowed && !isGalleryImageAllowed && !isVideoAllowed && !isFileAllowed) {
            showToast("No option for media selection available");
            finish();
            return;
        }

        utilityFile = new UtilityFile(this);

        documentArrayList = new ArrayList<>();

        permissionArrayList = new ArrayList<>();
        permissionArrayList.add(Constants.REQUEST_CAMERA_ACCESS);
        permissionArrayList.add(Constants.REQUEST_READ_STORAGE_ACCESS);
        permissionArrayList.add(Constants.REQUEST_WRITE_STORAGE_ACCESS);
        utilityFile.checkPermissions(permissionArrayList);

        initialUISetup();

    }

    private void initialUISetup() {

        tvAddMediaLabel = findViewById(R.id.tvAddMediaLabel);
        ivClose = findViewById(R.id.ivClose);
        tvTakePhoto = findViewById(R.id.tvTakePhoto);
        tvGalleryImage = findViewById(R.id.tvGalleryImage);
        tvVideo = findViewById(R.id.tvVideo);
        tvFile = findViewById(R.id.tvFile);

        tvAddMediaLabel.setTypeface(utilityFile.getTfMedium());
        tvTakePhoto.setTypeface(utilityFile.getTfRegular());
        tvGalleryImage.setTypeface(utilityFile.getTfRegular());
        tvVideo.setTypeface(utilityFile.getTfRegular());
        tvFile.setTypeface(utilityFile.getTfRegular());

        if (!isTakePhotoAllowed) {
            View v1 = findViewById(R.id.v1);
            v1.setVisibility(View.GONE);
            tvTakePhoto.setVisibility(View.GONE);
        }

        if (!isGalleryImageAllowed) {
            View v2 = findViewById(R.id.v2);
            v2.setVisibility(View.GONE);
            tvGalleryImage.setVisibility(View.GONE);
        }

        if (!isVideoAllowed) {
            View v3 = findViewById(R.id.v3);
            v3.setVisibility(View.GONE);
            tvVideo.setVisibility(View.GONE);
        }

        if (!isFileAllowed) {
            View v3 = findViewById(R.id.v3);
            v3.setVisibility(View.GONE);
            tvFile.setVisibility(View.GONE);
        }

    }

    private void cameraIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            mActualImageFile = null;
            try {
                mActualImageFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "cameraIntent: ", ex);
            }
            if (mActualImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getResources().getString(R.string.photo_uri),
                        mActualImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.SELECT_CAMERA_OPTION);
            }
        }

    }

    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (isMultipleSelectionAllowed) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_gallery_options)), Constants.SELECT_GALLERY_OPTION);

    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        sImagePath = image.getAbsolutePath();
        return image;

    }

    public void closeClicked(View view) {

        finish();

    }

    public void takePhotoClicked(View view) {

        cameraIntent();

    }

    public void galleryImageClicked(View view) {

        galleryIntent();

    }

    private void imagesSelected() {

        Intent i = new Intent();
        i.putExtra("SELECTED_DOCUMENTS", documentArrayList);
        setResult(Activity.RESULT_OK, i);
        finish();

    }


    private void showToast(String sDisplayMessage) {

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, "" + sDisplayMessage, Toast.LENGTH_SHORT);
        mToast.show();

    }

}
