package com.suse.salt.netapi.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Right biased disjunction mainly based on the Xor type from scala cats library.
 * This type is used for collecting salt errors that are in the place of a normal result.
 *
 * @param <L> type of the left value
 * @param <R> type of the right value
 */
public abstract class Xor<L, R> {

    public static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    public static <L, R> Right<L, R> right(R value) {
        return new Right<>(value);
    }

    public abstract boolean isRight();

    public abstract boolean isLeft();

    public abstract <T> T fold(Function<? super L, ? extends T> mapLeft,
            Function<? super R, ? extends T> mapRight);

    public abstract <T> Xor<L, T> map(Function<? super R, ? extends T> mapper);

    public abstract <T> Xor<? super L, T> flatMap(Function<? super R,
            Xor<? super L, T>> mapper);

    public abstract Optional<L> left();

    public abstract Optional<R> right();

    public abstract R orElse(R value);

    public abstract R getOrElse(Supplier<? extends R> supplier);

    public abstract boolean exists(Predicate<R> p);

    public final Optional<R> option() {
        return right();
    }

    /**
     * Left branch of the Xor
     * @param <L> type of the left value
     * @param <R> type of the right value
     */
    public static final class Left<L, R> extends Xor<L, R> {
        private final L left;

        private Left(L left) {
            this.left = left;
        }

        public boolean isRight() {
            return false;
        }

        public boolean isLeft() {
            return true;
        }

        public Optional<L> left() {
            return Optional.of(left);
        }

        public Optional<R> right() {
            return Optional.empty();
        }

        public <T> Xor<L, T> map(Function<? super R, ? extends T> mapper) {
            return left(left);
        }

        public <T> Xor<? super L, T> flatMap(Function<? super R,
                Xor<? super L, T>> mapper) {
            return left(left);
        }

        public <T> Xor<T, R> leftMap(Function<? super L, Xor<T, R>> mapper) {
            return mapper.apply(left);
        }

        public <T> T fold(Function<? super L, ? extends T> mapLeft,
                Function<? super R, ? extends T> mapRight) {
            return mapLeft.apply(left);
        }

        public boolean exists(Predicate<R> p) {
            return false;
        }

        public R orElse(R value) {
            return value;
        }

        public R getOrElse(Supplier<? extends R> supplier) {
            return supplier.get();
        }

        @Override
        public String toString() {
            return "Left(" + left.toString() + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            } else {
                return Objects.equals(left, ((Left<?, ?>) obj).left);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash("Left", left);
        }
    }

    /**
     * Right branch of the Xor
     * @param <L> type of the left value
     * @param <R> type of the right value
     */
    public static final class Right<L, R> extends Xor<L, R> {
        private final R right;

        private Right(R right) {
            this.right = right;
        }

        public boolean isRight() {
            return true;
        }

        public boolean isLeft() {
            return false;
        }

        public Optional<L> left() {
            return Optional.empty();
        }

        public Optional<R> right() {
            return Optional.of(right);
        }

        public <T> Xor<L, T> map(Function<? super R, ? extends T> mapper) {
            return right(mapper.apply(right));
        }

        public <T> Xor<? super L, T> flatMap(Function<? super R,
                Xor<? super L, T>> mapper) {
            return mapper.apply(right);
        }

        public <T> Xor<T, R> leftMap(Function<? super L, Xor<T, R>> mapper) {
            return right(right);
        }

        public <T> T fold(Function<? super L, ? extends T> mapLeft,
                Function<? super R, ? extends T> mapRight) {
            return mapRight.apply(right);
        }

        public boolean exists(Predicate<R> p) {
            return p.test(right);
        }

        public R orElse(R value) {
            return right;
        }

        public R getOrElse(Supplier<? extends R> supplier) {
            return right;
        }

        @Override
        public String toString() {
            return "Right(" + right.toString() + ")";
        }

        @Override
        public int hashCode() {
            return Objects.hash("Right", right);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            } else {
                return Objects.equals(right, ((Right<?, ?>) obj).right);
            }
        }
    }

}
