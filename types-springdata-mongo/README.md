# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Types-SpringData-Mongo

This library focuses purely on providing [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb) persistence support for the **types** defined in the
Essentials `types` library.

To use `Types-SpringData-Mongo` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-springdata-mongo</artifactId>
    <version>0.7.5</version>
</dependency>
```

**NOTE:**
**This library is WORK-IN-PROGRESS**

### Configuration

All you need to do is to register the following Spring Beans to your Spring configuration to support
Types conversions and automatic Id generation.

Example:

```
@Bean
public SingleValueTypeRandomIdGenerator registerIdGenerator() {
    return new SingleValueTypeRandomIdGenerator();
}

@Bean
public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(List.of(new SingleValueTypeConverter()));
}
```

Additionally, to support this Entity that uses a strongly types Map key, that can contain an `ObjectId#toString` as value, such as the `ProductId` key type in the `orderLines` property defined in the 
`Order`:

```
@Document
public class Order {
    @Id
    public OrderId id;
    public CustomerId customerId;
    public AccountId accountId;
    public Map<ProductId, Quantity> orderLines;
    
    ...
}    
```

and where `ProductId` is defined as (note the `random` method uses `new ProductId(ObjectId.get().toString())`):
```
public class ProductId extends CharSequenceType<ProductId> implements Identifier {
    public ProductId(CharSequence value) {
        super(value);
    }

    public static ProductId of(CharSequence value) {
        return new ProductId(value);
    }

    public static ProductId random() {
        return new ProductId(ObjectId.get().toString());
    }
}
```

Then you additionally need to explicit define that `ProductId` must be convertable to and from `ObjectId` when configuring the `MongoCustomConversions`:

```
@Bean
public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(List.of(
            new SingleValueTypeConverter(ProductId.class)));
}
```