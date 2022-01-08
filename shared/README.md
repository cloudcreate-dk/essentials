# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and strongly typed code, which doesn't depend on other libraries or frameworks, but
instead allows easy integrations with many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Shared

This library contains the smallest set of supporting building blocks needed for other Essentials libraries, such as:

### Tuples

Base Java is missing a simple Tuple library and while there are some excellent Functional libraries for Java, such as VAVR, adding a dependency on these goes against the Essentials philosophy, so
instead we provide the minimum in terms of Tuples support.

We offer two different flavors of Tuples:

- The normal `dk.cloudcreate.essentials.shared.functional.tuple.Tuple` that allows elements of any types
- The `dk.cloudcreate.essentials.shared.functional.tuple.comparable.ComparableTuple` that only allows elements that implement the Comparable interface

Example of using Tuples:

```
Triple<String, Long, BigDecimal> tuple = Tuple.of("Hello", 100L, new BigDecimal("125.95"));
var element1 = tuple._1;
var element2 = tuple._2;
var element3 = tuple._3;

var elements = tuple.toList();

Triple<String, String, String> stringTuple  = tuple.map((_1, _2, _3) -> Tuple.of(_1.toString(), _2.toString(), _3.toString));
Triple<String, String, String> stringTuple2 = tuple.map(Object::toString, Object::toString, Object::toString);

```

### Collections

Different utility functions for working with Collections, such as

`Stream<Pair<Integer, String>> indexedStream = Lists.toIndexedStream(List.of("A", "B", "C"));`

### Functional interfaces

Apart from Tuples, then the `dk.cloudcreate.essentials.shared.functional` package also contain reusable functional interfaces, such as the `TripleFunction`, which is used in the definition of
the `Triple` tuple's map function:

```
public <R1, R2, R3> Triple<R1, R2, R3> map(TripleFunction<? super T1, ? super T2, ? super T3, Triple<R1, R2, R3>> mappingFunction) {
   return mappingFunction.apply(_1, _2, _3);
}
```

or the Checked variant of the classic Functional-Interfaces (`Runnable`, `Consumer`, `Supplier`, `Function`, `BiFunction` and `TripleFunction`)
that behaves like the normal Functional-Interface, but which allows checked `Exception`'s to be thrown from their method:

- `CheckedRunnable`
- `CheckedConsumer`
- `CheckedSupplier`
- `CheckedFunction`
- `CheckedBiFunction`
- `CheckedTripleFunction`

#### Checked `CheckedRunnable` usage example:

Let's say we have a method called `someOperation` that cannot change, but which accepts a `Runnable` with the purpose of the calling `Runnable.run()`.

```
public void someOperation(Runnable operation) {        
    // ... Logic ...           
    operation.run();           
    // ... More logic ---   
}
```

The problem with `Runnable.run()` occurs when a `Runnable` lambda/instance calls an API that throws a checked `Exception`, e.g. the `java.io.File` API.  
Since `Runnable.run()` doesn't define that it throws any `Exception`'s we're forced to add a `try/catch` to handle the `java.io.IOException`
for the code to compile:

```
someOperation(() -> {                       
    try {                           
        // Logic that uses the File API                       
    } catch (IOException e) {                           
        throw new RuntimeException(e);                       
    }                 
}));
```

This is where the `CheckedRunnable` comes to the aid as its `run()` method defines that it throws a Checked `Exception` and its `safe(CheckedRunnable)` method will return a new `Runnable` instance
with a `Runnable.run()` method that ensures that the `run()` method is called and any checked `Exception`'s thrown will be caught and rethrown as a `CheckedExceptionRethrownException`:

```
someOperation(CheckedRunnable.safe(() -> {                       
     // Logic that uses the File API that throws IOException                 
}));
```

### FailFast argument validation (replacements for Objects.requireNonNull)

The `Objects.requireNonNull()` function is nice to have, but it's only limited to checking for null arguments, and it throws a `NullPointerException` which can be misleading.

This is where the `FailFast` class comes in as it supports many more assertion methods which all throw a `IllegalArgumentException` if the argument doesn't pass the assertion:

- `requireMustBeInstanceOf`
- `requireNonBlank`
- `requireTrue`
- `requireFalse`
- `requireNonEmpty`

``` 
public static Optional<Field> findField(Set<Field> fields,
                                        String fieldName,
                                        Class<?> fieldType) {
    FailFast.requireNonNull(fields, "You must supply a fields set");
    FailFast.requireNonBlank(fieldName, "You must supply a fieldName");
    FailFast.requireNonNull(fieldType, "You must supply a fieldType");
    
    return fields.stream()
                 .filter(field -> field.getName().equals(fieldName))
                 .filter(field -> field.getType().equals(fieldType))
                 .findFirst();
}
```

### Message formatter that aligns with SLF4J logging messages

Java already provides the `String.format()` method, but switching between it and SLF4J log messages, such as `log.debug("Found {} customers", customers.size());`, doesn't create as coherent code as
some want.

For these cases the `MessageFormatter` provides the simple static `msg()` method which supports the positional SLF4J placeholders `{}`.

`msg` is often used when constructing messages for Exceptions:

```
throw new ReflectionException(msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes)));
```

For situations, such as translation, where the arguments are known, but the order of them depends on the actual language text, `MessageFormatter` provides the static `bind()` method, which allows you
to use named placeholders:

Example:

```
var danishText  = "Kære {:firstName} {:lastName}";
var mergedDanishText = MessageFormatter.bind(danishText,
                                             arg("firstName", "John"),
                                             arg("lastName", "Doe"));

assertThat(mergedDanishText).isEqualTo("Kære John Doe");
```

Example 2:

```
var englishText = "Dear {:lastName}, {:firstName}";
var mergedEnglishText = MessageFormatter.bind(englishText,
                                              Map.of("firstName", "John",
                                                     "lastName", "Doe"));
                                                     
assertThat(mergedEnglishText).isEqualTo("Dear Doe, John");
```

### `GenericType` for capturing a generic/parameterized argument type

Using this class makes it possible to capture a generic/parameterized argument type ,such as `List<Money>`, instead of having to rely on the classical `.class` construct.   
When you specify a type reference using `.class` you loose any Generic/Parameterized information, as you cannot write `List<Money>.class`, only `List.class`.

With `GenericType` you can specify and capture parameterized type information:  
`var genericType = new GenericType<List<Money>>(){};` 

where genericType.getType() will return `List.class`  
and genericType#getGenericType() will return `ParameterizedType`, which can be introspected further

### `StopWatch` for timing different methods/operations

```
Duration duration = StopWatch.time(CheckedRunnable.safe(() -> someMethodCall()));
```

or operations/method that return a value

```
TimingResult<String> result = StopWatch.time(CheckedSupplier.safe(() -> return someMethodCall()));
Duration duration = result.getDuration();
String result = result.getResult();
```

### `Exceptions`

That support `sneakyThrow` (use with caution) as well as getting a stacktrace as a String.

```
try {
    var duration = time(CheckedRunnable.safe(() -> methodPatternMatcher.invokeMethod(methodToInvoke, argument, invokeMethodsOn, resolvedInvokeMethodWithArgumentOfType)));
} catch (CheckedExceptionRethrownException e) {
    // Unwrap the real cause and rethrow this exception
    sneakyThrow(e.getCause());
}
```

### High level Reflection package

Writing reflection can be cumbersome and there are many checked exception to handle. The `Reflector` class, and it's supporting classes
(`Accessibles`, `BoxedTypes`, `Classes`, `Constructors`, `Fields`, `Interfaces`, `Methods`), makes working with Reflection easier.

Example:

```
Class<?> concreteType = ...;
Object[] arguments = new Object[] { "Test", TestEnum.A };

var reflector = Reflector.reflectOn(concreteType);
if (reflector.hasMatchingConstructorBasedOnArguments(arguments)) {
    return reflector.newInstance(arguments);
} else {
    return reflector.invokeStatic("of", arguments);
}
```

### Reflective `PatternMatchingMethodInvoker`

Which supports creating your own reflective pattern matching method invokers.

Example using `PatternMatchingMethodInvoker` together with the provided `SingleArgumentAnnotatedMethodPatternMatcher`:

```
public class OrderEventHandler {
    private final PatternMatchingMethodInvoker patternMatchingInvoker;
    
    public OrderEventHandler() {
      patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                  new SingleArgumentAnnotatedMethodPatternMatcher<>(EventHandler.class,
                                                                                                                    OrderEvent.class),
                                                                  InvocationStrategy.InvokeMostSpecificTypeMatched);
    }
    
    public void handle(OrderEvent orderEvent) {
      // Find the single best matching method annotated with @EventHandler and invoke it based on the orderEvent argument
      patternMatchingInvoker.invoke(orderCreated);
    }

    @EventHandler
    private void orderEvent(OrderEvent orderEvent) {
      // Fallback event handler that will be called for e.g. OrderAccepted event as there isn't a method that explicitly handle this event
    }

    @EventHandler
    private void orderCreated(OrderCreated orderCreated) {
    }

    @EventHandler
    private void orderCancelled(OrderCancelled orderCancelled) {
    }
}
```