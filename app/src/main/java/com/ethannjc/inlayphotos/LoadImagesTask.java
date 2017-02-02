package com.ethannjc.inlayphotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class LoadImagesTask extends AsyncTask<String, Void, Void> {

    private GalleryActivity activity;

    public LoadImagesTask(GalleryActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        FTP.images.clear();
        FTP.descriptions.clear();
        try {
            String gallery;
            if (params.length > 0) {
                FTP.currentGallery = params[0];
                gallery = params[0] + "/";
                Log.d("[LoadImageTask]", "Attempting to cd to " + params[0] + " from" + FTP.getChannel().pwd());

                FTP.getChannel().cd(params[0]);
            } else {
                gallery = "";
                FTP.currentGallery = null;
            }
            InputStream metaIn = FTP.getChannel().get("meta.dat");
            BufferedReader metaReader = new BufferedReader(new InputStreamReader(metaIn));
            int count = Integer.parseInt(metaReader.readLine());
            // Log.d("[LoadImageTask]", count + "");
            for (int a = 1; a <= count; a++) {
                String id = String.format("%04d", a);
                //Log.d("[LoadImageTask]", id + ".dat");

                URL url = new URL("http://tracycoxguitars.com/wp-content/uploads/gallery/" + gallery.replace(" ", "%20") + "cache_160x0/" + id + ".jpg");
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                URL url2 = new URL("http://tracycoxguitars.com/wp-content/uploads/gallery/" + gallery.replace(" ", "%20") + id + ".dat");
                BufferedReader in = new BufferedReader(new InputStreamReader(url2.openStream()));
                String description = "";
                String line;
                while ((line = in.readLine()) != null) {
                    description += line;
                }
                //InputStream descriptionIn = FTP.getChannel().get(id + ".dat");
                //java.util.Scanner s = new java.util.Scanner(descriptionIn).useDelimiter("\\A");
                //String description = s.hasNext() ? s.next() : "";
                FTP.descriptions.put(a, description);
                //descriptionIn.close();
                //InputStream imageIn = FTP.getChannel().get("cache_160x0/" + id + ".jpg");
                //Bitmap image = BitmapFactory.decodeStream(imageIn);
                FTP.images.put(a, image);
                //imageIn.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.adapter.notifyDataSetChanged();
                    }
                });
            }
            metaReader.close();
            metaIn.close();

            if (params.length > 0) FTP.getChannel().cd("..");
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

