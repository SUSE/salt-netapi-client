package com.suse.salt.netapi.datatypes.target;

/**
 * Possible values for the tgt_type or expr_form parameter in salt netapi calls.
 */
public enum TargetType {

    GLOB("glob"),
    PCRE("pcre"),
    LIST("list"),
    GRAIN("grain"),
    GRAIN_PCRE("grain_pcre"),
    PILLAR("pillar"),
    PILLAR_PCRE("pillar_pcre"),
    NODEGROUP("nodegroup"),
    RANGE("range"),
    COMPOUND("compound"),
    IPCIDR("ipcidr");

    private final String value;

    TargetType(String value) { this.value = value; }

    public String getValue() { return value; }
}
