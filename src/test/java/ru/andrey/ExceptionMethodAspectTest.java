package ru.andrey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andrey.defaultvalue.PrimitiveDefaultTypeResolver;
import ru.andrey.defaultvalue.ReferenceDefaultTypeResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ExceptionMethodAspectTest {

    private ExceptionMethodAspect methodAspect;

    @Mock
    private Runnable action;

    @Mock
    private Consumer<Method> methodConsumer;

    private List<String> target;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        methodAspect = new ExceptionMethodAspect(new PrimitiveDefaultTypeResolver(), new ReferenceDefaultTypeResolver());
        target = new ArrayList<>();
    }

    @Test
    void test_action_was_invoked_on_any_exception() {
        List<String> proxy = methodAspect.doOnAnyException(target, action);

        proxy.set(10, "gonna fail");

        verify(action).run();
    }

    @Test
    void test_action_was_invoked_on_specific_exception() {
        List<String> proxy = methodAspect.doOnException(target, IndexOutOfBoundsException.class, action);

        proxy.set(10, "gonna fail");

        verify(action).run();
    }

    @Test
    void test_action_was_invoked_with_method_passed() {
        List<String> proxy = methodAspect.doOnException(target, IndexOutOfBoundsException.class, methodConsumer);

        proxy.set(10, "gonna fail");

        verify(methodConsumer).accept(any());
    }

    @Test
    void when_other_type_of_exception_then_throw_it() {
        List<String> proxy = methodAspect.doOnException(target, SecurityException.class, action);

        assertThrows(IndexOutOfBoundsException.class, () -> proxy.set(10, "gonna fail"));

        verify(action, never()).run();
    }
}
