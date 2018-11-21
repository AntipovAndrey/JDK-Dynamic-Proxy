package ru.andrey;

import ru.andrey.defaultvalue.DefaultValueResolver;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Performs an action when an instance of the exception has been thrown.
 * When an exception has been caught, method will return a value given by a DefaultValueResolver
 */
public class ExceptionMethodAspect extends AbstractMethodAspect {

    private final DefaultValueResolver primitiveResolver;
    private final DefaultValueResolver referenceResolver;

    public ExceptionMethodAspect(DefaultValueResolver primitiveResolver, DefaultValueResolver referenceResolver) {
        this.primitiveResolver = primitiveResolver;
        this.referenceResolver = referenceResolver;
    }

    public <T> T doOnAnyException(T object, Runnable action) {
        return doOnException(object, Throwable.class, action);
    }

    public <T> T doOnException(T object, Class<? extends Throwable> type, Runnable action) {
        return doOnException(object, type, args -> action.run());
    }

    public <T> T doOnException(T object, Class<? extends Throwable> type, Consumer<Method> action) {
        return doOnException(object, type, (method, args) -> action.accept(method));
    }

    public <T> T doOnException(T object, Class<? extends Throwable> type, BiConsumer<Method, Object[]> action) {
        return proxyOf(object)
                .withHandler((proxy, method, args) -> {
                    try {
                        return method.invoke(object, args);
                    } catch (Throwable e) {
                        if (type.isInstance(e.getCause())) {
                            action.accept(method, args);
                            if (method.getReturnType().isPrimitive()) {
                                return primitiveResolver.getDefault(method.getReturnType());
                            } else {
                                return referenceResolver.getDefault(method.getReturnType());
                            }
                        }
                        throw e.getCause();
                    }
                });
    }
}
