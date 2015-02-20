package com.suse.saltstack.netapi.results;

public class Token {

    // String attributes
    private String eauth;
    private String token;
    private String user;

    // TODO: Validity dates
    // private Date start;
    // private Date expire;

    public String getEauth() {
        return eauth;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

//    public Date getStart() {
//        return start;
//    }
//    public Date getExpire() {
//        return expire;
//    }
}
