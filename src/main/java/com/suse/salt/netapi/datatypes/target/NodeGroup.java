package com.suse.salt.netapi.datatypes.target;

/**
 * Target for referencing a nodegroup.
 */
public class NodeGroup implements Target<String> {

    private final String nodegroup;

    /**
     * Constructor expecting a nodegroup as string.
     *
     * @param nodegroup the nodegroup as string
     */
    public NodeGroup(String nodegroup) {
        this.nodegroup = nodegroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTarget() {
        return nodegroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "nodegroup";
    }
}
