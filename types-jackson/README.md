# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Types-Jackson

This library focuses purely on providing [Jackson (FasterXML)](https://github.com/FasterXML/jackson) serialization and deserialization support
for the **types** defined in the Essentials `types` library.

To use `Types-Jackson` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-jackson</artifactId>
    <version>0.7.0</version>
</dependency>
```

**NOTE:**
**This library is WORK-IN-PROGRESS**

### Configuration

All you need to do is to add the `dk.cloudcreate.essentials.types.EssentialTypesJacksonModule` to your `ObjectMapper`
configuration.

Example:

```
objectMapper.registerModule(new EssentialTypesJacksonModule());
```

Alternatively you can use the `EssentialTypesJacksonModule.createObjectMapper()` static method that creates a new
`ObjectMapper` with the `EssentialTypesJacksonModule` registered combined with an opinionated default configuration.

It also supports registering additional Jackson modules:

```
ObjectMapper objectMapper = EssentialTypesJacksonModule.createObjectMapper(new EssentialsImmutableJacksonModule(), new Jdk8Module(), new JavaTimeModule());
```

### Jackson Map key deserialization
Serialization of `SingleValueType`'s works automatically for `Map` key's and value's, but to deserialize a `Map` that has a Key of type `SingleValueType`, then you need to specify a `KeyDeserializer`.

Luckily these are easy to create:
```
public class ProductIdKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return ProductId.of(key);
    }
}
```

with the `ProductIdKeyDeserializer` we can now serialize `Map`'s that specify `ProductId` as keys:
```
public class Order {
    public OrderId                  id;

    @JsonDeserialize(keyUsing = ProductIdKeyDeserializer.class)
    public Map<ProductId, Quantity> orderLines;
    
    ...
}
```