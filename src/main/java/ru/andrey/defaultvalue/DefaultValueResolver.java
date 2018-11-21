package ru.andrey.defaultvalue;

/**
 * Resolves a default value for a give type
 */
public interface DefaultValueResolver {

    Object getDefault(Class<?> clazz);
}
