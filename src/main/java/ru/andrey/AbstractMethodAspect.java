package ru.andrey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class AbstractMethodAspect {

    protected <T> ProxyCreator<T> proxyOf(T original) {
        Class<?> originalClass = original.getClass();
        Class<?>[] interfaces = getInterfaces(originalClass);
        assertHasInterface(interfaces);
        return handler -> (T) Proxy.newProxyInstance(originalClass.getClassLoader(),
                getInterfaces(originalClass),
                handler);
    }

    private Class<?>[] getInterfaces(Class<?> originalClass) {
        List<Class<?>[]> interfaces = new ArrayList<>();

        do {
            interfaces.add(originalClass.getInterfaces());
            originalClass = originalClass.getSuperclass();
        } while (originalClass != null);

        return interfaces.stream()
                .flatMap(Stream::of)
                .distinct()
                .toArray(Class<?>[]::new);
    }

    private void assertHasInterface(Class<?>[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Original object implements no interface");
        }
    }


    public <T> T fromHandler(T object, InvocationHandler handler) {
        return proxyOf(object)
                .withHandler(handler);
    }

    @FunctionalInterface
    protected interface ProxyCreator<R> {

        R withHandler(InvocationHandler handler);
    }
}
