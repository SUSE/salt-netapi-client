package com.suse.salt.netapi.utils;

import java.lang.reflect.Type;

/** Implementation of Java's ParametrizedType for internal use */
public class ParameterizedTypeImpl implements java.lang.reflect.ParameterizedType {

    private final Type owner;
    private final Type raw;
    private final Type[] argumentsTypes;

    /**
     * Construct a parametrized type
     *
     * @param ownerIn the ownerIn type
     * @param rawTypeIn the raw type
     * @param argumentsTypesIn the arguments actual types
     */
    public ParameterizedTypeImpl(Type ownerIn, Type rawTypeIn, Type[] argumentsTypesIn) {
        owner = ownerIn;
        raw = rawTypeIn;
        argumentsTypes = argumentsTypesIn;
    }

    /**
     * Construct a parametrized type for a single argument
     *
     * @param ownerIn the ownerIn type
     * @param rawTypeIn the raw type
     * @param argumentTypeIn the argument actual types
     */
    public ParameterizedTypeImpl(Type ownerIn, Type rawTypeIn, Type argumentTypeIn) {
        this(ownerIn, rawTypeIn, new Type[]{argumentTypeIn});
    }

    @Override
    public Type[] getActualTypeArguments() {
        return argumentsTypes;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }
}
