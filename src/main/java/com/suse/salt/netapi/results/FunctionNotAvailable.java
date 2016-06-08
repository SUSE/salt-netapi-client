package com.suse.salt.netapi.results;

import java.util.function.Function;

/**
 * Salt error when trying to execute a function that does not exist
 */
final public class FunctionNotAvailable implements SaltError {

    private final String functionName;

    public FunctionNotAvailable(String fn) {
        this.functionName = fn;
    }

    public String getFunctionName() {
        return functionName;
    }

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
               Function<ModuleNotSupported, ? extends T> modNotSupported,
               Function<GenericSaltError, ? extends T> generic) {
        return fnNotAvail.apply(this);
    }

    @Override
    public String toString() {
        return "FunctionNotAvailable(" + functionName + ")";
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
            return obj instanceof FunctionNotAvailable &&
                   ((FunctionNotAvailable) obj).getFunctionName()
                         .contentEquals(getFunctionName());
        }
    }
}
