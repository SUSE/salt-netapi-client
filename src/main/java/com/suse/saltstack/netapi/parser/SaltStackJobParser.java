package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackJobResult;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.InputStream;

/**
 * Parses a SaltStackJbResult.
 */
public class SaltStackJobParser extends SaltStackParser<SaltStackJobResult> {
    @Override
    public SaltStackJobResult parse(InputStream inputStream) throws SaltStackParsingException {
        return this.parseSimple(SaltStackJobResult.class, inputStream);
    }
}
