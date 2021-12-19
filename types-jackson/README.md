# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Types-Jackson

This library focuses purely on providing https://github.com/FasterXML/jackson serialization and deserialization support
for the **types** defined in the Essentials `types` library.

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
`ObjectMapper` with the `EssentialTypesJacksonModule` registered and with an opinionated default configuration.

It also supports registering additional Jackson modules:

```
ObjectMapper objectMapper = EssentialTypesJacksonModule.createObjectMapper(new Jdk8Module(), new JavaTimeModule());
```