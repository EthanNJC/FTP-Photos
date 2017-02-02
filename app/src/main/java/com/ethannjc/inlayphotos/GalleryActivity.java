package com.ethannjc.inlayphotos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private static int IMAGES_PICKED = 43;

    private ListView imageList;
    private ImageButton deleteButton, saveButton;
    private Button uploadButton;

    public ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        Intent intent = getIntent();
        final String gallery = intent.getStringExtra("gallery");

        try {
            if (!FTP.isConnected()) FTP.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadButton = (Button) findViewById(R.id.upload_photos_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker imagePicker = ImagePicker.create(GalleryActivity.this);
                imagePicker
                        .returnAfterFirst(false)
                        .folderMode(true)
                        .folderTitle("Select Folder")
                        .imageTitle("Select Photos")
                        .multi()
                        .limit(20)
                        .showCamera(false)
                        .start(IMAGES_PICKED);
            }
        });


        deleteButton = (ImageButton) findViewById(R.id.gallery_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GalleryActivity.this);
                dialog.setMessage("Are you sure you want to delete this gallery?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteGalleryTask().execute(gallery);
                        FTP.galleries.remove(gallery);
                        FTP.images.clear();
                        FTP.descriptions.clear();
                        GalleryActivity.this.finish();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        saveButton = (ImageButton) findViewById(R.id.gallery_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.this.finish();
            }
        });

        adapter = new ImageListAdapter(this, R.layout.image_list_item, gallery);
        imageList = (ListView) findViewById(R.id.image_list_view);
        imageList.setFocusable(false);
        imageList.setAdapter(adapter);

        if (gallery == null) new LoadImagesTask(this).execute();
        else {
            TextView title = (TextView) findViewById(R.id.gallery_title);
            title.setText(WordUtils.capitalizeFully(gallery));
            new LoadImagesTask(this).execute(gallery);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGES_PICKED && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
            String[] paths = new String[images.size()];
            for (int a = 0; a < images.size(); a++) {
                paths[a] = images.get(a).getPath();
                Log.d("[GalleryActivity]", images.get(a).getPath());
            }
            new UploadPhotosTask(this).execute(paths);
            //Log.d("[ImagePicker]", images.get(0).getPath() + " and " + images.get(0).getName());
        }
    }
}
