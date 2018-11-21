package ru.andrey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class AbstractMethodAspectTest {

    private AbstractMethodAspect methodAspect;

    @Mock
    private InvocationHandler alwaysNullInvocationHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        methodAspect = new AbstractMethodAspect() {
        };
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

        assertThat(proxiesInterfaces, hasItems(original.getClass().getInterfaces()));
    }

    @Test
    void when_original_object_implements_no_interfaces_then_throw_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodAspect.fromHandler(new Object(), alwaysNullInvocationHandler);
        });
    }
}
