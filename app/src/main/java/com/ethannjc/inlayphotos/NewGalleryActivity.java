package com.ethannjc.inlayphotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class NewGalleryActivity extends AppCompatActivity {

    private ImageButton cancelButton, saveButton;
    private EditText nameField;
    private ImageView thumbnailView;
    private Button thumbButton;

    private Bitmap thumbnail;
    private static int THUMBNAIL_PICKER = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gallery);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        cancelButton = (ImageButton) findViewById(R.id.new_gallery_cancel_button);
        saveButton = (ImageButton) findViewById(R.id.new_gallery_save_button);
        nameField = (EditText) findViewById(R.id.new_gallery_name_field);
        thumbnailView = (ImageView) findViewById(R.id.new_gallery_thumbnail);
        thumbButton = (Button) findViewById(R.id.new_gallery_thumbnail_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGalleryActivity.this.finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().length() > 0 && thumbnail != null) {
                    String title = nameField.getText().toString();
                    new CreateGalleryTask().execute(title);
                    FTP.galleries.put(title.toLowerCase(), BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/thumb.jpg"));
                    NewGalleryActivity.this.finish();
                } else Toast.makeText(NewGalleryActivity.this, "Please enter a title and thumbnail", Toast.LENGTH_LONG).show();
            }
        });

        thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewGalleryActivity.this, ImagePickerActivity.class);
                intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
                intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, "Tap to select thumbnail");
                intent.putExtra(ImagePicker.EXTRA_RETURN_AFTER_FIRST, true);
                intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_SINGLE);
                startActivityForResult(intent, THUMBNAIL_PICKER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == THUMBNAIL_PICKER && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
            File file = new File(images.get(0).getPath());
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            if (height > width) {
                int dif = (height - width) / 2;
                bmp = Bitmap.createBitmap(bmp, 0, dif, width, width);
            } else {
                int dif = (width - height) / 2;
                bmp = Bitmap.createBitmap(bmp, dif, 0, height, height);
            }
            thumbnail = bmp;
            thumbnailView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 400, 400, true));
            try {
                File f = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/thumb.jpg");
                f.delete();
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                bmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            //Log.d("[ImagePicker]", images.get(0).getPath() + " and " + images.get(0).getName());
        }
    }
}
