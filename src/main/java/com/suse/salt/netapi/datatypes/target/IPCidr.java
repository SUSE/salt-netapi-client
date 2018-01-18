package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by IP CIDR.
 */
public class IPCidr extends AbstractTarget<String> implements Target<String> {

    /**
     * Creates an IPCidr matcher
     *
     * @param cidr CIDR targeting expression
     */
    public IPCidr(String cidr) {
        super(TargetType.IPCIDR, cidr);
    }

}
