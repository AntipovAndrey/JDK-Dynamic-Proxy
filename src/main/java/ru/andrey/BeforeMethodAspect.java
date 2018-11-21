package ru.andrey;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Performs an action before any method invocation
 */
public class BeforeMethodAspect extends AbstractMethodAspect {

    public <T> T doBefore(T object, Runnable action) {
        return doBefore(object, args -> action.run());
    }

    public <T> T doBefore(T object, Consumer<Method> action) {
        return doBefore(object, (method, args) -> action.accept(method));
    }

    public <T> T doBefore(T object, BiConsumer<Method, Object[]> action) {
        return proxyOf(object)
                .withHandler((proxy, method, args) -> {
                    action.accept(method, args);
                    return method.invoke(object, args);
                });
    }
}
