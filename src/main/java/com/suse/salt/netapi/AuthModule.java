package com.suse.salt.netapi;

/**
 * Salt authentication modules.
 *
 * @see <a href="http://docs.saltstack.com/en/latest/ref/auth/all/">Modules</a>
 */
public enum AuthModule {

    AUTO("auto"),
    DJANGO("django"),
    FILE("file"),
    KEYSTONE("keystone"),
    LDAP("ldap"),
    MYSQL("mysql"),
    PAM("pam"),
    PKI("pki"),
    REST("rest"),
    SHAREDSECRET("sharedsecret"),
    YUBICO("yubico");

    private final String value;

    AuthModule(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
