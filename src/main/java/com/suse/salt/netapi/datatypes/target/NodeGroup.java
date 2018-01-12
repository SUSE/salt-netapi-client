package com.suse.salt.netapi.datatypes.target;

/**
 * Target for referencing a nodegroup.
 */
public class NodeGroup extends AbstractTarget<String> implements Target<String> {

    /**
     * Constructor expecting a nodegroup as string.
     *
     * @param nodegroup the nodegroup as string
     */
    public NodeGroup(String nodegroup) {
        super(nodegroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() {
        return TargetType.NODEGROUP;
    }
}
