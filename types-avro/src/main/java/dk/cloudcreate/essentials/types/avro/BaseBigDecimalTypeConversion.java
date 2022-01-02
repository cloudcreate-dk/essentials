/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.cloudcreate.essentials.types.avro;

import dk.cloudcreate.essentials.types.*;
import org.apache.avro.*;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Base {@link Conversion} for all custom types of type {@link BigDecimalType}.<br>
 * <b>NOTICE:</b>
 * <blockquote>
 * This Conversion requires that AVRO field/property must be use the AVRO primitive type: <b><code>string</code></b> and NEITHER the <code>"double"</code> NOR the more sophisticated logical type <code>"decimal"</code>.<br>
 * This means that you can have multiple concrete {@link BigDecimalType}'s mapped with in a single Schema, which comes at the risk of loosing precision in the number representation in the conversion between the concrete
 * {@link BigDecimalType}'s internal {@link BigDecimal} and the corresponding <code>string</code> value in Avro!<br>
 * <br>
 * In case you want to use logical type <code>decimal</code> then you need to use the {@link SingleConcreteBigDecimalTypeConversion}, which comes with the limitation that ALL Avro <code>decimal</code> fields/properties will be
 * mapped to a single concrete {@link BigDecimalType} supported by your implementation of the {@link SingleConcreteBigDecimalTypeConversion}<br>
 * </blockquote>
 * <br>
 * Each concrete {@link BigDecimalType} that must be support Avro serialization and deserialization to/from <code>string</code> must have a dedicated
 * {@link Conversion}, {@link LogicalType} and {@link org.apache.avro.LogicalTypes.LogicalTypeFactory} pair registered with the <b><code>avro-maven-plugin</code></b>.<br>
 * <br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>string</code></b><br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("MyLogicalType")
 *       string  tax;
 *   }
 * }
 * }</pre>
 * <br>
 * <b>Example:Support AVRO serialization and Deserialization for {@link Amount}:</b><br>
 * <b><u>1. Create the <code>AmountLogicalType</code> and <code>AmountLogicalTypeFactory</code></u></b>:<br>
 * <pre>{@code
 * public class AmountLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
 *     public static final LogicalType AMOUNT = new AmountLogicalType("Amount");
 *
 *     @Override
 *     public LogicalType fromSchema(Schema schema) {
 *         return AMOUNT;
 *     }
 *
 *     @Override
 *     public String getTypeName() {
 *         return AMOUNT.getName();
 *     }
 *
 *     public static class AmountLogicalType extends LogicalType {
 *         public AmountLogicalType(String logicalTypeName) {
 *             super(logicalTypeName);
 *         }
 *
 *         @Override
 *         public void validate(Schema schema) {
 *             super.validate(schema);
 *             if (schema.getType() != Schema.Type.STRING) {
 *                 throw new IllegalArgumentException("'" + getName() + "' can only be used with type '" + Schema.Type.STRING.getName() + "'. Invalid schema: " + schema.toString(true));
 *             }
 *         }
 *     }
 * }
 * }</pre>
 * <b><u>2. Create the <code>AmountConversion</code></u></b>:<br>
 * <pre>{@code
 * public class AmountConversion extends BaseBigDecimalTypeConversion<Amount> {
 *     @Override
 *     public Class<Amount> getConvertedType() {
 *         return Amount.class;
 *     }
 *
 *     @Override
 *     protected LogicalType getLogicalType() {
 *         return AmountLogicalTypeFactory.AMOUNT;
 *     }
 * }
 * }</pre>
 * <b><u>3. Register the <code>AmountConversion</code> and <code>AmountLogicalTypeFactory</code> with the <code>avro-maven-plugin</code></u></b>:<br>
 * <pre>{@code
 * <plugin>
 *     <groupId>org.apache.avro</groupId>
 *     <artifactId>avro-maven-plugin</artifactId>
 *     <version>${avro.version}</version>
 *     <executions>
 *         <execution>
 *             <phase>generate-sources</phase>
 *             <goals>
 *                 <goal>idl-protocol</goal>
 *             </goals>
 *             <configuration>
 *                 <stringType>String</stringType>
 *                 <enableDecimalLogicalType>false</enableDecimalLogicalType>
 *                 <customLogicalTypeFactories>
 *                     <logicalTypeFactory>dk.cloudcreate.essentials.types.avro.AmountLogicalTypeFactory</logicalTypeFactory>
 *                 </customLogicalTypeFactories>
 *                 <customConversions>
 *                     <conversion>dk.cloudcreate.essentials.types.avro.AmountConversion</conversion>
 *                 </customConversions>
 *             </configuration>
 *         </execution>
 *     </executions>
 * </plugin>
 * }</pre>
 * <b><u>Create a Record the uses the "Amount" logical type</u></b><br>
 * Example IDL <code>"order.avdl"</code>:<br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       string           id;
 *       @logicalType("Amount")
 *       string  totalAmountWithoutSalesTax;
 *   }
 * }
 * }</pre>
 * <br>
 * This will generate an Order class that looks like this:
 * <pre>{@code
 * public class Order extends SpecificRecordBase implements SpecificRecord {
 *   ...
 *   private java.lang.String id;
 *   private dk.cloudcreate.essentials.types.Amount totalAmountWithoutSalesTax;
 *   ...
 * }
 * }</pre>
 * @param <T> the concrete {@link BigDecimalType} sub-type
 * @see AmountConversion
 * @see AmountLogicalTypeFactory
 * @see PercentageConversion
 * @see PercentageLogicalTypeFactory
 */
public abstract class BaseBigDecimalTypeConversion<T extends BigDecimalType<T>> extends Conversion<T> {
    protected abstract LogicalType getLogicalType();

    @Override
    public final Schema getRecommendedSchema() {
        return getLogicalType().addToSchema(Schema.create(Schema.Type.STRING));
    }

    @Override
    public final String getLogicalTypeName() {
        return getLogicalType().getName();
    }

    @Override
    public T fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
        var stringValue = StandardCharsets.UTF_8.decode(value).toString();
        return convertToBigDecimalType(stringValue);
    }

    @Override
    public ByteBuffer toBytes(T value, Schema schema, LogicalType type) {
        return StandardCharsets.UTF_8.encode(convertFromBigDecimalType(value));
    }

    @Override
    public T fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return convertToBigDecimalType(value.toString());
    }

    @Override
    public CharSequence toCharSequence(T value, Schema schema, LogicalType type) {
        return convertFromBigDecimalType(value);
    }
    /**
     * Converts the stringValue to a {@link BigDecimalType}<br>
     * Default conversion uses this logic: <code>SingleValueType.from(new BigDecimal(value), getConvertedType());</code>
     * Override this method to provide a custom to {@link BigDecimalType} conversion
     * @param stringValue the {@link BigDecimalType}øs value represented as a string
     * @return the {@link BigDecimalType} instance corresponding to the <code>stringValue</code>
     */
    protected T convertToBigDecimalType(String stringValue) {
        return SingleValueType.from(new BigDecimal(stringValue), getConvertedType());
    }

    /**
     * Converts the internal {@link BigDecimal} (inside the {@link BigDecimalType}) to a stringValue<br>
     * Default conversion is to use the internal {@link BigDecimalType}'s {@link BigDecimal} string representation<br>
     * Override this method to provide a custom from {@link BigDecimal} conversion
     * @param value the {@link BigDecimal} value
     * @return the <code>stringValue</code> representation of the <code>value</code>>
     */
    protected String convertFromBigDecimalType(T value) {
        return value.value().toString();
    }


}
