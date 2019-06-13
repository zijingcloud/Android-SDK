package com.vcrtcdemo.entities;

import java.io.Serializable;

public class InComingCall implements Serializable {

    private String remoteAlias;
    private String remoteName;
    private String conference;
    private String type;
    private String token;
    private String bssKey;
    private String time;
    private String sipkey;
    private String msgJson;

    public InComingCall(String remoteAlias, String remoteName, String conference, String type, String token, String time, String bssKey, String msgJson) {
        this.remoteAlias = remoteAlias;
        this.remoteName = remoteName;
        this.conference = conference;
        this.type = type;
        this.token = token;
        this.time = time;
        this.bssKey = bssKey;
        this.msgJson = msgJson;
    }

    public InComingCall() {

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

    public String getBssKey() {
        return bssKey;
    }

    public void setBssKey(String bssKey) {
        this.bssKey = bssKey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSipkey() {
        return sipkey;
    }

    public void setSipkey(String sipkey) {
        this.sipkey = sipkey;
    }

    public String getMsgJson() {
        return msgJson;
    }

    public void setMsgJson(String msgJson) {
        this.msgJson = msgJson;
    }

    @Override
    public String toString() {
        return "InComingCall{" +
                "remoteAlias='" + remoteAlias + '\'' +
                ", remoteName='" + remoteName + '\'' +
                ", conference='" + conference + '\'' +
                ", type='" + type + '\'' +
                ", token='" + token + '\'' +
                ", bssKey='" + bssKey + '\'' +
                ", time='" + time + '\'' +
                ", sipkey='" + sipkey + '\'' +
                ", msgJson='" + msgJson + '\'' +
                '}';
    }
}
