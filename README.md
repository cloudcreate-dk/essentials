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

To use `Shared` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>shared</artifactId>
    <version>0.1.1</version>
</dependency>
```

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

To use `Types` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types</artifactId>
    <version>0.1.1</version>
</dependency>
```


## Types Jackson

This library focuses purely on providing https://github.com/FasterXML/jackson serialization and deserialization support
for the **types** defined in the Essentials `types` library.

All you need to do is to add the `dk.cloudcreate.essentials.types.EssentialTypesJacksonModule` to your `ObjectMapper` configuration:

```
objectMapper.registerModule(new EssentialTypesJacksonModule());
```

To use `Types-Jackson` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-jackson</artifactId>
    <version>0.1.1</version>
</dependency>
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

To use `Types-SpringData-Mongo` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-springdata-mongo</artifactId>
    <version>0.1.1</version>
</dependency>
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

To use `Types-SpringData-JPA` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-springdata-jpa</artifactId>
    <version>0.1.1</version>
</dependency>
```

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

To use `Types-JDBI` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>types-jdbi</artifactId>
    <version>0.1.1</version>
</dependency>
```

## Types Avro
This library focuses purely on providing AVRO serialization and deserialization support for the **types** defined in the Essentials `types` library.

Some concrete `Types` such as `Amount`, `Percentage` and `CurrencyCode` come with supported our of the box.  
This allows you to define Avro schema/IDL protocol and directly refer these logical-types in your Avro Schema/IDL protocol. 

Example `order.avdl`:

```
@namespace("dk.cloudcreate.essentials.types.avro.test")
protocol Test {
  record Order {
      string           id;
      @logicalType("Amount")
      double           totalAmountWithoutSalesTax;
      @logicalType("CurrencyCode")
      string           currency;
      @logicalType("Percentage")
      double           salesTax;
  }
}
```

Let's say you want to introduce your own `OrderId` type:

```
package com.myproject.types;

public class OrderId extends CharSequenceType<OrderId> implements Identifier {
    public OrderId(CharSequence value) {
        super(value);
    }

    public static OrderId of(CharSequence value) {
        return new OrderId(value);
    }
}
```

and you want to use it in your Avro schema/IDL protocol:

```
@namespace("dk.cloudcreate.essentials.types.avro.test")
protocol Test {
  record Order {
      @logicalType("OrderId")
      string           id;
      @logicalType("Amount")
      double           totalAmountWithoutSalesTax;
      @logicalType("CurrencyCode")
      string           currency;
      @logicalType("Percentage")
      double           salesTax;
  }
}
```

then you will need to define the following classes:

#### 1. Create the `OrderIdLogicalType` and `OrderIdLogicalTypeFactory`

```
package com.myproject.types.avro;

public class OrderIdLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
    public static final LogicalType ORDER_ID = new CurrencyCodeLogicalType("OrderId");

    @Override
    public LogicalType fromSchema(Schema schema) {
        return ORDER_ID;
    }

    @Override
    public String getTypeName() {
        return ORDER_ID.getName();
    }

    public static class OrderIdLogicalType extends LogicalType {
        public OrderIdLogicalType(String logicalTypeName) {
            super(logicalTypeName);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.STRING) {
                throw new IllegalArgumentException("'" + getName() + "' can only be used with type '" + Schema.Type.STRING.getName() + "'. Invalid schema: " + schema.toString(true));
            }
        }
    }
}
```

#### 2. Create the `OrderIdConversion`

```
package com.myproject.types.avro;

public class OrderIdConversion extends BaseCharSequenceConversion<OrderId> {
    @Override
    public Class<OrderId> getConvertedType() {
        return OrderId.class;
    }

    @Override
    protected LogicalType getLogicalType() {
        return OrderIdLogicalTypeFactory.CURRENCY_CODE;
    }
}
```

#### 3. Register the `OrderIdConversion` and `OrderIdLogicalTypeFactory` with the `avro-maven-plugin`

```
<plugin>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-maven-plugin</artifactId>
    <version>${avro.version}</version>
    <executions>
        <execution>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>idl-protocol</goal>
            </goals>
            <configuration>
                <stringType>String</stringType>
                <enableDecimalLogicalType>false</enableDecimalLogicalType>
                <customLogicalTypeFactories>
                    <logicalTypeFactory>dk.cloudcreate.essentials.types.avro.CurrencyCodeLogicalTypeFactory</logicalTypeFactory>
                    <logicalTypeFactory>dk.cloudcreate.essentials.types.avro.AmountLogicalTypeFactory</logicalTypeFactory>
                    <logicalTypeFactory>dk.cloudcreate.essentials.types.avro.PercentageLogicalTypeFactory</logicalTypeFactory>
                    <logicalTypeFactory>com.myproject.types.avro.OrderIdLogicalTypeFactory</logicalTypeFactory>
                </customLogicalTypeFactories>
                <customConversions>
                    <conversion>dk.cloudcreate.essentials.types.avro.CurrencyCodeConversion</conversion>
                    <conversion>dk.cloudcreate.essentials.types.avro.AmountConversion</conversion>
                    <conversion>dk.cloudcreate.essentials.types.avro.PercentageConversion</conversion>
                    <conversion>com.myproject.types.avro.OrderIdConversion</conversion>
                </customConversions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will generate an `Order` class that now includes the `OrderId` and which will look like this:

```
@org.apache.avro.specific.AvroGenerated
public class Order extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  ...    
  private com.myproject.types.OrderId                  id;
  private dk.cloudcreate.essentials.types.Amount       totalAmountWithoutSalesTax;
  private dk.cloudcreate.essentials.types.CurrencyCode currency;
  private dk.cloudcreate.essentials.types.Percentage   salesTax;
  ...
}
```
