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

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<GenericSaltError, ? extends T> generic) {
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
