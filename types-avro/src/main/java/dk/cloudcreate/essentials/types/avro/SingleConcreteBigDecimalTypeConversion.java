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
import org.apache.avro.generic.GenericFixed;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * If you only wish to use a <b>single</b> {@link BigDecimalType} that ALL Avro <code>decimal</code> fields/properties will map to, then you can create your own
 * concrete {@link SingleConcreteBigDecimalTypeConversion} custom conversion. The advantage is that you can retain full precision in the number representation in the conversion between the internal
 * {@link BigDecimalType}'s {@link BigDecimal} and the corresponding <code>decimal</code> value in Avro!<br>
 * The disadvantage is that ALL Avro <code>decimal</code> fields/properties will be mapped to a single concrete {@link BigDecimalType} supported by your implementation of the {@link SingleConcreteBigDecimalTypeConversion}<br>
 * <br>
 * <b>In case you want each {@link BigDecimalType} field/property to be mappable to an individual logical-type, then you need to use the {@link BaseBigDecimalTypeConversion} for each concrete {@link BigDecimalType} you want to support!</b><br>
 * <br>
 * Example of a {@link SingleConcreteBigDecimalTypeConversion} conversion that converts all Avro <code>decimal</code>'s to the {@link Amount} type:
 * <pre>{@code
 * package com.myproject.types.avro;
 *
 * public class AmountConversion extends SingleConcreteBigDecimalTypeConversion<Amount> {
 *     @Override
 *     public Class<Amount> getConvertedType() {
 *         return Amount.class;
 *     }
 * }
 * }</pre>
 * This must be combined with setting <code>enableDecimalLogicalType</code> set to <b>true</b> on the <b><code>avro-maven-plugin</code></b><br>
 * and adding your custom <code>conversion</code> in the <code>customConversions</code> section:
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
 *                 <enableDecimalLogicalType>true</enableDecimalLogicalType>
 *                 <customConversions>
 *                     <conversion>com.myproject.types.avro.AmountConversion</conversion>
 *                 </customConversions>
 *             </configuration>
 *         </execution>
 *     </executions>
 * </plugin>
 * }</pre>
 * <br>
 * Given this Avro IDL "order.avdl":
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       string          id;
 *       decimal(9, 2)   totalAmountWithoutSalesTax;
 *       decimal(4, 1)   totalSalesTax;
 *   }
 * }
 * }</pre>
 * it will result in a generated <code>Order</code> class that will look like this:
 * <pre>{@code
 * public class Order extends SpecificRecordBase implements SpecificRecord {
 *   ...
 *   private java.lang.String id;
 *   private dk.cloudcreate.essentials.types.Amount totalAmountWithoutSalesTax;
 *   private dk.cloudcreate.essentials.types.Amount totalSalesTax;
 *   ...
 * }
 * }</pre>
 * <b>Note: That <code>Amount</code> is used for <u>ALL</u> <code>decimal</code> properties/fields</b>
 *
 * @param <T> the concrete {@link BigDecimalType} type
 */
public abstract class SingleConcreteBigDecimalTypeConversion<T extends BigDecimalType<T>> extends Conversion<T> {
    private static final Conversions.DecimalConversion DECIMAL_CONVERSION = new Conversions.DecimalConversion();

    @Override
    public final String getLogicalTypeName() {
        return DECIMAL_CONVERSION.getLogicalTypeName();
    }

    @Override
    public T fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
        return SingleValueType.from(DECIMAL_CONVERSION.fromBytes(value, schema, type), getConvertedType());
    }

    @Override
    public ByteBuffer toBytes(T value, Schema schema, LogicalType type) {
        return DECIMAL_CONVERSION.toBytes(value.value(), schema, type);
    }

    @Override
    public T fromFixed(GenericFixed value, Schema schema, LogicalType type) {
        return SingleValueType.from(DECIMAL_CONVERSION.fromFixed(value, schema, type), getConvertedType());
    }

    @Override
    public GenericFixed toFixed(T value, Schema schema, LogicalType type) {
        return DECIMAL_CONVERSION.toFixed(value.value(), schema, type);
    }
}
