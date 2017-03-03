package com.suse.salt.netapi.calls;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.utils.ClientUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract class for all function calls in salt.
 *
 * @param <R> the return type of the called function
 */
public abstract class AbstractCall<R> implements Call<R> {

    private final String moduleName;
    private final String functionName;
    private final TypeToken<R> returnType;
    private final Optional<Map<String, ?>> kwargs;

    AbstractCall(String functionName, Optional<Map<String, ?>> kwargs,
                 TypeToken<R> returnType) {
        this.moduleName = ClientUtils.getModuleNameFromFunction(functionName);
        this.functionName = functionName;
        this.kwargs = kwargs;
        this.returnType = returnType;
    }

    AbstractCall(String moduleName, String functionName, Optional<Map<String, ?>> kwargs,
                 TypeToken<R> returnType) {
        this.moduleName = moduleName;
        this.functionName = functionName;
        this.kwargs = kwargs;
        this.returnType = returnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fun", functionName);
        kwargs.ifPresent(payload::putAll);
        return payload;
    }

    /**
     * Returns the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Return the function na,e
     * @return functionName
     */
    String getFunctionName() {
        return functionName;
    }

    /**
     *  Return the kwargs
     * @return kwargs
     */
    Optional<Map<String, ?>> getKwargs() {
        return kwargs;
    }

    /**
     * Reurn the type
     * @return returnType
     */
    public TypeToken<R> getReturnType() {
        return returnType;
    }


}
