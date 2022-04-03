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

package dk.cloudcreate.essentials.types;

import dk.cloudcreate.essentials.shared.*;
import dk.cloudcreate.essentials.shared.reflection.*;

import java.io.Serializable;
import java.math.*;
import java.util.Map;

/**
 * Common interface for all <b>immutable</b> single value Types - see {@link CharSequenceType} and {@link NumberType} for more details about types supported directly<br>
 * <br>
 * <b>Purpose:</b><br>
 * {@link SingleValueType}'s are typically used to create strongly typed wrappers for simple values to increase readability, code search capabilities (e.g. searching for where a given type is referenced).<br>
 * Compare:
 * <pre>{@code
 * public class CreateOrder {
 *     public final String               id;
 *     public final BigDecimal           totalAmountWithoutSalesTax;
 *     public final String               currency;
 *     public final BigDecimal           salesTax;
 *     public final Map<String, Integer> orderLines;
 *
 *     ...
 * }
 * }</pre>
 * vs.
 * <pre>{@code
 * public class CreateOrder {
 *     public final OrderId                  id;
 *     public final Amount                   totalAmountWithoutSalesTax;
 *     public final Currency                 currency;
 *     public final Percentage               salesTax;
 *     public final Map<ProductId, Quantity> orderLines;
 *
 *     ...
 * }
 * }</pre>
 * <br>
 * <i>The last CreateOrder has a much higher degree of readability:</i><br>
 * <ul>
 *     <li>Which type reveal the <b>role</b> of a type best: <b>Amount<b/> or <i>BigDecimal</i>, <b>Currency</b> or <i>String</i>?</li>
 *     <li>You can see that <b>salesTax</b> is a <b>Percentage</b> number and <b>not</b> the calculated sales tax.</li>
 *     <li>The <b>orderLines</b> now clearly indicates that the <i>key</i> is the <b>ProductId</b> and the <i>value</i> is the <b>Quantity</b></li>
 * </ul>
 * <br>
 * A {@link SingleValueType} encapsulates a single <b>NON-NULL</b> <b><u>value</u></b> of type<code>VALUE_TYPE</code> (the supported VALUE_TYPE depends on the <b>base</b> <code>CONCRETE_TYPE</code> {@link SingleValueType}):
 * <table>
 *     <tr><td>Base CONCRETE_TYPE</td><td>VALUE_TYPE</td></tr>
 *     <tr><td>{@link BigDecimalType} (sub type of {@link NumberType})</td><td>{@link BigDecimal}</td></tr>
 *     <tr><td>{@link BigIntegerType} (sub type of {@link NumberType})</td><td>{@link BigInteger}</td></tr>
 *     <tr><td>{@link ByteType} (sub type of {@link NumberType})</td><td>{@link Byte}</td></tr>
 *     <tr><td>{@link CharSequenceType}</td><td>{@link CharSequence}/{@link String}</td></tr>
 *     <tr><td>{@link DoubleType} (sub type of {@link NumberType})</td><td>{@link Double}</td></tr>
 *     <tr><td>{@link FloatType} (sub type of {@link NumberType})</td><td>{@link Float}</td></tr>
 *     <tr><td>{@link IntegerType} (sub type of {@link NumberType})</td><td>{@link Integer}</td></tr>
 *     <tr><td>{@link LongType} (sub type of {@link NumberType})</td><td>{@link Long}</td></tr>
 *     <tr><td>{@link ShortType} (sub type of {@link NumberType})</td><td>{@link Short}</td></tr>
 * </table>
 * <br>
 * <b>Concrete</b> {@link SingleValueType}'s are always subclasses of a <b>base</b> <code>CONCRETE_TYPE</code>, such as the OrderId:
 * <pre>{@code
 * public class OrderId extends LongType<OrderId> implements Identifier {
 *     private static Random RANDOM_ID_GENERATOR = new Random();
 *
 *     public OrderId(Long value) {
 *         super(value);
 *     }
 *
 *     public static OrderId of(long value) {
 *         return new OrderId(value);
 *     }
 *
 *     public static OrderId ofNullable(Long value) {
 *         return value != null ? new OrderId(value) : null;
 *     }
 *
 *     public static OrderId random() {
 *         return new OrderId(RANDOM_ID_GENERATOR.nextLong());
 *     }
 * }
 * }</pre>
 * or CustomerId:
 * <pre>{@code
 * public class CustomerId extends CharSequenceType<CustomerId> implements Identifier {
 *     public CustomerId(CharSequence value) {
 *         super(value);
 *     }
 *
 *     public static CustomerId of(CharSequence value) {
 *         return new CustomerId(value);
 *     }
 *
 *     public static CustomerId ofNullable(CharSequence value) {
 *         return value != null ? new CustomerId(value) : null;
 *     }
 *
 *     public static CustomerId random() {
 *         return new CustomerId(UUID.randomUUID().toString());
 *     }
 * }
 * }</pre>
 * <hr>
 * You can expand with set of base <code>CONCRETE_TYPE</code>'s (e.g. to support wrapping a byte[]) by providing you own implementation of the {@link SingleValueType}:
 * <pre>{@code
 * public abstract class ByteArrayType<CONCRETE_TYPE extends ByteArrayType<CONCRETE_TYPE>> implements SingleValueType<byte[], CONCRETE_TYPE>{
 *     protected final byte[] value;
 *
 *     public NumberType(byte[] value) {
 *         requireNonNull(value, "You must provide a value");
 *         this.value = value;
 *     }
 *
 *     @Override
 *     public byte[] value() {
 *         return Arrays.hashCode(value);
 *     }
 *
 *     @Override
 *     public int hashCode() {
 *         return value.hashCode();
 *     }
 *
 *     @Override
 *     public boolean equals(Object o) {
 *         if (o == null) {
 *             return false;
 *         }
 *         if (this == o) {
 *             return true;
 *         }
 *         if (!(this.getClass().isAssignableFrom(o.getClass()))) {
 *             return false;
 *         }
 *
 *         var that = (ByteArrayType<?>) o;
 *         return Arrays.equals(value, that.value);
 *     }
 *
 *     @Override
 *     public String toString() {
 *         return Arrays.toString(value);
 *     }
 * }
 * }</pre>
 *
 * @param <VALUE_TYPE>    the type of value encapsulated with the concrete {@link SingleValueType} implementation
 * @param <CONCRETE_TYPE> the concrete {@link SingleValueType} implementation
 */
public interface SingleValueType<VALUE_TYPE, CONCRETE_TYPE extends SingleValueType<VALUE_TYPE, CONCRETE_TYPE>> extends Serializable, Comparable<CONCRETE_TYPE> {
    /**
     * Get the raw value of the type instance
     *
     * @return the value of the type instance (never null)
     */
    VALUE_TYPE value();

    // --------------------------------------------------------------------------------------------------------------

    /**
     * Create an instance of a concrete {@link SingleValueType} based on an VALUE_TYPE value<br>
     * It will initially try a constructor that accepts a single argument.<br>
     * If the type doesn't provide such a constructor it will fall back to calling a static <b>to</b> method that takes a single argument
     *
     * @param value           the non-null value that will be used as value for the <code>concreteType</code>
     * @param concreteType    the concrete {@link SingleValueType}
     * @param <CONCRETE_TYPE> the concrete {@link SingleValueType}
     * @return an instance of the type with the value
     */
    @SuppressWarnings("unchecked")
    static <VALUE_TYPE, CONCRETE_TYPE extends SingleValueType<VALUE_TYPE, CONCRETE_TYPE>> CONCRETE_TYPE from(VALUE_TYPE value, Class<CONCRETE_TYPE> concreteType) {
        FailFast.requireNonNull(value, "You must provide a value");
        FailFast.requireNonNull(concreteType, "You must provide a concreteType");

        return (CONCRETE_TYPE) fromObject(value, concreteType);
    }

    /**
     * Create an instance of a concrete {@link SingleValueType} based on an Object value - useful for generic code where the types aren't present in the code<br>
     * It will initially try a constructor that accepts a single argument.<br>
     * If the type doesn't provide such a constructor it will look for a static <b>to</b> method that takes a single argument matching the argument-type.<br>
     * If it cannot find such a method it will look for a static <b>from</b> method that takes a single argument matching the argument-type.
     *
     * @param value        the non-null value that will be used as value for the <code>concreteType</code>
     * @param concreteType the concrete {@link SingleValueType}
     * @return an instance of the type with the value
     * @throws ReflectionException in case we cannot find a matching constructor, static of or static from method
     */
    static SingleValueType<?, ?> fromObject(Object value, Class<? extends SingleValueType<?, ?>> concreteType) {
        FailFast.requireNonNull(value, "You must provide a value");
        FailFast.requireNonNull(concreteType, "You must provide a concreteType");

        Reflector reflector = Reflector.reflectOn(concreteType);
        if (reflector.hasMatchingConstructorBasedOnArguments(value)) {
            return reflector.newInstance(value);
        } else {
            // Assumes best practice of providing a static of method
            var matchingOfMethod = reflector.findMatchingMethod("of", true, value.getClass());
            if (matchingOfMethod.isPresent()) {
                return reflector.invokeStatic(matchingOfMethod.get(), value);
            } else {
                matchingOfMethod = reflector.findMatchingMethod("from", true, value.getClass());
                if (matchingOfMethod.isPresent()) {
                    return reflector.invokeStatic(matchingOfMethod.get(), value);
                }
                throw new ReflectionException(MessageFormatter.bind("Failed to create instance of '{:singleValueType}' from value of type '{:valueTypeName}'." +
                                                                            "Didn't find a {:singleValueTypeSimpleName}(:valueTypeName) constructor, " +
                                                                            "static {:singleValueTypeSimpleName} of({:valueTypeName}), " +
                                                                            "static {:singleValueTypeSimpleName} from({:valueTypeName})",
                                                                    Map.of("singleValueTypeName", concreteType.getName(),
                                                                           "singleValueTypeSimpleName", concreteType.getSimpleName(),
                                                                           "valueTypeName", value.getClass().getName())));
            }
        }
    }
}
