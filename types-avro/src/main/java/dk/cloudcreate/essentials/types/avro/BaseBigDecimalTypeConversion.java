/*
 * Copyright 2021 the original author or authors.
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

/**
 * Base {@link Conversion} for all custom types of type {@link BigDecimalType}.<br>
 * <b>NOTICE:</b> This Conversion requires that AVRO field/property must be use the AVRO primitive type: <b><code>double</code></b> and NOT the more sophisticated logical type <code>"decimal"</code>.<br>
 * In case you want to use logical type <code>"decimal"</code> then you need to use the {@link SingleConcreteBigDecimalTypeConversion}<br>
 * <br>
 * Each concrete {@link BigDecimalType} that must be support Avro serialization and deserialization must have a dedicated
 * {@link Conversion}, {@link LogicalType} and {@link org.apache.avro.LogicalTypes.LogicalTypeFactory} pair registered with the <b><code>avro-maven-plugin</code></b>.<br>
 * <br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>double</code></b><br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("MyLogicalType")
 *       double  tax;
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
 *             if (schema.getType() != Schema.Type.DOUBLE) {
 *                 throw new IllegalArgumentException("Amount can only be used with an underlying Double type");
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
 *       double  totalAmountWithoutSalesTax;
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
        return getLogicalType().addToSchema(Schema.create(Schema.Type.DOUBLE));
    }

    @Override
    public final String getLogicalTypeName() {
        return getLogicalType().getName();
    }

    @Override
    public T fromDouble(Double value, Schema schema, LogicalType type) {
        return SingleValueType.from(new BigDecimal(value), getConvertedType());
    }

    @Override
    public Double toDouble(T value, Schema schema, LogicalType type) {
        return value.doubleValue();
    }
}
