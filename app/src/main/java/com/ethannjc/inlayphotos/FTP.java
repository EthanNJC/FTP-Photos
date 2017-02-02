package com.ethannjc.inlayphotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

public class FTP {

    // Temporary static config for testing
    private static final String host = "";
    private static final String user = "";
    private static final String pass = "";
    private static int port = 22;

    private static Channel channel = null;
    private static Session session = null;
    private static ChannelSftp sftpChannel = null;

    public static HashMap<String, Bitmap> galleries = new HashMap<>();
    public static HashMap<Integer, Bitmap> images = new HashMap<>();
    public static HashMap<Integer, String> descriptions = new HashMap<>();
    public static String currentGallery;

    public static void connect() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(user, host, port);
        session.setPassword(pass);
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();

        sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd("html/wp-content/uploads/gallery");

    }

    public static void disconnect() {
        sftpChannel.exit();
        channel.disconnect();
        session.disconnect();
    }

    public static ChannelSftp getChannel() {
        return sftpChannel;
    }

    public static void mkdir(String title) {
        try {
            sftpChannel.mkdir(title);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public static void upload(String file, String dest) {
        try {
            sftpChannel.put(file, dest);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public static File download(String path) {
        return new File(path);
    }

    public static boolean isConnected() {
        if (sftpChannel == null) return false;
        return sftpChannel.isConnected();
    }
}
