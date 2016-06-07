package com.suse.salt.netapi.results;

/**
 * Salt error containing a stacktrace if one is returned instead of a result
 */
final public class StackTraceError implements SaltError {

    private final String stacktrace;

    public StackTraceError(String fn) {
        this.stacktrace = fn;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    public String toString() {
        return "StackTraceError(" + stacktrace + ")";
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
            return obj instanceof StackTraceError &&
                    ((StackTraceError) obj).getStacktrace().contentEquals(getStacktrace());
        }
    }
}
