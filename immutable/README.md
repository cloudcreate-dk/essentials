# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Immutable

This library focuses purely on providing utility classes that make it easier to create **simple** immutable types/classes, that
doesn't rely on code generators.

To use `Immutable` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>immutable</artifactId>
    <version>0.7.4</version>
</dependency>
```

**NOTE:**
**This library is WORK-IN-PROGRESS**

### Immutable Value Object
The base type `ImmutableValueObject` supports creating immutable (i.e. an object where its values cannot change after object instantiation/creation) **Value Object**  
The core feature set of `ImmutableValueObject` is that it provides default implementations for `toString`, `equals` and `hashCode`, but you're always free to override this and provide your own
implementation.

Example:
```
public class ImmutableOrder extends ImmutableValueObject {
    public final OrderId                  orderId;
    public final CustomerId               customerId;
    @Exclude.EqualsAndHashCode
    public final Percentage               percentage;
    @Exclude.ToString
    public final Money                    totalPrice;

    public ImmutableOrder(OrderId orderId,
                          CustomerId customerId,
                          Percentage percentage,
                          EmailAddress email,
                          Money totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.percentage = percentage;
        this.totalPrice = totalPrice;
    }
}
```

#### Value Object definition
> A **Value Object** is defined by its property values and not by an identifier.  
If two value objects, of the same type, have the **same property values** then they're considered to be **equal**.

Example:
```
var thisOrder = new ImmutableOrder(OrderId.of(123456789),
                                   CustomerId.of("CustomerId1"),
                                   Percentage.from("50%"),
                                   Money.of("1234.56", CurrencyCode.DKK));

var thatOrder = new ImmutableOrder(OrderId.of(123456789),
                                   CustomerId.of("CustomerId1"),
                                   Percentage.from("50%"),
                                   Money.of("1234.56", CurrencyCode.DKK));
                                   
assertThat(thisOrder).isEqualTo(thatOrder);
```

#### Immutability
The default implementation of `toString` and `hashCode` relies upon the assumption that ALL *non-transient* instance fields are marked `final` to ensure that they cannot change after they have been assigned a value.  
To ensure that `toString` and `hashCode` calculation only happens once, the `ImmutableValueObject` will **cache** the output of the first call to `toString` and `hashCode`.

This also means that if a field isn't `final` or if the **field type** is a `mutable` type, such as `List`, `Map`, `Set`, etc. then you **cannot reliably rely** on the output of followup calls
to `toString` or `hashCode` as the fields used for calculation may have changed value.

#### `toString` logic
All fields without the `@Exclude.ToString` annotation with be included in the output.  
The fields are sorted alphabetically (ascending order) and then grouped into fields with and without value.  
We will first output non-null fields (in alphabetically order) and finally null fields (in alphabetically order)

Example:
```
public class ImmutableOrder extends ImmutableValueObject {
    public final OrderId                  orderId;
    public final CustomerId               customerId;
    @Exclude.EqualsAndHashCode
    public final Percentage               percentage;
    @Exclude.ToString
    public final Money                    totalPrice;

    public ImmutableOrder(OrderId orderId,
                          CustomerId customerId,
                          Percentage percentage,
                          EmailAddress email,
                          Money totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.percentage = percentage;
        this.totalPrice = totalPrice;
    }
}

new ImmutableOrder(OrderId.of(123456789),
                   null,
                   Percentage.of("50%),
                   Money.of("1234.56", CurrencyCode.DKK)
                  )
                  .toString()
```
will return:

`ImmutableOrder { orderId: 123456789, percentage: 50.00%, customerId: null }`

with `customerId` last (as it has a null value) and without the `totalPrice` as it is excluded from being included in the `toString` 
result due to `@Exclude.ToString`

#### `equals` logic
The logic of `equals` follows the standard Java approach where we don't accept subclasses of a type, we only accept the exact same type.  
All fields without the `@Exclude.EqualsAndHashCode` annotation with be included in the `equals` operation.  
The fields are sorted alphabetically (ascending order) and values from the two objects being compared one by one.  
As soon a difference in field values is identified the comparison is stopped (to avoid wasting CPU cycles) and the result is returned.

#### `hashCode` logic
All fields without the `@Exclude.EqualsAndHashCode` annotation with be included the calculation of the `hash-code`.  
The fields are sorted alphabetically (ascending order) and the `hashCode` will be calculated field by field in the order of the fields names.  
The algorithm for calculating the hashcode follows the `Objects#hash(Object...)` method.