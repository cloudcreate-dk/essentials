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

import org.apache.avro.*;

/**
 * {@link LogicalType} and {@link org.apache.avro.LogicalTypes.LogicalTypeFactory} for
 * {@link dk.cloudcreate.essentials.types.CurrencyCode}.<br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>string</code></b><br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("CurrencyCode")
 *       string  currency;
 *   }
 * }
 * }</pre>
 * <br>
 * To support Avro code generation for fields/properties that use the logical-type "CurrencyCode" you
 * need to perform the following steps:
 * <b><u>Register the <code>CurrencyCodeConversion</code> and <code>CurrencyCodeLogicalTypeFactory</code> with the <code>avro-maven-plugin</code></u></b>:<br>
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
 */
public class CurrencyCodeLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
    public static final LogicalType CURRENCY_CODE = new CurrencyCodeLogicalType("CurrencyCode");

    @Override
    public LogicalType fromSchema(Schema schema) {
        return CURRENCY_CODE;
    }

    @Override
    public String getTypeName() {
        return CURRENCY_CODE.getName();
    }

    public static class CurrencyCodeLogicalType extends LogicalType {
        public CurrencyCodeLogicalType(String logicalTypeName) {
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
