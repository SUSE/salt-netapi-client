package com.suse.salt.netapi.calls;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.utils.ClientUtils;

/**
 * Abstract class for all function calls in salt.
 *
 * @param <R> the return type of the called function
 */
public abstract class AbstractCall<R> implements Call<R> {

    private final String moduleName;
    private final String function;
    private final TypeToken<R> returnType;

    /**
     * Default constructor.
     *
     * @param function string containing module and function name (e.g. "test.ping")
     * @param returnType the return type of this call
     */
    AbstractCall(String function, TypeToken<R> returnType) {
        this.moduleName = ClientUtils.getModuleNameFromFunction(function);
        this.function = function;
        this.returnType = returnType;
    }

    /**
     * Return the module name.
     *
     * @return moduleName the module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Return the function string containing module and function name (e.g. "test.ping").
     *
     * @return function string containing module and function name (e.g. "test.ping")
     */
    String getFunction() {
        return function;
    }

    /**
     * Return the return type of this call.
     *
     * @return returnType the return type of this call
     */
    public TypeToken<R> getReturnType() {
        return returnType;
    }

    /**
     * Return just the function name (e.g. "ping" ).
     *
     * @return just the function name (e.g. "ping").
     */
    public String getFunctionName() {
        return ClientUtils.getFunctionNameFromFunction(function);
    }
}
