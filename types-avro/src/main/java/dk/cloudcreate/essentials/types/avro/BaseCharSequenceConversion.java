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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Base {@link Conversion} for all custom types of type {@link CharSequenceType}.<br>
 * Each concrete {@link CharSequenceType} that must be support Avro serialization and deserialization must have a dedicated
 * {@link Conversion}, {@link LogicalType} and {@link org.apache.avro.LogicalTypes.LogicalTypeFactory} pair registered with the <b><code>avro-maven-plugin</code></b>.<br>
 * <br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>string</code></b><br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("MyLogicalType")
 *       string  currency;
 *   }
 * }
 * }</pre>
 * <br>
 * <b>Example:Support AVRO serialization and Deserialization for {@link CurrencyCode}:</b><br>
 * <b><u>1. Create the <code>CurrencyCodeLogicalType</code> and <code>CurrencyCodeLogicalTypeFactory</code></u></b>:<br>
 * <pre>{@code
 * package dk.cloudcreate.essentials.types.avro;
 *
 * public class CurrencyCodeLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
 *     public static final LogicalType CURRENCY_CODE = new CurrencyCodeLogicalType("CurrencyCode");
 *
 *     @Override
 *     public LogicalType fromSchema(Schema schema) {
 *         return CURRENCY_CODE;
 *     }
 *
 *     @Override
 *     public String getTypeName() {
 *         return CURRENCY_CODE.getName();
 *     }
 *
 *     public static class CurrencyCodeLogicalType extends LogicalType {
 *         public CurrencyCodeLogicalType(String logicalTypeName) {
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
 * <b><u>2. Create the <code>CurrencyCodeConversion</code></u></b>:<br>
 * <pre>{@code
 * package dk.cloudcreate.essentials.types.avro;
 *
 * public class CurrencyCodeConversion extends BaseCharSequenceConversion<CurrencyCode> {
 *     @Override
 *     public Class<CurrencyCode> getConvertedType() {
 *         return CurrencyCode.class;
 *     }
 *
 *     @Override
 *     protected LogicalType getLogicalType() {
 *         return CurrencyCodeLogicalTypeFactory.CURRENCY_CODE;
 *     }
 * }
 * }</pre>
 * <b><u>3. Register the <code>CurrencyCodeConversion</code> and <code>CurrencyCodeLogicalTypeFactory</code> with the <code>avro-maven-plugin</code></u></b>:<br>
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
 *                     <logicalTypeFactory>dk.cloudcreate.essentials.types.avro.CurrencyCodeLogicalTypeFactory</logicalTypeFactory>
 *                 </customLogicalTypeFactories>
 *                 <customConversions>
 *                     <conversion>dk.cloudcreate.essentials.types.avro.CurrencyCodeConversion</conversion>
 *                 </customConversions>
 *             </configuration>
 *         </execution>
 *     </executions>
 * </plugin>
 * }</pre>
 * <b><u>Create a Record the uses the "CurrencyCode" logical type</u></b><br>
 * Example IDL <code>"order.avdl"</code>:<br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       string           id;
 *       @logicalType("CurrencyCode")
 *       string           currency;
 *   }
 * }
 * }</pre>
 * <br>
 * This will generate an Order class that looks like this:
 * <pre>{@code
 * public class Order extends SpecificRecordBase implements SpecificRecord {
 *   ...
 *   private java.lang.String id;
 *   private dk.cloudcreate.essentials.types.CurrencyCode currency;
 *   ...
 * }
 * }</pre>
 * @param <T> the concrete {@link CharSequenceType} sub-type
 */
public abstract class BaseCharSequenceConversion<T extends CharSequenceType<T>> extends Conversion<T> {
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
        return SingleValueType.from(stringValue, getConvertedType());
    }

    @Override
    public ByteBuffer toBytes(T value, Schema schema, LogicalType type) {
        return StandardCharsets.UTF_8.encode(value.toString());
    }

    @Override
    public T fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return SingleValueType.from(value.toString(), getConvertedType());
    }

    @Override
    public CharSequence toCharSequence(T value, Schema schema, LogicalType type) {
        return value.value();
    }
}
