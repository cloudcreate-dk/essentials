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

/**
 * Base {@link Conversion} for all custom types of type {@link IntegerType}.<br>
 * <b>NOTICE:</b> This Conversion requires that AVRO field/property must be use the AVRO primitive type: <b><code>int</code></b><br>
 * <br>
 * Each concrete {@link IntegerType} that must be support Avro serialization and deserialization must have a dedicated
 * {@link Conversion}, {@link LogicalType} and {@link LogicalTypes.LogicalTypeFactory} pair registered with the <b><code>avro-maven-plugin</code></b>.<br>
 * <br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>int</code></b><br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("MyLogicalType")
 *       int  quantity;
 *   }
 * }
 * }</pre>
 * <br>
 * <b>Example:Support AVRO serialization and Deserialization for a given Quantity type:</b><br>
 * <b><u>1. Create the <code>QuantityLogicalType</code> and <code>QuantityLogicalTypeFactory</code></u></b>:<br>
 * <pre>{@code
 * public class QuantityLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
 *     public static final LogicalType QUANTITY = new QuantityLogicalType("Quantity");
 *
 *     @Override
 *     public LogicalType fromSchema(Schema schema) {
 *         return QUANTITY;
 *     }
 *
 *     @Override
 *     public String getTypeName() {
 *         return QUANTITY.getName();
 *     }
 *
 *     public static class QuantityLogicalType extends LogicalType {
 *         public QuantityLogicalType(String logicalTypeName) {
 *             super(logicalTypeName);
 *         }
 *
 *         @Override
 *         public void validate(Schema schema) {
 *             super.validate(schema);
 *             if (schema.getType() != Schema.Type.INT) {
 *                 throw new IllegalArgumentException("Quantity can only be used with an underlying Int type");
 *             }
 *         }
 *     }
 * }
 * }</pre>
 * <b><u>2. Create the <code>QuantityConversion</code></u></b>:<br>
 * <pre>{@code
 * public class QuantityConversion extends BaseLongTypeConversion<Quantity> {
 *     @Override
 *     public Class<Quantity> getConvertedType() {
 *         return Quantity.class;
 *     }
 *
 *     @Override
 *     protected LogicalType getLogicalType() {
 *         return QuantityLogicalTypeFactory.QUANTITY;
 *     }
 * }
 * }</pre>
 * <b><u>3. Register the <code>QuantityConversion</code> and <code>QuantityLogicalTypeFactory</code> with the <code>avro-maven-plugin</code></u></b>:<br>
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
 *                     <logicalTypeFactory>com.myproject.types.avro.QuantityLogicalTypeFactory</logicalTypeFactory>
 *                 </customLogicalTypeFactories>
 *                 <customConversions>
 *                     <conversion>com.myproject.types.avro.QuantityConversion</conversion>
 *                 </customConversions>
 *             </configuration>
 *         </execution>
 *     </executions>
 * </plugin>
 * }</pre>
 * <b><u>Create a Record the uses the "Quantity" logical type</u></b><br>
 * Example IDL <code>"order.avdl"</code>:<br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       string           id;
 *       @logicalType("Quantity")
 *       int             quanity;
 *   }
 * }
 * }</pre>
 * <br>
 * This will generate an Order class that looks like this:
 * <pre>{@code
 * public class Order extends SpecificRecordBase implements SpecificRecord {
 *   ...
 *   private java.lang.String id;
 *   private com.myproject.types.Quantity quantity;
 *   ...
 * }
 * }</pre>
 *
 * @param <T> the concrete {@link IntegerType} sub-type
 */
public abstract class BaseIntegerTypeConversion<T extends IntegerType<T>> extends Conversion<T> {
    protected abstract LogicalType getLogicalType();

    @Override
    public final Schema getRecommendedSchema() {
        return getLogicalType().addToSchema(Schema.create(Schema.Type.INT));
    }

    @Override
    public final String getLogicalTypeName() {
        return getLogicalType().getName();
    }

    @Override
    public T fromInt(Integer value, Schema schema, LogicalType type) {
        return SingleValueType.from(value, getConvertedType());
    }

    @Override
    public Integer toInt(T value, Schema schema, LogicalType type) {
        return value.intValue();
    }
}
