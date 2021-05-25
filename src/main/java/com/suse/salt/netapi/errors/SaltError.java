package com.suse.salt.netapi.errors;

import java.util.function.Function;

/**
 * Interface for all salt related errors
 */
public interface SaltError {

    <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<JsonParsingError, ? extends T> jsonError,
            Function<GenericError, ? extends T> generic,
            Function<SaltSSHError, ? extends T> saltSSHError);

}

