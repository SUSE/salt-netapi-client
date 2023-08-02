package com.suse.salt.netapi.errors;

import java.util.function.Function;

/**
 * Salt error when passing invalid arguments to a function
 */
final public class InvalidArgs implements SaltError {

    private final String functionName;
    private final String message;

    public InvalidArgs(String fn, String msg) {
        this.functionName = fn;
        this.message = msg;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<JsonParsingError, ? extends T> jsonError,
            Function<GenericError, ? extends T> generic,
            Function<SaltSSHError, ? extends T> saltSSHError,
            Function<InvalidArgs, ? extends T> invalidArgs) {
        return invalidArgs.apply(this);
    }

    @Override
    public String toString() {
        return "InvalidArgs(" + functionName + ": " + message + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InvalidArgs that = (InvalidArgs) o;

        if (!functionName.equals(that.functionName)) {
            return false;
        }
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = functionName.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
