package com.suse.salt.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

/**
 * Adapt a homogeneous collection of objects.
 */
public class CollectionTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public CollectionTypeAdapterFactory() {
        this.constructorConstructor = new ConstructorConstructor(new HashMap<>());
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();

        Class<? super T> rawType = typeToken.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) {
            return null;
        }
        Type elementType = $Gson$Types.getCollectionElementType(type, rawType);

        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
        ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

        TypeAdapter<T> result = new Adapter(elementTypeAdapter, constructor);
        return result;
    }

    /**
     * Adapter for the Collections.
     * @param <E> The type of elements in the collection
     */
    private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
        private final TypeAdapter<E> elementTypeAdapter;
        private final ObjectConstructor<? extends Collection<E>> constructor;

        public Adapter(TypeAdapter<E> elementAdapter, ObjectConstructor<? extends
                Collection<E>> constructor) {
            this.constructor = constructor;
            this.elementTypeAdapter = elementAdapter;
        }

        public Collection<E> read(JsonReader in) throws IOException {
            Collection<E> collection = constructor.construct();
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
