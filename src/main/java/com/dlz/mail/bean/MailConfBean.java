package com.dlz.mail.bean;

public class MailConfBean {
    private int auth;
    private String protocol;
    private String host;
    private String port;
    private String user;
    private String password;
    private String userName;
    private int pwdEncrypt;

    public int getAuth() {
        return auth;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPwdEncrypt() {
        return pwdEncrypt;
    }

    public void setPwdEncrypt(int pwdEncrypt) {
        this.pwdEncrypt = pwdEncrypt;
    }
}
