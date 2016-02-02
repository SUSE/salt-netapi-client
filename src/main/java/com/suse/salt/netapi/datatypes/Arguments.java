package com.suse.salt.netapi.datatypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of args and kwargs.
 */
public class Arguments {

    private final List<Object> args = new ArrayList<>();
    private final Map<String, Object> kwargs = new LinkedHashMap<>();

    public List<Object> getArgs() {
        return args;
    }

    public Map<String, Object> getKwargs() {
        return kwargs;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "args=" + args +
                ", kwargs=" + kwargs +
                '}';
    }
}
