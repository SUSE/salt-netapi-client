package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackTokenResult;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.InputStream;

/**
 * Parses the result of the /login endpoint
 */
public class SaltStackTokenParser extends SaltStackParser<SaltStackTokenResult> {

    @Override
    public SaltStackTokenResult parse(InputStream inputStream) throws SaltStackParsingException {
        return this.parseSimple(SaltStackTokenResult.class, inputStream);
    }
}
