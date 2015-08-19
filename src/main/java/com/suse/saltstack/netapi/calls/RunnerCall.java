package com.suse.saltstack.netapi.calls;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class representing a function call of a salt runner module.
 *
 * @param <R> the return type of the called function
 */
public class RunnerCall<R> implements Call<R> {

    private final String functionName;
    private final Optional<Map<String, Object>> kwargs;
    private final TypeToken<R> returnType;

    public RunnerCall(String functionName, Optional<Map<String, Object>> kwargs,
            TypeToken<R> returnType) {
        this.functionName = functionName;
        this.kwargs = kwargs;
        this.returnType = returnType;
    }

    public TypeToken<R> getReturnType() {
        return returnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> payload() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("fun", functionName);
        kwargs.ifPresent(kwargs -> payload.put("kwargs", kwargs));
        return payload;
    }
}
