package ru.andrey;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Performs an action instead of any method invocation.
 * Value from a given supplier or a function will be returned on a method call.
 */
public class InsteadMethodAspect extends AbstractMethodAspect {

    public <T> T doInstead(T object, Supplier<?> supplier) {
        return doInstead(object, args -> supplier.get());
    }

    public <T> T doInstead(T object, Function<Method, ?> transformer) {
        return doInstead(object, (method, args) -> transformer.apply(method));
    }

    public <T> T doInstead(T object, BiFunction<Method, Object[], ?> transformer) {
        return proxyOf(object)
                .withHandler((proxy, method, args) -> transformer.apply(method, args));
    }
}
