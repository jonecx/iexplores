package com.app.sambaaccesssmb;

import android.app.Application;
import com.app.sambaaccesssmb.connection.SMBConnection;
import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class SMBAccess extends Application {
    private static SMBAccess instance = null;
    private static SMBConnection smbConnectionInstance;

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
