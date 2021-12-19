# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

**NOTE:**
**The libraries are WORK-IN-PROGRESS**

## Shared

This library contains the smallest set of supporting building blocks needed for other Essentials libraries, such as:

- **Tuples**
    - E.g. `Triple<String, Long, BigDecimal> tuple = Tuple.of("Hello", 100L, new BigDecimal("125.95"));`
- **Collections**
    - E.g. `Stream<Pair<Integer, String>> indexedStream = Lists.toIndexedStream(List.of("A", "B", "C"));`
- **Functional Interfaces**
    - E.g. `Tuple.of("Hello", 100L, new BigDecimal("125.95")).map((_1, _2, _3) -> Tuple.of(_1.toString(), _2.toString(), _3.toString))`
- **FailFast** argument validation (Objects.requireNonNull replacement)
    - E.g. `FailFast.requireNonBlank(fieldName, "You must supply a fieldName");`
- SLF4J compatible **Message formatter**
  - E.g. `msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes))`
- High level **Reflection** API
    - E.g. `Reflector.reflectOn(someType).invokeStatic("of");`

## Types

This library focuses purely on providing base types and utility types that can be used to better documented and more
strongly typed code.  
With this libraries `SingleValueType` concept you can create your High Level types that works well using e.g. Jackson,
Spring Data Mongo, etc:

```
public class CreateOrder {
    public final OrderId                  id;
    public final Amount                   totalAmountWithoutSalesTax;
    public final Currency                 currency;
    public final Percentage               salesTax;
    public final Map<ProductId, Quantity> orderLines;

    ...
}
```

where a minimal custom identifier looks like this:

```
public class OrderId extends CharSequenceType<OrderId> implements Identifier {
    public OrderId(CharSequence value) {
        super(value);
    }
}
```

## Types Jackson

This library focuses purely on providing https://github.com/FasterXML/jackson serialization and deserialization support
for the **types** defined in the Essentials `types` library.

All you need to do is to add the `essentials.types.EssentialTypesJacksonModule` to your `ObjectMapper` configuration:

```
objectMapper.registerModule(new EssentialTypesJacksonModule());
```

## Types Spring Data Mongo

This library focuses purely on providing Spring Data Mongo persistence support for the **types** defined in the
Essentials `types` library.

All you need to do is to register the following Spring Beans to your Spring configuration:

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

to support this Entity (including automatic Id generation):

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

## Types Spring Data JPA

This library focuses purely on providing Spring Data JPA persistence support for the **types** defined in the
Essentials `types` library. Example:

```
@Entity
@Table(name = "orders")
public class Order {
    @Id
    public OrderId                  id;
    public CustomerId               customerId;
    public AccountId                accountId;
    @ElementCollection
    public Map<ProductId, Quantity> orderLines;
    
    ...
}
```

will work out of the box without the need for any custom `AttributeConverter`'s. BUT we currently don't support
automatic Id generation.

## Types JDBI (v3)

This library focuses purely on providing JDBI **argument** support for the **types** defined in the Essentials `types`
library.

You need to register your own `ArgumentFactory` with the `Jdbi` or `Handle` instance:

```
Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test");
jdbi.registerArgument(new OrderIdArgumentFactory());
jdbi.registerArgument(new CustomerIdArgumentFactory());
jdbi.registerArgument(new ProductIdArgumentFactory());
jdbi.registerArgument(new AccountIdArgumentFactory());
    
var orderId    = OrderId.random();
var customerId = CustomerId.random();
var productId  = ProductId.random();
var accountId  = AccountId.random();

handle.useHandle(handle -> handle.createUpdate("INSERT INTO orders(id, customer_id, product_id, account_id) VALUES (:id, :customerId, :productId, :accountId)")
                                  .bind("id", orderId)
                                  .bind("customerId", customerId)
                                  .bind("productId", productId)
                                  .bind("accountId", accountId)
                                  .execute());
```

where `CustomerId` is defined as:

```
public class CustomerId extends CharSequenceType<CustomerId> implements Identifier {
    public CustomerId(CharSequence value) {
        super(value);
    }
}
```

And the `CustomerIdArgumentFactory` of type `CharSequenceType` must extend `CharSequenceTypeArgumentFactory`:

```
public class CustomerIdArgumentFactory extends CharSequenceTypeArgumentFactory<CustomerId> {
}
```