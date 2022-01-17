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

package dk.cloudcreate.essentials.immutable;

import dk.cloudcreate.essentials.immutable.annotations.Exclude.*;
import dk.cloudcreate.essentials.shared.functional.tuple.Tuple;
import dk.cloudcreate.essentials.shared.reflection.Reflector;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base type {@link ImmutableValueObject} supports creating immutable (i.e. an object where its values cannot change after object instantiation/creation) <b>Value Object</b><br>
 * The core feature set of {@link ImmutableValueObject} is that it provides default implementations for {@link #toString()}, {@link #equals(Object)} and {@link #hashCode()},
 * but you're always free to override this and provide your own implementation.<br>
 * <br>
 * Example:
 * <pre>{@code
 * public class ImmutableOrder extends ImmutableValueObject {
 *     public final OrderId                  orderId;
 *     public final CustomerId               customerId;
 *     @Exclude.EqualsAndHashCode
 *     public final Percentage               percentage;
 *     @Exclude.ToString
 *     public final Money                    totalPrice;
 *
 *     public ImmutableOrder(OrderId orderId,
 *                           CustomerId customerId,
 *                           Percentage percentage,
 *                           EmailAddress email,
 *                           Money totalPrice) {
 *         this.orderId = orderId;
 *         this.customerId = customerId;
 *         this.percentage = percentage;
 *         this.totalPrice = totalPrice;
 *     }
 * }
 * }</pre>
 * <p>
 * <b>Value Object definition</b>
 * <br>
 * <blockquote>A Value Object is defined by its property values and not by an identifier.<br>
 * If two value objects, of the same type, have the <b>same property values</b> then they're considered to be <b>equal</b>.
 * </blockquote>
 * <pre>{@code
 * var thisOrder = new ImmutableOrder(OrderId.of(123456789),
 *                                    CustomerId.of("CustomerId1"),
 *                                    Percentage.from("50%"),
 *                                    Money.of("1234.56", CurrencyCode.DKK));
 *
 * var thatOrder = new ImmutableOrder(OrderId.of(123456789),
 *                                    CustomerId.of("CustomerId1"),
 *                                    Percentage.from("50%"),
 *                                    Money.of("1234.56", CurrencyCode.DKK));
 *
 * assertThat(thisOrder).isEqualTo(thatOrder);
 * }</pre>
 * <br>
 * <b>Immutability</b><br>
 * <br>
 * The default implementation of {@link #toString()} and {@link #hashCode()} relies upon the assumption that ALL non-transient instance fields are marked <b>final</b> to ensure that they cannot change after they have been assigned a value.<br>
 * To ensure that {@link #toString()} and {@link #hashCode()} calculation only happens once, the {@link ImmutableValueObject} will <b><u>cache</u></b> the output of the first call to {@link #toString()} and {@link #hashCode()}.
 * <p>
 * This also means that if a field isn't <b>final</b> or if the field type is a mutable type, such as {@link List}, {@link Map}, {@link Set}, etc. then you cannot reliably rely on the output of followup calls to {@link #toString()} and
 * {@link #hashCode()} as the fields used for calculation may have changed value.
 * <br>
 * See {@link #toString()}, {@link #hashCode()} and {@link #equals(Object)} for the logic related to each operation.
 */
public abstract class ImmutableValueObject implements Immutable {
    private transient Integer     hashCode;
    private transient String      toString;
    private transient List<Field> equalsAndHashCodeFields;

    /**
     * All fields without the {@link EqualsAndHashCode} annotation with be included the calculation of the <code>hash-code</code>.<br>
     * The fields are sorted alphabetically (ascending order) and the <code>hash-code</code> will be calculated in the order of the fields names.<br>
     * The algorithm for calculating the hashcode follows the Objects#hash(Object...) method.
     *
     * @return the calculated hashcode
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashCode == null) {
            var reflector = Reflector.reflectOn(this.getClass());
            hashCode = equalsAndHashCodeIncludedFields(reflector).stream()
                                                                 .map(field -> reflector.get(this, field))
                                                                 .map(fieldValue -> (fieldValue == null ? 0 : fieldValue.hashCode()))
                                                                 .reduce(1, (result, elementHashCode) -> 31 * result + elementHashCode);
        }
        return hashCode;
    }

    /**
     * The logic of {@link #equals(Object)} follows the standard approach where we don't accept subclasses of a type, we only accept the exact same type.<br>
     * All fields without the {@link EqualsAndHashCode}  annotation with be included in the equals operation.<br>
     * The fields are sorted alphabetically (ascending order) and values from the two objects being compared one by one.<br>
     * As soon a difference in field values is identified the comparison is stopped (to avoid wasting CPU cycles) and the result is returned.
     *
     * @param that the other object we're comparing our instance to
     * @return true if this object is the same as the that argument otherwise false
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (this == that) {
            return true;
        }
        if (!this.getClass().equals(that.getClass())) {
            return false;
        }

        var reflector = Reflector.reflectOn(this.getClass());
        for (var field : equalsAndHashCodeIncludedFields(reflector)) {
            Object thisFieldValue = reflector.get(this, field);
            Object thatFieldValue = reflector.get(that, field);
            if (!Objects.equals(thisFieldValue, thatFieldValue)) {
                return false;
            }
        }
        return true;
    }

    private List<Field> equalsAndHashCodeIncludedFields(Reflector reflector) {
        if (equalsAndHashCodeFields == null) {
            equalsAndHashCodeFields = reflector.fields.stream()
                                                      .filter(field -> !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                                                      .filter(field -> !field.isAnnotationPresent(EqualsAndHashCode.class))
                                                      .sorted(Comparator.comparing(Field::getName))
                                                      .collect(Collectors.toList());
        }
        return equalsAndHashCodeFields;
    }

    /**
     * All fields without the {@link ToString} annotation with be included in the output.<br>
     * The fields are sorted alphabetically (ascending order) and then grouped into fields with and without value.<br>
     * We will first output non-null fields (in alphabetically order) and finally null fields (in alphabetically order)<br>
     * <br>
     * Example:
     * <pre>{@code
     * public class ImmutableOrder extends ImmutableValueObject {
     *     public final OrderId                  orderId;
     *     public final CustomerId               customerId;
     *     @Exclude.EqualsAndHashCode
     *     public final Percentage               percentage;
     *     @Exclude.ToString
     *     public final Money                    totalPrice;
     *
     *     public ImmutableOrder(OrderId orderId,
     *                           CustomerId customerId,
     *                           Percentage percentage,
     *                           EmailAddress email,
     *                           Money totalPrice) {
     *         this.orderId = orderId;
     *         this.customerId = customerId;
     *         this.percentage = percentage;
     *         this.totalPrice = totalPrice;
     *     }
     * }
     *
     * new ImmutableOrder(OrderId.of(123456789),
     *                    null,
     *                    Percentage.of("50%),
     *                    Money.of("1234.56", CurrencyCode.DKK)
     *                   )
     *                   .toString()
     * }</pre>
     * will return<br>
     * <code>ImmutableOrder { orderId: 123456789, percentage: 50.00%, customerId: null }</code><br>
     * with <code>customerId</code> last (as it has a null value) and without the <code>totalPrice</code> as it is excluded from being included in the {@link #toString()}
     * result due to <code>@Exclude.ToString</code>
     *
     * @return all fields and their values (see method description)
     * @see Object#toString()
     */
    @Override
    public String toString() {
        if (toString == null) {

            var reflector = Reflector.reflectOn(this.getClass());
            var nonNullVsNullFieldValues
                    = reflector.fields
                    .stream()
                    .filter(field -> !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                    .filter(field -> !field.isAnnotationPresent(ToString.class))
                    .map(field -> Tuple.of(field.getName(),
                                           reflector.get(this, field)))
                    .collect(Collectors.groupingBy(fieldNameAndValue -> fieldNameAndValue._2 != null));

            var builder = new StringBuilder(this.getClass().getSimpleName());
            builder.append(" { ");
            // First output non-null values sorted by field name
            var nonNullFields = nonNullVsNullFieldValues.get(true);
            if (nonNullFields != null) {
                builder.append(nonNullFields
                                       .stream()
                                       .sorted(Comparator.comparing(fieldNameAndValue -> fieldNameAndValue._1))
                                       .map(fieldNameAndValue -> fieldNameAndValue._1 + ": " + fieldNameAndValue._2.toString())
                                       .reduce((result, element) -> result + ", " + element)
                                       .get());
            }

            // Next output null values sorted by field name
            var nullFields = nonNullVsNullFieldValues.get(false);
            if (nullFields != null) {
                if (nonNullFields != null) {
                    builder.append(", ");
                }
                builder.append(nullFields
                                       .stream()
                                       .sorted(Comparator.comparing(fieldNameAndValue -> fieldNameAndValue._1))
                                       .map(fieldNameAndValue -> fieldNameAndValue._1 + ": null")
                                       .reduce((result, element) -> result + ", " + element)
                                       .get());
            }
            builder.append(" }");

            toString = builder.toString();
        }
        return toString;
    }
}
