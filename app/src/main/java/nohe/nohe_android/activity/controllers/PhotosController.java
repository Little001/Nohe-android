package nohe.nohe_android.activity.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.services.PagerService;

public class PhotosController {
    private AppCompatActivity context;
    private PagerService pagerService;
    private Button button;
    private Button dependButton;
    private Button btnRemovePhoto;
    private ArrayList<Bitmap> photoCollection;
    public String mCurrentPhotoPath;

    public PhotosController(AppCompatActivity context, PagerService pagerService, ArrayList<Bitmap> photoCollection, Button button, Button dependButton,
                            Button btnRemovePhoto) {
        this.context = context;
        this.pagerService = pagerService;
        this.button = button;
        this.dependButton = dependButton;
        this.btnRemovePhoto = btnRemovePhoto;
        this.photoCollection = photoCollection;
        this.mCurrentPhotoPath = "";
        setDependButtonVisibility();
        setRemovePhotoButtonVisibility();
    }

    public void addPhoto(Bitmap photo) {
        photoCollection.add(photo);
        this.updateImageSwitcher();
        setCameraButtonVisibility();
        setDependButtonVisibility();
        setRemovePhotoButtonVisibility();
    }

    public void removePhoto() {
        ViewPager mViewPager = (ViewPager) this.context.findViewById(R.id.photo_show_pager);

        if(photoCollection.size() > 0) {
            photoCollection.remove(mViewPager.getCurrentItem());
            updateImageSwitcher();
        }
        setCameraButtonVisibility();
        setDependButtonVisibility();
        setRemovePhotoButtonVisibility();
    }

    private void updateImageSwitcher() {
        ViewPager mViewPager = (ViewPager) this.context.findViewById(R.id.photo_show_pager);
        mViewPager.setAdapter(pagerService);

        if (this.photoCollection.size() < 1 ) {
            mViewPager.setBackground(ContextCompat.getDrawable(context, R.drawable.images));
        } else {
            mViewPager.setBackgroundResource(0);
        }
    }

    private void setCameraButtonVisibility() {
        if (this.photoCollection.size() > 2) {
            this.button.setEnabled(false);
        } else {
            this.button.setEnabled(true);
        }
    }

    private void setDependButtonVisibility() {
        if (this.photoCollection.size() >= 1 && this.photoCollection.size() <= 3) {
            this.dependButton.setEnabled(true);
        } else {
            this.dependButton.setEnabled(false);
        }
    }

    private void setRemovePhotoButtonVisibility() {
        if (this.photoCollection.size() >= 1) {
            this.btnRemovePhoto.setVisibility(View.VISIBLE);
        } else {
            this.btnRemovePhoto.setVisibility(View.INVISIBLE);
        }
    }

    public void dispatchTakePictureIntent(Integer request_take_photo) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                if (!AppConfig.IS_PRODUCTION) {
                    Toast.makeText(this.context.getApplicationContext(),
                            ex.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this.context,
                        "com.mydomain.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.context.startActivityForResult(takePictureIntent, request_take_photo);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
