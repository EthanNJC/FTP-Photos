package com.ethannjc.inlayphotos;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.util.Vector;

public class DeleteGalleryTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        try {
            delete(params[0]);
            FTP.getChannel().rm("cache_thumbs/" + params[0] + ".jpg");
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void delete(String directory) throws SftpException {
        FTP.getChannel().cd(directory);
        Vector<ChannelSftp.LsEntry> entries = FTP.getChannel().ls("*");
        for (ChannelSftp.LsEntry entry: entries) {
            if (!entry.getAttrs().isDir()) {
                FTP.getChannel().rm(entry.getFilename());
                //Log.d("[DeleteGallery]", "Deleted " + entry.getFilename());
            } else {
                //Log.d("[DeleteGallery]", "Directory?: " + directory);
                delete(entry.getFilename());
            }
        }
        FTP.getChannel().cd("..");
        FTP.getChannel().rmdir(directory);
        //Log.d("[DeleteGallery]", "Deleted " + directory);
    }
}
