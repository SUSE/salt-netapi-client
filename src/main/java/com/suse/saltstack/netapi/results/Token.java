package com.suse.saltstack.netapi.results;

import java.util.Date;

public class Token {

    // String attributes
    private String eauth;
    private String token;
    private String user;

    private Date start;
    private Date expire;

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
}
