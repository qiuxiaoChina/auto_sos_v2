package com.autosos.yd.model;

/**
 * Created by Administrator on 2015/7/29.
 */
public class Version {
    private String version;
    private int verCode;
    private int canUpdateVersion;
    private String name;
    private String url;
    private String update_data;
    private int debug_versioncode;

    public int getCanUpdateVersion() {
        return canUpdateVersion;
    }

    public void setCanUpdateVersion(int canUpdateVersion) {
        this.canUpdateVersion = canUpdateVersion;
    }

    public int getDebug_versioncode() {
        return debug_versioncode;
    }

    public void setDebug_versioncode(int debug_versioncode) {
        this.debug_versioncode = debug_versioncode;
    }

    public String getUpdate_data() {
        return update_data;
    }

    public void setUpdate_data(String update_data) {
        this.update_data = update_data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
