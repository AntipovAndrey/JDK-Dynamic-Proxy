#JDK Dynamic Proxy
Simple AOP implementation based on the java.lang.reflect.Proxy class <br/>
Supports enhancement for objects whose classes implementing at least one interface

It supports the following set of enhancement:
+ After a method invocation
+ Before a method invocation
+ Instead of a method invocation with substitution of the return value
+ On a given exception thrown during a method execution. In this case a default value of the method's return type will be returned

##Samples
#### doBefore 
```java
BeforeMethodAspect beforeMethodAspect = new BeforeMethodAspect();

List<String> target = new ArrayList<>();

target = beforeMethodAspect.doBefore(target, () -> {
      System.out.println("[a method was called]");
});

target.add("hello");
System.out.println(target.size());
```
Result:
```
[a method was called]
[a method was called]
1
```
#### doAfter
```java
AfterMethodAspect afterMethodAspect = new AfterMethodAspect();
List<String> target = new ArrayList<>();

target = afterMethodAspect.doAfter(target, method -> {
    System.out.println("[" + method.getName() + " was called]");
});

target.add("hello");
System.out.println(target.size());
```
Result:
```
[add was called]
[size was called]
1
```
#### doInstead
```java
InsteadMethodAspect insteadMethodAspect = new InsteadMethodAspect();

List<String> target = new ArrayList<>();

target = insteadMethodAspect.doInstead(target, (method, args) -> {
    if (method.getName().equals("size")) {
        return 42;
    }
    throw new SecurityException("you are not permitted to invoke " + method.getName());
});

System.out.println(target.size());
System.out.println(target.hashCode());

```
Result:
```
42
Exception in thread "main" java.lang.SecurityException: you are not permitted to invoke hashCode
	at Main.lambda$test$0(Main.java:37)
	at ru.andrey.InsteadMethodAspect.lambda$doInstead$2(InsteadMethodAspect.java:23)
	at com.sun.proxy.$Proxy0.hashCode(Unknown Source)
	at Main.test(Main.java:41)
	at Main.main(Main.java:25)
```
#### doOnException
```java
ExceptionMethodAspect exceptionMethodAspect = ...;

List<String> target = new ArrayList<>();

target = exceptionMethodAspect.doOnException(target, IndexOutOfBoundsException.class,
        () -> System.out.println("caught!"));

System.out.println(target.get(42));
```
Result:
```
caught!
null
```
### Combining
You can also combine aspects as you wish
```java
List<String> strings = createStringList();

strings.add("Foo");
strings.add("Bar");
strings.add("Baz");
strings.add("andrey");

strings = exceptionMethodAspect.doOnException(strings, IndexOutOfBoundsException.class,
            () -> System.out.println("IndexOutOfBoundsException intercepted"));

strings = beforeMethodAspect.doBefore(strings,
            (m) -> System.out.println(" method " + m.getName() + " called "));

strings = beforeMethodAspect.doBefore(strings,
            () -> System.out.println("[ Starting an invocation ]"));

strings = afterMethodAspect.doAfter(strings,
            (m, args) -> System.out.println("[ Method done with args " + Arrays.toString(args) +  " ]"));

strings.set(10, "it is out of bounds");

System.out.println(strings.get(2));

System.out.println(strings);
```
Result:
```
[ Starting an invocation ]
 method set called 
IndexOutOfBoundsException intercepted
[ Method done with args [10, it is out of bounds] ]
[ Starting an invocation ]
 method get called 
[ Method done with args [2] ]
baz
[ Starting an invocation ]
 method toString called 
[ Method done with args null ]
[foo, bar, baz, foobar, Foo, Bar, Baz, andrey]
```