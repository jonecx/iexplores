package com.app.sambaaccesssmb.model;

import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.rapid7.client.dcerpc.mssrvs.ServerService;

public class SmbSession {
    private ServerService serverService;
    private Session session;
    private DiskShare currentDiskShare;

    public SmbSession(ServerService serverService, Session session) {
        this.serverService = serverService;
        this.session = session;
    }

    public ServerService getServerService() {
        return serverService;
    }

    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public DiskShare getCurrentDiskShare() {
        return currentDiskShare;
    }

    public void setCurrentDiskShare(DiskShare currentDiskShare) {
        this.currentDiskShare = currentDiskShare;
    }

    public boolean isConnected() {
        return this != null && session.getConnection().isConnected();
    }
}
