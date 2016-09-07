package com.suse.salt.netapi.calls;

import java.util.Optional;

/**
 * Salt SSH configuration with a builder class.
 */
public class SaltSSHConfig {

    private final Optional<String> extraFilerefs;
    private final Optional<Boolean> identitiesOnly;
    private final Optional<Boolean> ignoreHostKeys;
    private final Optional<Boolean> keyDeploy;
    private final Optional<Boolean> noHostKeys;
    private final Optional<String> passwd;
    private final Optional<String> priv;
    private final Optional<Boolean> rawShell;
    private final Optional<Boolean> refreshCache;
    private final Optional<String> remotePortForwards;
    private final Optional<String> roster;
    private final Optional<String> rosterFile;
    private final Optional<String> scanPorts;
    private final Optional<Double> scanTimeout;
    private final Optional<Integer> sshMaxProcs;
    private final Optional<Boolean> sudo;
    private final Optional<String> user;
    private final Optional<Boolean> wipe;

    private SaltSSHConfig(Builder builder) {
        extraFilerefs = builder.extraFilerefs;
        identitiesOnly = builder.identitiesOnly;
        ignoreHostKeys = builder.ignoreHostKeys;
        keyDeploy = builder.keyDeploy;
        noHostKeys = builder.noHostKeys;
        passwd = builder.passwd;
        priv = builder.priv;
        rawShell = builder.rawShell;
        refreshCache = builder.refreshCache;
        remotePortForwards = builder.remotePortForwards;
        roster = builder.roster;
        rosterFile = builder.rosterFile;
        scanPorts = builder.scanPorts;
        scanTimeout = builder.scanTimeout;
        sshMaxProcs = builder.sshMaxProcs;
        sudo = builder.sudo;
        user = builder.user;
        wipe = builder.wipe;
    }

    public Optional<String> getExtraFilerefs() {
        return extraFilerefs;
    }

    public Optional<Boolean> getIdentitiesOnly() {
        return identitiesOnly;
    }

    public Optional<Boolean> getIgnoreHostKeys() {
        return ignoreHostKeys;
    }

    public Optional<Boolean> getKeyDeploy() {
        return keyDeploy;
    }

    public Optional<Boolean> getNoHostKeys() {
        return noHostKeys;
    }

    public Optional<String> getPasswd() {
        return passwd;
    }

    public Optional<String> getPriv() {
        return priv;
    }

    public Optional<Boolean> getRawShell() {
        return rawShell;
    }

    public Optional<Boolean> getRefreshCache() {
        return refreshCache;
    }

    public Optional<String> getRemotePortForwards() {
        return remotePortForwards;
    }

    public Optional<String> getRoster() {
        return roster;
    }

    public Optional<String> getRosterFile() {
        return rosterFile;
    }

    public Optional<String> getScanPorts() {
        return scanPorts;
    }

    public Optional<Double> getScanTimeout() {
        return scanTimeout;
    }

    public Optional<Integer> getSSHMaxProcs() {
        return sshMaxProcs;
    }

    public Optional<Boolean> getSudo() {
        return sudo;
    }

    public Optional<String> getUser() {
        return user;
    }

    public Optional<Boolean> getWipe() {
        return wipe;
    }

    /**
     * Builder class to create configurations for Salt SSH.
     */
    public static class Builder {

        private Optional<String> extraFilerefs = Optional.empty();
        private Optional<Boolean> identitiesOnly = Optional.empty();
        private Optional<Boolean> ignoreHostKeys = Optional.empty();
        private Optional<Boolean> keyDeploy = Optional.empty();
        private Optional<Boolean> noHostKeys = Optional.empty();
        private Optional<String> passwd = Optional.empty();
        private Optional<String> priv = Optional.empty();
        private Optional<Boolean> rawShell = Optional.empty();
        private Optional<Boolean> refreshCache = Optional.empty();
        private Optional<String> remotePortForwards = Optional.empty();
        private Optional<String> roster = Optional.empty();
        private Optional<String> rosterFile = Optional.empty();
        private Optional<String> scanPorts = Optional.empty();
        private Optional<Double> scanTimeout = Optional.empty();
        private Optional<Boolean> sudo = Optional.empty();
        private Optional<Integer> sshMaxProcs = Optional.empty();
        private Optional<String> user = Optional.empty();
        private Optional<Boolean> wipe = Optional.empty();

        public Builder() {
        }

        /**
         * Pass in extra files to include in the state tarball.
         *
         * @param extraFilerefs the value to set
         * @return this builder
         */
        public Builder extraFilerefs(String extraFilerefs) {
            this.extraFilerefs = Optional.of(extraFilerefs);
            return this;
        }

        /**
         * Use the only authentication identity files configured in the ssh_config files.
         * See IdentitiesOnly flag in man ssh_config.
         *
         * @param identitiesOnly the value to set
         * @return this builder
         */
        public Builder identitiesOnly(boolean identitiesOnly) {
            this.identitiesOnly = Optional.of(identitiesOnly);
            return this;
        }

        /**
         * By default ssh host keys are honored and connections will ask for approval. Use
         * this option to disable 'StrictHostKeyChecking.'
         *
         * @param ignoreHostKeys the value to set
         * @return this builder
         */
        public Builder ignoreHostKeys(boolean ignoreHostKeys) {
            this.ignoreHostKeys = Optional.of(ignoreHostKeys);
            return this;
        }

        /**
         * Set this flag to attempt to deploy the authorized ssh key with all minions. This
         * combined with --passwd can make initial deployment of keys very fast and easy.
         *
         * @param keyDeploy the value to set
         * @return this builder
         */
        public Builder keyDeploy(boolean keyDeploy) {
            this.keyDeploy = Optional.of(keyDeploy);
            return this;
        }

        /**
         * Removes all host key checking functionality from SSH session.
         *
         * @param noHostKeys the value the value to set
         * @return this builder
         */
        public Builder noHostKeys(boolean noHostKeys) {
            this.noHostKeys = Optional.of(noHostKeys);
            return this;
        }

        /**
         * Set the default password to attempt to use when authenticating.
         *
         * @param passwd the value to set
         * @return this builder
         */
        public Builder passwd(String passwd) {
            this.passwd = Optional.of(passwd);
            return this;
        }

        /**
         * SSH private key file.
         *
         * @param priv the value to set
         * @return this builder
         */
        public Builder priv(String priv) {
            this.priv = Optional.of(priv);
            return this;
        }

        /**
         * Don't execute a salt routine on the targets, execute a
         * raw shell command.
         *
         * @param rawShell if command should be execute as a shell command
         * @return this builder
         */
        public Builder rawShell(boolean rawShell) {
            this.rawShell = Optional.of(rawShell);
            return this;
        }

        /**
         * Force a refresh of the master side data cache of the target's data. This is
         * needed if a target's grains have been changed and the auto refresh timeframe has
         * not been reached.
         *
         * @param refreshCache the value to set
         * @return this builder
         */
        public Builder refreshCache(boolean refreshCache) {
            this.refreshCache = Optional.of(refreshCache);
            return this;
        }

        /**
         * Setup remote port forwarding using the same syntax as with the -R parameter of
         * ssh. A comma separated list of port forwarding definitions will be translated
         * into multiple -R parameters.
         *
         * @param remotePortForwards the value to set
         * @return this builder
         */
        public Builder remotePortForwards(String remotePortForwards) {
            this.remotePortForwards = Optional.of(remotePortForwards);
            return this;
        }

        /**
         * Define which roster system to use, this defines if a database backend, scanner,
         * or custom roster system is used. Default: 'flat'.
         *
         * @param roster the value to set
         * @return this builder
         */
        public Builder roster(String roster) {
            this.roster = Optional.of(roster);
            return this;
        }

        /**
         * Define an alternative location for the default roster file location. The default
         * roster file is called roster and is found in the same directory as the master
         * config file.
         *
         * @param rosterFile the value to set
         * @return this builder
         */
        public Builder rosterFile(String rosterFile) {
            this.rosterFile = Optional.of(rosterFile);
            return this;
        }

        /**
         * Comma-separated list of ports to scan in the scan roster.
         *
         * @param scanPorts the value to set
         * @return this builder
         */
        public Builder scanPorts(String scanPorts) {
            this.scanPorts = Optional.of(scanPorts);
            return this;
        }

        /**
         * Scanning socket timeout for the scan roster.
         *
         * @param scanTimeout the value to set
         * @return this builder
         */
        public Builder scanTimeout(Double scanTimeout) {
            this.scanTimeout = Optional.of(scanTimeout);
            return this;
        }

        /**
         * Run command via sudo.
         *
         * @param sudo the value to set
         * @return this builder
         */
        public Builder sudo(boolean sudo) {
            this.sudo = Optional.of(sudo);
            return this;
        }

        /**
         * Set the number of concurrent minions to communicate with. This value defines how
         * many processes are opened up at a time to manage connections, the more running
         * processes the faster communication should be. Default: 25.
         *
         * @param sshMaxProcs the value to set
         * @return this builder
         */
        public Builder sshMaxProcs(int sshMaxProcs) {
            this.sshMaxProcs = Optional.of(sshMaxProcs);
            return this;
        }

        /**
         * Set the default user to attempt to use when authenticating.
         *
         * @param user the value to set
         * @return this builder
         */
        public Builder user(String user) {
            this.user = Optional.of(user);
            return this;
        }

        /**
         * Remove the deployment of the salt files when done executing.
         *
         * @param wipe the value to set
         * @return this builder
         */
        public Builder wipe(boolean wipe) {
            this.wipe = Optional.of(wipe);
            return this;
        }

        /**
         * Build the configuration object.
         *
         * @return the config object
         */
        public SaltSSHConfig build() {
            return new SaltSSHConfig(this);
        }
    }
}
