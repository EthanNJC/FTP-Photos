package com.ethannjc.inlayphotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.util.Vector;

public class LoadGalleriesTask extends AsyncTask<Void, Void, Void> {
    private MainActivity activity;
    public LoadGalleriesTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        FTP.galleries.clear();
        try {
            Vector<ChannelSftp.LsEntry> ls = FTP.getChannel().ls("*");
            for (ChannelSftp.LsEntry entry : ls) {
                if (entry.getAttrs().isDir() && !entry.getFilename().startsWith("cache_")){
                    InputStream thumbIn = FTP.getChannel().get("cache_thumbs/" + entry.getFilename() + ".jpg");
                    Bitmap thumb = BitmapFactory.decodeStream(thumbIn);
                    FTP.galleries.put(entry.getFilename(), thumb);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return null;
    }
}
