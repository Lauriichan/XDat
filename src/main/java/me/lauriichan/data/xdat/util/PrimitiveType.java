package me.lauriichan.data.xdat.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PrimitiveType {

    BYTE(byte.class, Byte.class, Byte.BYTES),
    SHORT(short.class, Short.class, Short.BYTES),
    INTEGER(int.class, Integer.class, Integer.BYTES),
    LONG(long.class, Long.class, Long.BYTES),
    FLOAT(float.class, Float.class, Float.BYTES),
    DOUBLE(double.class, Double.class, Double.BYTES);

    private static final Map<Class<?>, PrimitiveType> TYPES;

    static {
        final HashMap<Class<?>, PrimitiveType> typeMap = new HashMap<>();
        for (PrimitiveType type : PrimitiveType.values()) {
            typeMap.put(type.primitive(), type);
            typeMap.put(type.complex(), type);
        }
        TYPES = Collections.unmodifiableMap(typeMap);
    }

    public static PrimitiveType get(Class<?> clazz) {
        return TYPES.get(clazz);
    }
    
    public static boolean is(Class<?> clazz) {
        return TYPES.containsKey(clazz);
    }

    private final Class<?> primitive, complex;
    private final int size;

    private PrimitiveType(Class<?> primitive, Class<?> complex, int size) {
        this.primitive = primitive;
        this.complex = complex;
        this.size = size;
    }

    public final Class<?> primitive() {
        return primitive;
    }

    public final Class<?> complex() {
        return complex;
    }

    public final int size() {
        return size;
    }

}