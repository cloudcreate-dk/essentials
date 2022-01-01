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

import dk.cloudcreate.essentials.shared.FailFast;

import java.math.*;

/**
 * Base class for all {@link SingleValueType}'s that encapsulate {@link Number}.<br>
 * {@link NumberType}'s are typically used to create strongly typed wrappers for simple values to increase readability, code search capabilities (e.g. searching for where a given type is referenced).<br>
 * Example of candidates for concrete {@link NumberType} sub-types are Primary-Key identifiers, Aggregate Identifiers, Counters, Indexes, Percentage, etc.<br>
 * <br>
 * Directly supported {@link Number} types:<br>
 * <table>
 *     <tr><td>Base CONCRETE_TYPE (sub class of {@link NumberType})</td><td>NUMBER_TYPE</td></tr>
 *     <tr><td>{@link BigDecimalType}</td><td>{@link BigDecimal}</td></tr>
 *     <tr><td>{@link BigIntegerType}</td><td>{@link BigInteger}</td></tr>
 *     <tr><td>{@link ByteType}</td><td>{@link Byte}</td></tr>
 *     <tr><td>{@link DoubleType}</td><td>{@link Double}</td></tr>
 *     <tr><td>{@link FloatType}</td><td>{@link Float}</td></tr>
 *     <tr><td>{@link IntegerType}</td><td>{@link Integer}</td></tr>
 *     <tr><td>{@link LongType}</td><td>{@link Long}</td></tr>
 *     <tr><td>{@link ShortType}</td><td>{@link Short}</td></tr>
 * </table>
 * <br>
 * <b>Concrete</b> {@link NumberType}'s are always subclasses for a <b>base</b> <code>CONCRETE_TYPE</code>, such as the OrderId:
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
 * <br>
 * <b>Note:</b> You can expand with additional supported {@link Number} types by adding an abstract class that extends the {@link NumberType} and supply the given <code>NUMBER_TYPE</code>.<br>
 * <br>
 * Example for how the {@link Long} type is supported:
 * <pre>{@code
 * public abstract class LongType<CONCRETE_TYPE extends LongType<CONCRETE_TYPE>> extends NumberType<Long, CONCRETE_TYPE> {
 *
 *     public LongType(Long value) {
 *         super(value);
 *     }
 *
 *     @Override
 *     public int compareTo(CONCRETE_TYPE o) {
 *         return value.compareTo(o.longValue());
 *     }
 * }
 * }</pre>
 *
 * @param <NUMBER_TYPE>   The type of {@link Number} the <code>CONCRETE_TYPE</code> type encapsulated
 * @param <CONCRETE_TYPE> The concrete {@link NumberType} implementation
 */
public abstract class NumberType<NUMBER_TYPE extends Number, CONCRETE_TYPE extends NumberType<NUMBER_TYPE, CONCRETE_TYPE>> extends Number implements SingleValueType<NUMBER_TYPE, CONCRETE_TYPE> {
    protected final NUMBER_TYPE value;

    public NumberType(NUMBER_TYPE value) {
        FailFast.requireNonNull(value, "You must provide a value");
        this.value = value;
    }

    @Override
    public NUMBER_TYPE value() {
        return value;
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(this.getClass().isAssignableFrom(o.getClass()))) {
            return false;
        }

        var that = (NumberType<?, ?>) o;
        return value.equals(that.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
