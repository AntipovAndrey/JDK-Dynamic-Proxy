package ru.andrey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public abstract class AbstractMethodAspect {

    protected <T> ProxyCreator<T> proxyOf(T original) {
        Class<?> originalClass = original.getClass();
        assertInterface(originalClass);
        return handler -> (T) Proxy.newProxyInstance(originalClass.getClassLoader(),
                originalClass.getInterfaces(),
                handler);
    }

    public <T> T fromHandler(T object, InvocationHandler handler) {
        return proxyOf(object)
                .withHandler(handler);
    }

    private void assertInterface(Class<?> originalClass) {
        if (originalClass.getInterfaces().length == 0) {
            throw new IllegalArgumentException("Original object implements no interface");
        }
    }

    @FunctionalInterface
    protected interface ProxyCreator<R> {

        R withHandler(InvocationHandler handler);
    }
}
