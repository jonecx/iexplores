package com.app.sambaaccesssmb.model;

import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;

public class SmbSession {
    private ServerService serverService;
    private Session session;

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

    public boolean isConnected() {
        return this != null && session.getConnection().isConnected();
    }
}
