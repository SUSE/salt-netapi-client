package com.suse.salt.netapi.results;

import java.util.function.Function;

/**
 * Interface for all salt related errors
 */
public interface SaltError {

    <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<GenericSaltError, ? extends T> generic);

}

