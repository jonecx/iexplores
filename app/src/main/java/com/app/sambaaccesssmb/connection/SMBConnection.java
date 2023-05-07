package com.app.sambaaccesssmb.connection;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import com.app.sambaaccesssmb.BuildConfig;
import com.app.sambaaccesssmb.SMBAccess;
import com.app.sambaaccesssmb.interfaces.ReceiveCallback;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import jcifs.CIFSContext;
import jcifs.Configuration;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class SMBConnection {
    private static volatile SMBConnection instance = null;
    private ReceiveCallback receiveCallback;
    private SmbFile rootSMBFile;
    private SmbFile currentSMBFile;
    private boolean threadRunning = true;
    private final Runnable runnable =
            new Runnable() {
                @Override
                public void run() {
                    try {
                        while (threadRunning) {
                            if (receiveCallback != null) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("is_success", true);
                                receiveCallback.onReceiveCallback(bundle);
                                receiveCallback.onReceiveFilesData(
                                        Arrays.asList(currentSMBFile.listFiles()));
                            }
                            Thread.sleep(500);
                        }
                    } catch (SmbException | InterruptedException e) {
                        handleError(e.getLocalizedMessage());
                    }
                }
            };
    private Thread thread;
    private boolean isConnectionStabled;

    public static SMBConnection getInstance() {
        if (instance == null) {
            synchronized (SMBConnection.class) {
                if (instance == null) {
                    instance = new SMBConnection();
                }
            }
        }
        return instance;
    }

    public boolean isConnectionStabled() {
        return isConnectionStabled;
    }

    private void handleError(String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_success", false);
        bundle.putString("reason", errorMessage);
        receiveCallback.onReceiveCallback(bundle);
    }

    public void setRootSMBFile(SmbFile rootSMBFile) {
        this.rootSMBFile = rootSMBFile;
    }

    public void isConnected(boolean isConnected) {
        this.isConnectionStabled = isConnected;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void setReceiveCallback(ReceiveCallback receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public void initiateConnection(String serverAddress, String userName, String password) {

        Thread thread =
                new Thread(
                        () -> {
                            Properties jcifsProperties = new Properties();
                            jcifsProperties.setProperty("jcifs.smb.client.enableSMB2", "true");
                            jcifsProperties.setProperty("jcifs.smb.client.dfs.disabled", "true");
                            Bundle bundle = new Bundle();

                            try {
                                Configuration config = new PropertyConfiguration(jcifsProperties);
                                BaseContext baseCxt = new BaseContext(config);
                                NtlmPasswordAuthenticator ntlmPasswordAuthenticator =
                                        new NtlmPasswordAuthenticator(
                                                serverAddress, userName, password);
                                CIFSContext auth =
                                        baseCxt.withCredentials(ntlmPasswordAuthenticator);
                                rootSMBFile = new SmbFile(serverAddress, (CIFSContext) auth);
                                rootSMBFile.connect();
                                bundle.putBoolean("is_success", true);
                                isConnectionStabled = true;
                                receiveCallback.onReceiveCallback(bundle);
                            } catch (IOException e) {
                                handleError(e.getLocalizedMessage());
                            }
                        });
        thread.setName("smb-connection");
        thread.start();
    }

    public void getAllFiles() {
        getAllFiles(null);
    }

    public void getAllFiles(SmbFile currentFile) {
        currentSMBFile = currentFile != null ? currentFile : rootSMBFile;
        if (thread == null || !thread.isAlive()) {
            threadRunning = true;
            thread = new Thread(runnable);
            thread.setName("smb-get-files");
            thread.start();
        }
    }

    public void uploadFile(File file) throws IOException {
        Thread thread =
                new Thread(
                        () -> {
                            try {
                                // local source file and target smb file
                                SmbFile smbFileTarget = new SmbFile(currentSMBFile, file.getName());

                                // input and output stream
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileTarget);

                                // writing data
                                try {
                                    // 16 kb
                                    final byte[] b = new byte[16 * 1024];
                                    int read;
                                    if (fis != null) {
                                        while ((read = fis.read(b, 0, b.length)) > 0) {
                                            smbfos.write(b, 0, read);
                                        }
                                    }
                                } finally {
                                    if (fis != null) {
                                        fis.close();
                                    }
                                    smbfos.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
        thread.setName("upload-file");
        thread.start();
    }

    public void deleteFile(SmbFile currentFile) {
        Thread thread =
                new Thread(
                        () -> {
                            try {
                                if (currentFile != null && currentFile.canWrite()) {
                                    currentFile.delete();
                                }
                            } catch (SmbException e) {
                                e.printStackTrace();
                            }
                        });
        thread.setName("delete-file");
        thread.start();
    }

    public void downloadFile(SmbFile smbFile) {
        Thread thread =
                new Thread(
                        () -> {
                            try {
                                // local source file and target smb file
                                File smbFileTarget =
                                        new File(
                                                SMBAccess.getInstance().getCacheDir(),
                                                smbFile.getName());

                                // input and output stream

                                // writing data
                                try (SmbFileInputStream fis = new SmbFileInputStream(smbFile);
                                        FileOutputStream fos =
                                                new FileOutputStream(smbFileTarget)) {
                                    // 16 kb
                                    final byte[] b = new byte[16 * 1024];
                                    int read;
                                    while ((read = fis.read(b, 0, b.length)) > 0) {
                                        fos.write(b, 0, read);
                                    }
                                }
                                viewFile(smbFileTarget);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
        thread.setName("download-file");
        thread.start();
    }

    private void viewFile(File file) {
        Context context = SMBAccess.getInstance();
        Uri fileURI =
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(fileURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> resInfoList =
                context.getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(
                    packageName,
                    fileURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        SMBAccess.getInstance().startActivity(intent);
    }

    public void releaseThread() {
        threadRunning = false;
    }

    public SmbFile getCurrentSMBFile() {
        return currentSMBFile;
    }

    public SmbFile getRootSMBFile() {
        return rootSMBFile;
    }
}
