package com.ethannjc.inlayphotos;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class CreateGalleryTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {

        try {
            FTP.mkdir(params[0].toLowerCase());
            FTP.mkdir(params[0].toLowerCase() + "/cache_160x0");
            FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/thumb.jpg", "cache_thumbs/" + params[0].toLowerCase() + ".jpg");

            File meta = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat");
            meta.delete();
            meta.getParentFile().mkdirs();
            meta.createNewFile();
            PrintWriter writer = new PrintWriter(meta);
            writer.print("0");
            writer.flush();
            writer.close();
            FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat", params[0].toLowerCase() + "/meta.dat");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("[ImagePicker]", images.get(0).getPath() + " and " + images.get(0).getName());
        return null;
    }
}
