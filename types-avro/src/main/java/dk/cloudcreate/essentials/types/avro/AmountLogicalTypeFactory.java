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
 * {@link dk.cloudcreate.essentials.types.Amount}.<br>
 * <br>
 * <b>Important:</b> The AVRO field/property must be use the AVRO primitive type: <b><code>string</code></b>, otherwise we loose precision<br>
 * <pre>{@code
 * @namespace("dk.cloudcreate.essentials.types.avro.test")
 * protocol Test {
 *   record Order {
 *       @logicalType("Amount")
 *       string  totalAmountWithoutSalesTax;
 *   }
 * }
 * }</pre>
 * <br>
 * To support Avro code generation for fields/properties that use the logical-type "Amount" you
 * need to perform the following steps:
 * <b><u>Register the <code>AmountConversion</code> and <code>AmountLogicalTypeFactory</code> with the <code>avro-maven-plugin</code></u></b>:<br>
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
 */
public class AmountLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {
    public static final LogicalType AMOUNT = new AmountLogicalType("Amount");

    @Override
    public LogicalType fromSchema(Schema schema) {
        return AMOUNT;
    }

    @Override
    public String getTypeName() {
        return AMOUNT.getName();
    }

    public static class AmountLogicalType extends LogicalType {
        public AmountLogicalType(String logicalTypeName) {
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
