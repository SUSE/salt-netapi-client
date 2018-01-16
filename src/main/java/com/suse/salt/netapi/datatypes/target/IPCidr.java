package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by IP CIDR.
 */
public class IPCidr extends AbstractTarget<String> implements Target<String> {

    /**
     * Default constructor.
     */
    public IPCidr(String cidr) {
        super(TargetType.IPCIDR, cidr);
    }

}
