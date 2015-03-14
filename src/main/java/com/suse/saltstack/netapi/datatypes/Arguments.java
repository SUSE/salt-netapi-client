package com.suse.saltstack.netapi.datatypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of args and kwargs.
 */
public class Arguments {

    private List<Object> args;
    private Map<String, Object> kwargs;

    public Arguments() {
        args = new ArrayList<>();
        kwargs = new LinkedHashMap<>();
    }

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
