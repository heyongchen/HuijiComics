package com.huiji.comic.bobcat.huijicomics.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicDataBean implements Parcelable {
    private String dataUrl;
    private String dataTitle;

    public ComicDataBean(String dataUrl, String dataTitle) {
        this.dataUrl = dataUrl;
        this.dataTitle = dataTitle;
    }

    protected ComicDataBean(Parcel in) {
        dataUrl = in.readString();
        dataTitle = in.readString();
    }

    public static final Creator<ComicDataBean> CREATOR = new Creator<ComicDataBean>() {
        @Override
        public ComicDataBean createFromParcel(Parcel in) {
            return new ComicDataBean(in);
        }

        @Override
        public ComicDataBean[] newArray(int size) {
            return new ComicDataBean[size];
        }
    };

    public String getDataUrl() {
        return dataUrl;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataUrl);
        dest.writeString(dataTitle);
    }
}
