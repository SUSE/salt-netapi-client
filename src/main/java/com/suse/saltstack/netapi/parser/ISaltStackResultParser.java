package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * A result parser parses the json answer returned from the salt-api.
 */
public interface ISaltStackResultParser {
    <T> T parse(Type resultType, InputStream inputStream) throws SaltStackParsingException;
}
