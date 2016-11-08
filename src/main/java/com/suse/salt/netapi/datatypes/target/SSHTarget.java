package com.suse.salt.netapi.datatypes.target;

/**
 * Target interface for specifying a group of minions.
 * Target types implementing this interface can be used with salt-ssh
 *
 * @param <T> Type of tgt property when making a request
 */
public interface SSHTarget<T> extends Target<T> {
}
