package com.app.sambaaccesssmb;

import android.app.Application;
import timber.log.Timber;

public class SMBAccess extends Application {
    private static SMBAccess instance = null;

    public static SMBAccess getInstance() {
        return instance;
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
