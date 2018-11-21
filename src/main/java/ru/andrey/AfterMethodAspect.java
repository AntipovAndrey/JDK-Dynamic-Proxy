package ru.andrey;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Performs an action after any method invocation
 */
public class AfterMethodAspect extends AbstractMethodAspect {

    public <T> T doAfter(T object, Runnable action) {
        return doAfter(object, args -> action.run());
    }

    public <T> T doAfter(T object, Consumer<Method> action) {
        return doAfter(object, (method, args) -> action.accept(method));
    }

    public <T> T doAfter(T object, BiConsumer<Method, Object[]> action) {
        return proxyOf(object)
                .withHandler((proxy, method, args) -> {
                    Object result = method.invoke(object, args);
                    action.accept(method, args);
                    return result;
                });
    }
}
