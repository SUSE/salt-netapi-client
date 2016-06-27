package com.suse.salt.netapi.calls;

import java.util.Optional;

/**
 * Salt SSH configuration with a builder class.
 */
public class SaltSSHConfig {

    private Optional<Boolean> ignoreHostKeys = Optional.empty();
    private Optional<Boolean> noHostKeys = Optional.empty();
    private Optional<String> privateKeyFile = Optional.empty();
    private Optional<String> roster = Optional.empty();
    private Optional<String> rosterFile = Optional.empty();
    private Optional<Boolean> sudo = Optional.empty();

    private SaltSSHConfig(Builder builder) {
        ignoreHostKeys = builder.ignoreHostKeys;
        noHostKeys = builder.noHostKeys;
        privateKeyFile = builder.privateKeyFile;
        roster = builder.roster;
        rosterFile = builder.rosterFile;
        sudo = builder.sudo;
    }

    public Optional<Boolean> getIgnoreHostKeys() {
        return ignoreHostKeys;
    }

    public Optional<Boolean> getNoHostKeys() {
        return noHostKeys;
    }

    public Optional<String> getPrivateKeyFile() {
        return privateKeyFile;
    }

    public Optional<String> getRoster() {
        return roster;
    }

    public Optional<String> getRosterFile() {
        return rosterFile;
    }

    public Optional<Boolean> getSudo() {
        return sudo;
    }

    /**
     * Builder class to create configurations for Salt SSH.
     */
    public static class Builder {

        private Optional<Boolean> ignoreHostKeys = Optional.empty();
        private Optional<Boolean> noHostKeys = Optional.empty();
        private Optional<String> privateKeyFile = Optional.empty();
        private Optional<String> roster = Optional.empty();
        private Optional<String> rosterFile = Optional.empty();
        private Optional<Boolean> sudo = Optional.empty();

        public Builder() {
        }

        /**
         * By default ssh host keys are honored and connections will ask for approval. Use
         * this option to disable 'StrictHostKeyChecking.'
         *
         * @param ignoreHostKeys the value
         * @return this builder
         */
        public Builder ignoreHostKeys(boolean ignoreHostKeys) {
            this.ignoreHostKeys = Optional.of(ignoreHostKeys);
            return this;
        }

        /**
         * Removes all host key checking functionality from SSH session.
         *
         * @param noHostKeys the value
         * @return this builder
         */
        public Builder noHostKeys(boolean noHostKeys) {
            this.noHostKeys = Optional.of(noHostKeys);
            return this;
        }

        /**
         * SSH private key file.
         *
         * @param privateKeyFile path to the file
         * @return this builder
         */
        public Builder privateKeyFile(String privateKeyFile) {
            this.rosterFile = Optional.of(privateKeyFile);
            return this;
        }

        /**
         * Define which roster system to use, this defines if a database backend, scanner,
         * or custom roster system is used. Default: 'flat'.
         *
         * @param roster the roster system to use
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
         * @param rosterFile the roster file location
         * @return this builder
         */
        public Builder rosterFile(String rosterFile) {
            this.rosterFile = Optional.of(rosterFile);
            return this;
        }

        /**
         * Run command via sudo.
         *
         * @param sudo the value
         * @return this builder
         */
        public Builder sudo(boolean sudo) {
            this.sudo = Optional.of(sudo);
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
