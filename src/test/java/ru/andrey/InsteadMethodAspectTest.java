package ru.andrey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class InsteadMethodAspectTest {
    private InsteadMethodAspect methodAspect;

    private Supplier<Integer> sizeSupplier;

    private Function<Method, String> fromMethod;

    private AbstractMethodAspect.BiFunction<Method, Object[], String> methodAndArgs;

    @Spy
    private List<String> target;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        methodAspect = new InsteadMethodAspect();

        sizeSupplier = () -> 42;
        fromMethod = Method::getName;
        methodAndArgs = (method, args) -> method.getName() + args.length;
    }

    @Test
    void test_action_was_invoked_instead_the_method_call() {
        List<String> proxy = methodAspect.doInstead(target, sizeSupplier);

        int fakeSize = proxy.size();

        verify(target, never()).size();
        assertThat(fakeSize, is(sizeSupplier.get()));
    }

    @Test
    void test_action_was_invoked_instead_the_method_call_and_method_was_passed() throws NoSuchMethodException {
        List<String> proxy = methodAspect.doInstead(target, fromMethod);

        String fakeString = proxy.get(100);

        verify(target, never()).get(anyInt());
        assertThat(fakeString, is(fromMethod.apply(proxy.getClass().getMethod("get", int.class))));
    }

    @Test
    void test_action_was_invoked_instead_the_method_call_and_method_was_passed_with_args() throws Exception {
        List<String> proxy = methodAspect.doInstead(target, methodAndArgs);

        String fakeString = proxy.get(100);

        String expected = methodAndArgs.apply(proxy.getClass().getMethod("get", int.class), new Object[]{100});

        verify(target, never()).get(anyInt());
        assertThat(fakeString, is(expected));
    }
}
