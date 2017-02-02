package com.ethannjc.inlayphotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView galleryList;
    private Button newGalleryButton;
    public GalleryListAdapter adapter;
    private int STORAGE_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        try {
            if (!FTP.isConnected()) {
                FTP.connect();
                new LoadGalleriesTask(this).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        newGalleryButton = (Button) findViewById(R.id.new_gallery_button);
        newGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewGalleryActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                MainActivity.this.startActivity(intent);
            }
        });

        adapter = new GalleryListAdapter(this, R.layout.gallery_list_item);
        adapter.notifyDataSetChanged();
        galleryList = (ListView) findViewById(R.id.gallery_list_view);
        galleryList.setAdapter(adapter);
        galleryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("gallery", adapter.getItem(position));
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void handlePermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) return;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int request, String permissions[], int[] results) {
        if (request == STORAGE_PERMISSION && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("[MainActivity]", "Permission granted");
        } else {
            Toast.makeText(this, "Application needs to be able to read photos from storage", Toast.LENGTH_LONG).show();
        }
    }
}
