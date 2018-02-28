package com.zijingdemo;

import java.io.Serializable;

/**
 * Created by wangzhen on 2017/12/1.
 */

public class ZjInComingCall implements Serializable {

    public static final String TYPE_DOT = "gateway";
    public static final String TYPE_CONFERENCE = "conference";

    private String remoteAlias;
    private String remoteName;
    private String conference;
    private String type;
    private String token;
    private String time;


    public ZjInComingCall(String remoteAlias, String remoteName, String conference, String type, String token, String time) {
        this.remoteAlias = remoteAlias;
        this.remoteName = remoteName;
        this.conference = conference;
        this.type = type;
        this.token = token;
        this.time = time;
    }

    public ZjInComingCall() {
    }

    public String getRemoteAlias() {
        return remoteAlias;
    }

    public void setRemoteAlias(String remoteAlias) {
        this.remoteAlias = remoteAlias;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ZjInComingCall{" +
                "remoteAlias='" + remoteAlias + '\'' +
                ", remoteName='" + remoteName + '\'' +
                ", conference='" + conference + '\'' +
                ", type='" + type + '\'' +
                ", token='" + token + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
