package com.suse.salt.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Adapt a homogeneous collection of objects.
 */
public class CollectionTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();

        Class<? super T> rawType = typeToken.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) {
            return null;
        }
        Type elementType = collectionElementType(type);

        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
        Supplier<Collection<?>> constructor = collectionSupplier(rawType);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        TypeAdapter<T> result = new Adapter(elementTypeAdapter, constructor);
        return result;
    }

    /**
     * Returns the element type of a Collection parameterized type.
     * Handles wildcard upper bounds (e.g. {@code Collection<? extends Foo>}).
     */
    private static Type collectionElementType(Type type) {
        if (type instanceof ParameterizedType) {
            Type arg = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (arg instanceof WildcardType) {
                return ((WildcardType) arg).getUpperBounds()[0];
            }
            return arg;
        }
        return Object.class;
    }

    /**
     * Returns a {@link Supplier} that creates a suitable mutable {@link Collection}
     * instance for the given raw collection type.
     */
    private static Supplier<Collection<?>> collectionSupplier(Class<?> rawType) {
        if (SortedSet.class.isAssignableFrom(rawType)) {
            return TreeSet::new;
        } else if (Set.class.isAssignableFrom(rawType)) {
            return LinkedHashSet::new;
        } else if (Queue.class.isAssignableFrom(rawType)) {
            return ArrayDeque::new;
        } else {
            return ArrayList::new;
        }
    }

    /**
     * Adapter for the Collections.
     * @param <E> The type of elements in the collection
     */
    private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
        private final TypeAdapter<E> elementTypeAdapter;
        private final Supplier<Collection<E>> constructor;

        @SuppressWarnings("unchecked")
        public Adapter(TypeAdapter<E> elementAdapter, Supplier<?> constructor) {
            this.constructor = (Supplier<Collection<E>>) constructor;
            this.elementTypeAdapter = elementAdapter;
        }

        public Collection<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                throw new JsonParseException("null is not a valid value for a Collection");
            }

            Collection<E> collection = constructor.get();
            in.beginArray();
            while (in.hasNext()) {
                E instance = elementTypeAdapter.read(in);
                collection.add(instance);
            }
            in.endArray();
            return collection;
        }

        public void write(JsonWriter out, Collection<E> collection) throws IOException {
            if (collection == null) {
                throw new JsonParseException("null is not a valid value for an array");
            } else {
                out.beginArray();
                for (E element : collection) {
                    elementTypeAdapter.write(out, element);
                }
                out.endArray();
            }
        }
    }
}
