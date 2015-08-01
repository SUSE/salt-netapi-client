package com.suse.saltstack.netapi.datatypes.target;

/**
 * Target for referencing a nodegroup.
 */
public class NodeGroup implements Target<String> {

    private final String nodegroup;

    public NodeGroup(String nodegroup) {
        this.nodegroup = nodegroup;
    }

    @Override
    public String target() {
        return nodegroup;
    }

    @Override
    public String targetType() {
        return "nodegroup";
    }

}
