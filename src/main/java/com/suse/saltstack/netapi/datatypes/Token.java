package com.suse.saltstack.netapi.datatypes;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Token {

    // String attributes
    private String eauth;
    private String token;
    private String user;

    private Date start;
    private Date expire;

    @SerializedName("perms")
    private List<String> permissions;

    public String getEauth() {
        return eauth;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

    public Date getStart() {
        return start;
    }

    public Date getExpire() {
        return expire;
    }

    /**
     * Returns the list of permissions for the user.<br>
     * <br>
     * Example results: <br>
     * <br>
     * [".*"] or ["@runner", "@jobs"]
     * 
     * @return permissions
     */
    public List<String> getPermissions() {
        return permissions;
    }
}
