package com.suse.salt.netapi.utils;

import com.suse.salt.netapi.errors.FunctionNotAvailable;
import com.suse.salt.netapi.errors.ModuleNotSupported;
import com.suse.salt.netapi.errors.SaltError;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for deriving {@link SaltError} based on the salt text output.
 */
public class SaltErrorUtils {

    private static final Pattern FN_UNAVAILABLE =
            Pattern.compile("'([^']+)' is not available.");
    private static final Pattern MODULE_NOT_SUPPORTED =
            Pattern.compile("'([^']+)' __virtual__ returned False");

    /**
     * Based on the salt text output, derive particular {@link SaltError}.
     * @param saltOutput salt output
     * @return salt error corresponding to given string
     */
    public static Optional<SaltError> deriveError(String saltOutput) {
        Matcher fnuMatcher = FN_UNAVAILABLE.matcher(saltOutput);
        Matcher mnsMatcher = MODULE_NOT_SUPPORTED.matcher(saltOutput);
        if (fnuMatcher.find()) {
            String fn = fnuMatcher.group(1);
            return Optional.of(new FunctionNotAvailable(fn));
        } else if (mnsMatcher.find()) {
            String module = mnsMatcher.group(1);
            return Optional.of(new ModuleNotSupported(module));
        }
        return Optional.empty();
    }

}
