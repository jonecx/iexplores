package com.app.sambaaccesssmb;

import android.app.Application;
import com.app.sambaaccesssmb.connection.SMBConnection;
import dagger.hilt.android.HiltAndroidApp;
import java.util.Properties;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import timber.log.Timber;

@HiltAndroidApp
public class SMBAccess extends Application {
    private static SMBAccess instance = null;
    private static SMBConnection smbConnectionInstance;

    private static final String enableSMB2Property = "jcifs.smb.client.enableSMB2";
    private static final String distributedFileSystemProperty = "jcifs.smb.client.dfs.disabled";

    private static CIFSContext cifsContext = null;

    public static CIFSContext getCIFSContext(String address, String username, String password)
            throws CIFSException {
        if (cifsContext == null) {
            Properties properties = new Properties();
            properties.setProperty(enableSMB2Property, "true");
            properties.setProperty(distributedFileSystemProperty, "true");

            PropertyConfiguration propertyConfiguration = new PropertyConfiguration(properties);
            BaseContext baseContext = new BaseContext(propertyConfiguration);
            NtlmPasswordAuthenticator ntlmPasswordAuthenticator =
                    new NtlmPasswordAuthenticator(address, username, password);
            cifsContext = baseContext.withCredentials(ntlmPasswordAuthenticator);
            return cifsContext;
        }
        return cifsContext;
    }

    public static SMBAccess getInstance() {
        return instance;
    }

    public static SMBConnection getSmbConnectionInstance() {
        if (smbConnectionInstance == null) {
            synchronized (SMBConnection.class) {
                smbConnectionInstance = new SMBConnection();
            }
        }
        return smbConnectionInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configLogger();
    }

    private void configLogger() {
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }
}
