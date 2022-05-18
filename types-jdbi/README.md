# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Types-JDBI

This library focuses purely on providing [JDBI v3](https://jdbi.org) **argument** support for the **types** defined in the Essentials `types`
library.

To use `Types-JDBI` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-jdbi</artifactId>
    <version>0.7.1</version>
</dependency>
```

**NOTE:**
**This library is WORK-IN-PROGRESS**

To support a given Argument of type `SingleValueType` you need to extend the corresponding provided
abstract `ArgumentFactory`.

Example:

```
public class CustomerId extends CharSequenceType<CustomerId> implements Identifier {
    public CustomerId(CharSequence value) {
        super(value);
    }

    public static CustomerId of(CharSequence value) {
        return new CustomerId(value);
    }

    public static CustomerId random() {
        return new CustomerId(UUID.randomUUID().toString());
    }
}
```

This concrete `SingleValueType` extends `CharSequenceType`, wherefore a JDBI `Argument`
converter (aka `ArgumentFactory`) must extend `CharSequenceTypeArgumentFactory`:

```
public class CustomerIdArgumentFactory extends CharSequenceTypeArgumentFactory<CustomerId> {
}
```

After this you need to register the `ArgumentFactory` with the `Jdbi` or `Handle` instance:

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
