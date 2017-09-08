package com.huiji.comic.bobcat.allcomics.bean;

/**
 * Created by HeYongchen on 2017/8/17.
 */

public class UpdateNoteBean {

    private String version;
    private String msg;

    public UpdateNoteBean(String version, String msg) {
        this.version = version;
        this.msg = msg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
