import ru.andrey.AfterMethodAspect;
import ru.andrey.BeforeMethodAspect;
import ru.andrey.ExceptionMethodAspect;
import ru.andrey.InsteadMethodAspect;
import ru.andrey.defaultvalue.PrimitiveDefaultTypeResolver;
import ru.andrey.defaultvalue.ReferenceDefaultTypeResolver;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final ExceptionMethodAspect exceptionMethodAspect = new ExceptionMethodAspect(
            new PrimitiveDefaultTypeResolver(),
            new ReferenceDefaultTypeResolver()
    );

    private static final BeforeMethodAspect beforeMethodAspect = new BeforeMethodAspect();
    private static final AfterMethodAspect afterMethodAspect = new AfterMethodAspect();
    private static final InsteadMethodAspect insteadMethodAspect = new InsteadMethodAspect();

    public static void main(String[] args) {
        multipleAspects();
        doInstead();
    }

    private static void doInstead() {
        List<String> strings = new ArrayList<>();

        strings.add("Foo");

        List<String> fakeBehaviour = insteadMethodAspect.doInstead(strings,
                (m, a) -> {
                    if (m.getName().equals("size")) {
                        return 100;
                    }
                    if (m.getName().equals("get")) {
                        return null;
                    }
                    return m.invoke(null, a);
                });

        System.out.println(fakeBehaviour.size());
        System.out.println(fakeBehaviour.get(0));
    }

    private static void multipleAspects() {
        List<String> strings = new ArrayList<>();

        strings.add("Foo");
        strings.add("Bar");
        strings.add("Baz");
        strings.add("andrey");

        strings = exceptionMethodAspect.doOnException(strings, NullPointerException.class,
                () -> System.out.println("npe occurred"));

        strings = exceptionMethodAspect.doOnException(strings, IndexOutOfBoundsException.class,
                () -> System.out.println("IndexOutOfBoundsException"));

        strings = beforeMethodAspect.doBefore(strings,
                (m) -> System.out.println("******* method " + m.getName() + " called ******"));

        strings = afterMethodAspect.doAfter(strings,
                () -> System.out.println("********** method done ******"));

        strings.set(10, "it is out of bounds");
        strings.addAll(null);

        System.out.println(strings.get(2));
        System.out.println(strings);
    }
}
