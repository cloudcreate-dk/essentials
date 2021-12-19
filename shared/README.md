# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Shared

This library contains the smallest set of supporting building blocks needed for other Essentials libraries, such as:

### Tuples

Base Java is missing a simple Tuple library and while there are some excellent Functional libraries for Java, such as
VAVR, adding a dependency on these goes against the Essentials philosophy, so instead we provide the minimum in terms of
Tuples support.

We offer two different flavors of Tuples:

- The normal `dk.cloudcreate.essentials.shared.functional.tuple.Tuple` that allows elements of any types
- The `dk.cloudcreate.essentials.shared.functional.tuple.comparable.ComparableTuple` that only allows elements that
  implement the Comparable interface

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

Apart from Tuples, then the `dk.cloudcreate.essentials.shared.functional` package also contain reusable functional
interfaces, such as the `TripleFunction`, which is used in the definition of the `Triple` tuple's map function:

```
public <R1, R2, R3> Triple<R1, R2, R3> map(TripleFunction<? super T1, ? super T2, ? super T3, Triple<R1, R2, R3>> mappingFunction) {
   return mappingFunction.apply(_1, _2, _3);
}
```

### FailFast argument validation (replacements for Objects.requireNonNull)

The `Objects.requireNonNull()` function is nice to have, but it's only limited to checking for null arguments, and it
throws a `NullPointerException` which can be misleading.

This is where the `FailFast` class comes in as it supports many more assertion methods which all throw
a `IllegalArgumentException` if the argument doesn't pass the assertion:

- `requireMustBeInstanceOf`
- `requireNonBlank`
- `requireTrue`
- `requireFalse`
- `requireNonEmpty`

Example:

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

Java already provides the `String.format()` method, but switching between it and SLF4J log messages, such
as `log.debug("Found {} customers", customers.size());`, doesn't create as coherent code as some want.

For these cases the `MessageFormatter` provides the simple static `msg()` method which supports the positional SLF4J
placeholders `{}`.

`msg` is often used when constructing messages for Exceptions:

```
throw new ReflectionException(msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes)));
```

For situations, such as translation, where the arguments are known, but the order of them depends on the actual language
text, `MessageFormatter` provides the static `bind()` method, which allows you to use named placeholders:

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

### High level Reflection package

Writing reflection can be cumbersome and there are many checked exception to handle. The `Reflector` class, and it's
supporting classes
(`Accessibles`, `BoxedTypes`, `Classes`, `Constructors`, `Fields`, `Interfaces`, `Methods`), makes working with
Reflection easier.

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
