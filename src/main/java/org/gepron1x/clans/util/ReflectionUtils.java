package org.gepron1x.clans.util;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public final class ReflectionUtils {
    private ReflectionUtils() {}

    public static  <T> List<T> getConstants(Class<?> target, Class<T> valueType) {
        List<T> values = new ArrayList<>();
        for(Field field : target.getFields()) {
            int modifiers = field.getModifiers();
            if(!(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))) continue;
            if(!field.getType().equals(valueType)) continue;
            values.add(valueType.cast(getStaticValue0(field)));
        }
        return values;
    }

    public static Object getValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getStaticValue(Field field) {
        Preconditions.checkArgument(Modifier.isStatic(field.getModifiers()), "field is not static");
        return getStaticValue0(field);
    }
    private static Object getStaticValue0(Field field) {
        return getValue(field, null);
    }
    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static Class<?> findClassFrom(String... names) {
        for(String name : names) {
            Class<?> clazz = findClass(name);
            if(clazz != null) return clazz;
        }
        return null;
    }


    @SafeVarargs
    public static <T> boolean equals(T obj, Object other, Function<T, ?>... fields) {
        return equals(obj, other, Arrays.asList(fields));

    }
    @SuppressWarnings("unchecked")
    public static <T> boolean equals(T obj, Object other, Collection<Function<T, ?>> fields) {
        if (obj == other) return true;
        if (other == null || obj.getClass() != other.getClass()) return false;
        T o = (T) other;
        boolean eq = true;
        for(Function<T, ?> function : fields) {
            if(!Objects.equals(function.apply(obj), function.apply(o))) {
                eq = false;
                break;
            }
        }
        return eq;

    }
    public static boolean equals(Object fst, Object snd) {
        List<Function<Object, ?>> fields = new ArrayList<>();
        for(Field field : fst.getClass().getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;
            fields.add(obj -> getValue(field, obj));
        }
        return equals(fst, snd, fields);
    }
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }
    @SuppressWarnings("unchecked")
    public static <T> Class<T> typeToken() {
        return (Class<T>) new TypeToken<T>(){}.getRawType();
    }







}
