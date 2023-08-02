package com.suse.salt.netapi.utils;

import com.suse.salt.netapi.errors.InvalidArgs;
import com.suse.salt.netapi.errors.SaltError;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaltErrorUtilsTest {

    @Test
    public void testInvalidArgs() {
        String saltErrorStr =
                "Passed invalid arguments to test.ping: ping() takes 0 positional arguments but 1 was given\\n\\n"
                        + "    Used to make sure the minion is up and responding. Not an ICMP ping.\\n\\n"
                        + "    Returns ``True``.\\n\\n"
                        + "    CLI Example:\\n\\n"
                        + "    .. code-block:: bash\\n\\n"
                        + "        salt '*' test.ping\\n    ";
        Optional<SaltError> actual = SaltErrorUtils.deriveError(saltErrorStr);
        assertTrue(actual.isPresent());
        SaltError saltError = actual.get();
        assertTrue(saltError instanceof InvalidArgs);
        InvalidArgs invalidArgs = (InvalidArgs) saltError;
        assertEquals("test.ping", invalidArgs.getFunctionName());
        assertEquals("ping() takes 0 positional arguments but 1 was given", invalidArgs.getMessage());
        assertEquals("InvalidArgs(test.ping: ping() takes 0 positional arguments but 1 was given)", actual.get().toString());
    }

}