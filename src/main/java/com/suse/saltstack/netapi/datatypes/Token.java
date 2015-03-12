package com.suse.saltstack.netapi.datatypes;

import java.util.Date;
import java.util.List;

public class Token {

    private String eauth;

    private String token;

    private String user;

    private Date start;

    private Date expire;

    private List<String> perms;

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

    public List<String> getPerms() {
        return perms;
    }
}
