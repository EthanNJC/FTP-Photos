package com.ethannjc.inlayphotos;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class DeleteImageTask extends AsyncTask<Integer, Void, Void> {
    private String gallery;

    public DeleteImageTask(String gallery) {
        this.gallery = gallery.toLowerCase();
    }


    @Override
    protected Void doInBackground(Integer... params) {

        int id = params[0];

        try {

            FTP.getChannel().cd(gallery);

            // Get Meta Size
            InputStream metaIn = FTP.getChannel().get("meta.dat");
            BufferedReader metaReader = new BufferedReader(new InputStreamReader(metaIn));
            int count = Integer.parseInt(metaReader.readLine());

            // Remove Target
            String formattedId = String.format("%04d", id);
            Log.d("[DeleteImageTask]", "Deleting " + formattedId + " in directory " +
            FTP.getChannel().pwd());
            FTP.getChannel().rm(formattedId + ".dat");
            FTP.getChannel().rm(formattedId + ".jpg");
            FTP.getChannel().rm("cache_160x0/" + formattedId + ".jpg");
            Log.d("[DeleteImageTask]", "Deleted " + formattedId);

            // Rename iteration for id -> max

            for (int a = id; a < count; a++) {
                String end = String.format("%04d", a);
                String target = String.format("%04d", a+1);
                FTP.getChannel().rename(target + ".dat", end + ".dat");
                FTP.getChannel().rename(target + ".jpg", end + ".jpg");
                FTP.getChannel().rename("cache_160x0/" + target + ".jpg", "cache_160x0/" + end + ".jpg");
                Log.d("[DeleteImageTask]", target + " -> " + end);
            }

            // update meta.dat
            File meta = new File(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat");
            meta.delete();
            meta.getParentFile().mkdirs();
            meta.createNewFile();
            PrintWriter writer = new PrintWriter(meta);
            writer.print(count - 1);
            writer.flush();
            writer.close();
            FTP.upload(Environment.getExternalStorageDirectory().toString() + "/IDSPhotos/meta.dat", "meta.dat");
            FTP.getChannel().cd("..");
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
