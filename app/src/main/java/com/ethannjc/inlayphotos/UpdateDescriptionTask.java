package com.ethannjc.inlayphotos;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class UpdateDescriptionTask extends AsyncTask<Integer, Void, Void> {

    private String gallery;

    public UpdateDescriptionTask(String gallery) {
        if (gallery == null) this.gallery = ".";
        else this.gallery = gallery;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try {
            File desc = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/desc.dat");
            desc.delete();
            desc.getParentFile().mkdirs();
            desc.createNewFile();
            PrintWriter writer = new PrintWriter(desc);
            writer.print(FTP.descriptions.get(params[0]));
            // Log.d("[UpdateDescriptionTask]", FTP.descriptions.get(params[0]));
            writer.flush();
            writer.close();
            FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/desc.dat", gallery + "/" + String.format("%04d", params[0]) + ".dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
