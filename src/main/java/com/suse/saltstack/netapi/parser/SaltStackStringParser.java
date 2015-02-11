package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.client.SaltStackStringResult;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.InputStream;

/**
 * Parses a SaltStackStringResult.
 */
public class SaltStackStringParser extends SaltStackParser<SaltStackStringResult> {

    @Override
    public SaltStackStringResult parse(InputStream inputStream) throws SaltStackParsingException {
        return this.parseSimple(SaltStackStringResult.class, inputStream);
    }
}
