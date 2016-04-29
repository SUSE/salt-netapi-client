package com.suse.salt.netapi.calls.modules;

/**
 * The type of hash based on Python hashlib - only always supported types.
 */
public enum HashType {

    SHA1("sha1"),
    MD5("md5"),
    SHA256("sha256"),
    SHA224("sha224"),
    SHA512("sha512"),
    SHA384("sha384");

    private String hashType;

    HashType(String hashType) {
        this.hashType = hashType;
    }

    /**
     *
     * @return the corresponding hash type.
     */
    public String getHashType() {
        return hashType;
    }
}
