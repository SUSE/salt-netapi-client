package com.suse.saltstack.netapi;

/**
 * Saltstack authentication modules.
 *
 * @see <a href="http://docs.saltstack.com/en/latest/ref/auth/all/">Modules</a>
 */
public enum AuthModule {

    AUTO("auto"),
    DJANGO("django"),
    KEYSTONE("keystone"),
    LDAP("ldap"),
    MYSQL("mysql"),
    PAM("pam"),
    PKI("pki"),
    STORMPATH_MOD("stormpath_mod"),
    YUBICO("yubico");

    private final String value;

    AuthModule(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
