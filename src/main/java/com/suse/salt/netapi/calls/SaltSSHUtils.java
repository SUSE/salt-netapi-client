package com.suse.salt.netapi.calls;

import java.util.Map;

/**
 * Salt SSH utility class with shared methods.
 */
public class SaltSSHUtils {

    /**
     * Maps config parameters to salt-ssh rest arguments
     * @param cfg SSH configuration to read values to be converted
     * @param props properties to be set when rest calling
     */
    public static void mapConfigPropsToArgs(SaltSSHConfig cfg, Map<String, Object> props) {
        cfg.getExtraFilerefs().ifPresent(v -> props.put("extra_filerefs", v));
        cfg.getIdentitiesOnly().ifPresent(v -> props.put("ssh_identities_only", v));
        cfg.getIgnoreHostKeys().ifPresent(v -> props.put("ignore_host_keys", v));
        cfg.getKeyDeploy().ifPresent(v -> props.put("ssh_key_deploy", v));
        cfg.getNoHostKeys().ifPresent(v -> props.put("no_host_keys", v));
        cfg.getPasswd().ifPresent(v -> props.put("ssh_passwd", v));
        cfg.getPriv().ifPresent(v -> props.put("ssh_priv", v));
        cfg.getRefreshCache().ifPresent(v -> props.put("refresh_cache", v));
        cfg.getRemotePortForwards()
                .ifPresent(v -> props.put("ssh_remote_port_forwards", v));
        cfg.getRoster().ifPresent(v -> props.put("roster", v));
        cfg.getRosterFile().ifPresent(v -> props.put("roster_file", v));
        cfg.getScanPorts().ifPresent(v -> props.put("ssh_scan_ports", v));
        cfg.getScanTimeout().ifPresent(v -> props.put("ssh_scan_timeout", v));
        cfg.getSudo().ifPresent(v -> props.put("ssh_sudo", v));
        cfg.getSSHMaxProcs().ifPresent(v -> props.put("ssh_max_procs", v));
        cfg.getUser().ifPresent(v -> props.put("ssh_user", v));
        cfg.getWipe().ifPresent(v -> props.put("ssh_wipe", v));
    }
}
