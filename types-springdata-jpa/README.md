# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies
other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and
strongly typed code, which doesn't depend on other libraries or frameworks, but instead allows easy integrations with
many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Types-SpringData-JPA

This library focuses purely on providing Spring Data JPA persistence support for the **types** defined in the
Essentials `types` library.

**NOTE:**
**This library is WORK-IN-PROGRESS, and currently it only supports simple `AttributeConverter`'s.   
It e.g. doesn't support Id _autogeneration_ for `@Id` annotated `SingleValueType` field/properties!!!**

Example:

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

will work out of the box without the need for any custom `AttributeConverter`'s.  
Should you require a custom `AttributeConverter`, then you can extend one of the many Base AttributeConverters in
`dk.cloudcreate.essentials.types.springdata.jpa.converters`:

- `BaseBigDecimalTypeAttributeConverter`
- `BaseByteTypeAttributeConverter`
- `BaseCharSequenceTypeAttributeConverter`
- `BaseDoubleTypeAttributeConverter`
- `BaseFloatTypeAttributeConverter`
- `BaseIntegerTypeAttributeConverter`
- `BaseLongTypeAttributeConverter`
- `BaseShortTypeAttributeConverter`

Example:

```
@Converter(autoApply = true)
public class CustomerIdAttributeConverter extends BaseCharSequenceTypeAttributeConverter<CustomerId> {
    @Override
    protected Class<CustomerId> getConcreteCharSequenceType() {
        return CustomerId.class;
    }
}
```

or

```
@Converter(autoApply = true)
public class AccountIdAttributeConverter extends BaseLongTypeAttributeConverter<AccountId> {
    @Override
    protected Class<AccountId> getConcreteLongType() {
        return AccountId.class;
    }
}
 ```
