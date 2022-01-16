# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and strongly typed code, which doesn't depend on other libraries or frameworks, but
instead allows easy integrations with many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Immutable-Jackson

This library focuses purely on providing https://github.com/FasterXML/jackson deserialization support for immutable classes or other classes that don't have a suitable creator
(constructor, or no-arg static factory method, etc.).  
This is very useful for when you're using `Record`'s (in Java 14+) or other types supporting immutable objects, as it allows Jackson to create an object instance without requiring
a matching constructing.  

Property/Field values are set directly using reflection, even if the fields themselves are final.  
For this to work we require that opinionated defaults, such as **using FIELDS for serialization**, have been applied to the `ObjectMapper` instance.  
This can e.g. be accomplished by using the `EssentialsImmutableJacksonModule#createObjectMapper(Module...)` method

The object creation/instantiation logic is as follows:
- First it will try using the **standard Jackson `ValueInstantiator`**
- IF the **standard Jackson `ValueInstantiator`** cannot create a new instance of the `Class`, then we will use `Objenesis` to create an instance of the Class
    - **BE AWARE: If the object is created/instantiated using `Objenesis` then *NO constructor will be called and NO fields will have their default values set by Java*!!!!**

Using this module means that you can deserialize an immutable class such as this one that only consists of `public final` fields and a single constructor that doesn't allow initialization of
all fields values during deserialization.
``` 
public final class ImmutableOrder {
    public final OrderId       orderId;
    public final CustomerId    customerId;
    public final Money         totalPrice;
    public final LocalDateTime orderedTimestamp;

    public ImmutableOrder(OrderId orderId,
                          CustomerId customerId,
                          Money totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.orderedTimestamp = LocalDateTime.now();
    }
}
```

**NOTE:**
**This library is WORK-IN-PROGRESS**

### Configuration

All you need to do is to add the `dk.cloudcreate.essentials.jackson.immutable.EssentialsImmutableJacksonModule` to your `ObjectMapper`
configuration.

Example:

```

objectMapper.registerModule(new EssentialsImmutableJacksonModule());

```

Alternatively you can use the `EssentialsImmutableJacksonModule.createObjectMapper(Module...)` static method that creates a new
`ObjectMapper` with the `EssentialsImmutableJacksonModule` registered combined with an opinionated default configuration.

It also supports registering additional Jackson modules:

```

ObjectMapper objectMapper = EssentialsImmutableJacksonModule.createObjectMapper(new EssentialTypesJacksonModule(), new Jdk8Module(), new JavaTimeModule())

```