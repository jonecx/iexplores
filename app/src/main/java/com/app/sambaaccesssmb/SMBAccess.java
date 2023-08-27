package com.app.sambaaccesssmb;

import android.app.Application;
import com.app.sambaaccesssmb.model.SmbSession;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class SMBAccess extends Application {

    public static boolean isFeatureHidden = true;

    private static SMBAccess instance = null;

    private static SMBClient smbClientInstance = null;

    private static SmbSession smbSession;

    public static SMBAccess getInstance() {
        return instance;
    }

    public static SMBClient getSmbClientInstance() {
        if (smbClientInstance == null) {
            SmbConfig smbConfig =
                    SmbConfig.builder()
                            .withBufferSize(8 * 1024 * 1024) // 8MB buffer size
                            .build();
            smbClientInstance = new SMBClient(smbConfig);
        }
        return smbClientInstance;
    }

    public static SmbSession getSmbSession() {
        return smbSession;
    }

    public static void setSmbSession(SmbSession smbSession) {
        SMBAccess.smbSession = smbSession;
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
