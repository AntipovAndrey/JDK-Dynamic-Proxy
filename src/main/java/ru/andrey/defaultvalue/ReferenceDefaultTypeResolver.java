package ru.andrey.defaultvalue;

public class ReferenceDefaultTypeResolver implements DefaultValueResolver {

    @Override
    public Object getDefault(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("passed class is a primitive type " + clazz.getCanonicalName());
        }
        return null;
    }
}
