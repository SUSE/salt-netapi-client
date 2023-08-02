package com.suse.salt.netapi.utils;

import com.suse.salt.netapi.errors.FunctionNotAvailable;
import com.suse.salt.netapi.errors.InvalidArgs;
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

    private static final Pattern INVALID_ARGS =
            Pattern.compile("^Passed invalid arguments to ([^:]+): (.+?)\\\\n");

    /**
     * Based on the salt text output, derive particular {@link SaltError}.
     * @param saltOutput salt output
     * @return salt error corresponding to given string
     */
    public static Optional<SaltError> deriveError(String saltOutput) {
        Matcher fnuMatcher = FN_UNAVAILABLE.matcher(saltOutput);
        Matcher mnsMatcher = MODULE_NOT_SUPPORTED.matcher(saltOutput);
        Matcher iaMatcher = INVALID_ARGS.matcher(saltOutput);
        if (fnuMatcher.find()) {
            String fn = fnuMatcher.group(1);
            return Optional.of(new FunctionNotAvailable(fn));
        } else if (mnsMatcher.find()) {
            String module = mnsMatcher.group(1);
            return Optional.of(new ModuleNotSupported(module));
        } else if (iaMatcher.find()) {
            String fn = iaMatcher.group(1);
            String msg = iaMatcher.group(2);
            return Optional.of(new InvalidArgs(fn, msg));
        }
        return Optional.empty();
    }

}
