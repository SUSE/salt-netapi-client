package com.suse.salt.netapi.errors;

import java.util.function.Function;

/**
 * Error that happens if a modules is not supported
 */
final public class ModuleNotSupported implements SaltError {

    private final String moduleName;

    public ModuleNotSupported(String module) {
        this.moduleName = module;
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String toString() {
        return "ModuleNotSupported(" + moduleName + ")";
    }

    @Override
    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<JsonParsingError, ? extends T> jsonError,
            Function<GenericError, ? extends T> generic,
            Function<SaltSSHError, ? extends T> saltSSHError,
            Function<InvalidArgs, ? extends T> invalidArgs) {
        return modNotSupported.apply(this);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            return obj instanceof ModuleNotSupported &&
                  ((ModuleNotSupported) obj).getModuleName().contentEquals(getModuleName());
        }
    }
}
