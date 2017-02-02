package com.ethannjc.inlayphotos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class UploadPhotosTask extends AsyncTask<String[], Void, Void> {

    GalleryActivity activity;

    public UploadPhotosTask(GalleryActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String[]... params) {
        String[] paths = params[0];
        try {
            if (FTP.currentGallery != null) FTP.getChannel().cd(FTP.currentGallery);
            InputStream metaIn = FTP.getChannel().get("meta.dat");
            BufferedReader metaReader = new BufferedReader(new InputStreamReader(metaIn));
            int count = Integer.parseInt(metaReader.readLine());
            Log.d("[UploadImagesTask]", "Count" + count);

            for (int a = 0; a < paths.length; a++) {

                Bitmap bmp = BitmapFactory.decodeFile(paths[a]);
                double factor = (double) bmp.getWidth() / 160;
                Log.d("[UploadTask]", "Width: " + bmp.getWidth());
                Log.d("[UploadTask]", "Height: " + bmp.getHeight());
                Log.d("[UploadTask]", "Factor: " + factor);
                String id = String.format("%04d", a+count+1);
                try {
                    File f = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/thumb.jpg");
                    f.delete();
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    bmp = Bitmap.createScaledBitmap(bmp, 160, (int) Math.round((double) bmp.getHeight() / factor), true);
                    FTP.images.put(count+a+1, bmp);
                    FTP.descriptions.put(count+a+1, "");
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/thumb.jpg", "cache_160x0/" + id + ".jpg");
                    Log.d("[UploadImagesTask]", "Uploaded Thumb" + id);
                    FTP.upload(paths[a], id + ".jpg");
                    Log.d("[UploadImagesTask]", "Uploaded Image" + id);
                    FTP.getChannel().put( new ByteArrayInputStream( "".getBytes() ), id + ".dat");

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.adapter.notifyDataSetChanged();
                    }
                });

                Log.d("[UploadImagesTask]", "Uploades Meta" + id);
            }

            File meta = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat");
            meta.delete();
            meta.getParentFile().mkdirs();
            meta.createNewFile();
            PrintWriter writer = new PrintWriter(meta);
            writer.print(Integer.toString(count+paths.length));
            writer.flush();
            writer.close();
            FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat", "meta.dat");

            if (FTP.currentGallery != null) FTP.getChannel().cd("..");


        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
