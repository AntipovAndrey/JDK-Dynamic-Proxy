package ru.andrey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;


class BeforeMethodAspectTest {

    private BeforeMethodAspect methodAspect;

    @Spy
    private Runnable action;

    @Spy
    private Consumer<Method> methodConsumer;

    @Spy
    private BiConsumer<Method, Object[]> methodAndArgsConsumer;

    @Spy
    private List<String> target;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        methodAspect = new BeforeMethodAspect();
    }

    @Test
    void test_action_was_invoked_before_the_method_call() {
        List<String> proxy = methodAspect.doBefore(target, action);

        proxy.size();

        InOrder order = Mockito.inOrder(action, target);
        order.verify(action).run();
        order.verify(target).size();
    }

    @Test
    void test_action_was_invoked_with_the_same_method_passed() throws Exception {
        List<String> proxy = methodAspect.doBefore(target, methodConsumer);

        proxy.size();

        ArgumentCaptor<Method> methodCaptor = ArgumentCaptor.forClass(Method.class);

        verify(methodConsumer).accept(methodCaptor.capture());
        assertThat("size", is(methodCaptor.getValue().getName()));
    }

    @Test
    void test_action_was_invoked_with_the_same_method_and_args_passed() throws Exception {
        List<String> proxy = methodAspect.doBefore(target, methodAndArgsConsumer);

        proxy.add("42");

        ArgumentCaptor<Method> methodCaptor = ArgumentCaptor.forClass(Method.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(methodAndArgsConsumer).accept(methodCaptor.capture(), argsCaptor.capture());

        assertThat("add", is(methodCaptor.getValue().getName()));
        assertThat("42", is(argsCaptor.getValue()[0]));
    }
}
