package com.suse.saltstack.netapi.datatypes;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author azouz
 */
public class Events {

    @SerializedName("tag")
    private String tag;

    @SerializedName("data")
    private String data;

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }
}
