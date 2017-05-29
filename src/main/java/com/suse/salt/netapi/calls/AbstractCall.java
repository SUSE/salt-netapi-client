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
    private final String functionName;
    private final TypeToken<R> returnType;

    AbstractCall(String functionName, TypeToken<R> returnType) {
        this.moduleName = ClientUtils.getModuleNameFromFunction(functionName);
        this.functionName = functionName;
        this.returnType = returnType;
    }

    AbstractCall(String moduleName, String functionName, TypeToken<R> returnType) {
        this.moduleName = moduleName;
        this.functionName = functionName;
        this.returnType = returnType;
    }

    /**
     * Returns the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Return the function name
     * @return functionName
     */
    String getFunctionName() {
        return functionName;
    }

    /**
     * Reurn the type
     * @return returnType
     */
    public TypeToken<R> getReturnType() {
        return returnType;
    }


}
