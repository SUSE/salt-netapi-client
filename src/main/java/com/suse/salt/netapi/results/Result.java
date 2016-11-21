package com.suse.salt.netapi.results;

import com.suse.salt.netapi.errors.SaltError;
import com.suse.salt.netapi.utils.Xor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Representation of call results for a single minion implemented as a wrapper around
 * {@link Xor}.
 *
 * @param <R> the type of the internal result
 */
public class Result<R> {

    private final Xor<SaltError, R> xor;

    public static <T> Result<T> success(T r) {
        return new Result<>(Xor.right(r));
    }

    public static <T> Result<T> error(SaltError e) {
        return new Result<>(Xor.left(e));
    }

    public Result(Xor<SaltError, R> xor) {
        this.xor = xor;
    }

    public Optional<SaltError> error() {
        return xor.left();
    }

    public Optional<R> result() {
        return xor.right();
    }

    public <T> T fold(Function<? super SaltError, ? extends T> mapError,
            Function<? super R, ? extends T> mapResult) {
        return xor.fold(mapError, mapResult);
    }

    public void consume(Consumer<? super SaltError> consumerError,
            Consumer<? super R> consumerResult) {
        xor.consume(consumerError, consumerResult);
    }

    public <T> Result<T> map(Function<? super R, ? extends T> mapper) {
        return new Result<>(xor.map(mapper));
    }

    // FIXME: Redundant definition of type arguments needed for oraclejdk8
    public <T> Result<T> flatMap(Function<? super R, Result<T>> mapper) {
        return xor.fold(
            e -> new Result<T>(Xor.left(e)),
            mapper
        );
    }

    public Xor<SaltError, R> toXor() {
        return xor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Result<?> other = (Result<?>) obj;
        return xor.equals(other.xor);
    }

    @Override
    public String toString() {
        return xor.fold(
            error -> "Error(" + error + ")",
            result -> "Result(" + result + ")"
        );
    }
}
