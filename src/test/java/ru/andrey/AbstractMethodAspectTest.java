package ru.andrey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class AbstractMethodAspectTest {

    private AbstractMethodAspect methodAspect;
    private InvocationHandler alwaysNullInvocationHandler;

    @BeforeEach
    void setUp() {
        methodAspect = new AbstractMethodAspect() {
        };

        alwaysNullInvocationHandler = Mockito.spy(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    @Test
    void test_handler_was_called() throws Throwable {
        List<String> list = methodAspect.fromHandler(new ArrayList<>(), alwaysNullInvocationHandler);

        list.get(42);

        verify(alwaysNullInvocationHandler).invoke(any(), any(), any());
    }

    @Test
    void test_proxy_has_the_same_interfaces() {
        ArrayList<?> original = new ArrayList<>();

        Object proxy = methodAspect.fromHandler(original, alwaysNullInvocationHandler);

        List<Class<?>> proxiesInterfaces = Arrays.asList(proxy.getClass().getInterfaces());

        assertThat(proxiesInterfaces, containsInAnyOrder(original.getClass().getInterfaces()));
    }

    @Test
    void when_original_object_implements_no_interfaces_then_throw_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodAspect.fromHandler(new Object(), alwaysNullInvocationHandler);
        });
    }
}
