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

package dk.cloudcreate.essentials.types.springdata.mongo;

import dk.cloudcreate.essentials.types.*;
import org.bson.types.*;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.*;
import java.util.stream.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * {@link GenericConverter} that supports converting the following {@link SingleValueType} subtypes:
 * {@link CharSequenceType} to {@link String} and {@link String} to {@link CharSequenceType}<br>
 * {@link NumberType} to {@link Number} and {@link Number}/{@link Decimal128} to {@link NumberType}
 */
public class SingleValueTypeConverter implements GenericConverter {
    private final List<Class<? extends CharSequenceType<?>>> explicitCharSequenceTypeToObjectIdConverters;

    /**
     * If a concrete {@link CharSequenceType} subtype can contain an {@link ObjectId#toString()} as value, such as
     * <the code>ProductId</code> defined here, which in its <code>random</code> method returns
     * <code>new ProductId(ObjectId.get().toString())</code>:
     * <pre>{@code
     * public class ProductId extends CharSequenceType<ProductId> implements Identifier {
     *     public ProductId(CharSequence value) {
     *         super(value);
     *     }
     *
     *     public static ProductId of(CharSequence value) {
     *         return new ProductId(value);
     *     }
     *
     *     public static ProductId random() {
     *         return new ProductId(ObjectId.get().toString());
     *     }
     * }
     * }</pre>
     * Then you have to explicitly define that type, in this case the <code>ProductId</code>, must be convertable
     * to and from {@link ObjectId} when configuring the {@link org.springframework.data.mongodb.core.convert.MongoCustomConversions}:
     * <pre>{@code
     * @Bean
     * public MongoCustomConversions mongoCustomConversions() {
     *     return new MongoCustomConversions(List.of(
     *             new SingleValueTypeConverter(ProductId.class)));
     * }
     * }</pre>
     *
     * @param explicitCharSequenceTypeToObjectIdConverters the list of all concrete {@link CharSequenceType}'s that must be convertable
     *                                                     to and from {@link ObjectId}
     */
    @SafeVarargs
    public SingleValueTypeConverter(Class<? extends CharSequenceType<?>>... explicitCharSequenceTypeToObjectIdConverters) {
        this.explicitCharSequenceTypeToObjectIdConverters = List.of(explicitCharSequenceTypeToObjectIdConverters);
    }

    /**
     * If a concrete {@link CharSequenceType} subtype can contain an {@link ObjectId#toString()} as value, such as
     * <the code>ProductId</code> defined here, which in its <code>random</code> method returns
     * <code>new ProductId(ObjectId.get().toString())</code>:
     * <pre>{@code
     * public class ProductId extends CharSequenceType<ProductId> implements Identifier {
     *     public ProductId(CharSequence value) {
     *         super(value);
     *     }
     *
     *     public static ProductId of(CharSequence value) {
     *         return new ProductId(value);
     *     }
     *
     *     public static ProductId random() {
     *         return new ProductId(ObjectId.get().toString());
     *     }
     * }
     * }</pre>
     * Then you have to explicitly define that type, in this case the <code>ProductId</code>, must be convertable
     * to and from {@link ObjectId} when configuring the {@link org.springframework.data.mongodb.core.convert.MongoCustomConversions}:
     * <pre>{@code
     * @Bean
     * public MongoCustomConversions mongoCustomConversions() {
     *     return new MongoCustomConversions(List.of(
     *             new SingleValueTypeConverter(List.of(ProductId.class))));
     * }
     * }</pre>
     *
     * @param explicitCharSequenceTypeToObjectIdConverters the list of all concrete {@link CharSequenceType}'s that must be convertable
     *                                                     to and from {@link ObjectId}
     */
    public SingleValueTypeConverter(List<Class<? extends CharSequenceType<?>>> explicitCharSequenceTypeToObjectIdConverters) {
        this.explicitCharSequenceTypeToObjectIdConverters = requireNonNull(explicitCharSequenceTypeToObjectIdConverters, "No list of explicitCharSequenceTypeToObjectIdConverters provided");
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        var allConverters = new HashSet<ConvertiblePair>();
        allConverters.addAll(explicitCharSequenceTypeToObjectIdConverters.stream()
                                                                         .flatMap(singleValueType -> Stream.of(new ConvertiblePair(String.class, singleValueType),
                                                                                                               new ConvertiblePair(ObjectId.class, singleValueType)))
                                                                         .collect(Collectors.toList()));

        allConverters.addAll(Set.of(
                new ConvertiblePair(CharSequenceType.class, String.class),
                new ConvertiblePair(SingleValueType.class, Object.class), // Needed for Map Key conversions
                new ConvertiblePair(String.class, CharSequenceType.class),
                new ConvertiblePair(NumberType.class, Number.class),
                new ConvertiblePair(Number.class, NumberType.class)));

        return allConverters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source instanceof CharSequenceType && ObjectId.class.isAssignableFrom(targetType.getType()) && ObjectId.isValid(source.toString())) {
            return new ObjectId(source.toString());
        } else if (source instanceof SingleValueType) {
            return ((SingleValueType<?, ?>) source).value();
        } else if (source instanceof ObjectId) {
            return SingleValueType.fromObject(((ObjectId) source).toString(), (Class<SingleValueType<?, ?>>) targetType.getType());
        } else {
            var convertFromValue = source;
            if (convertFromValue instanceof Decimal128) {
                convertFromValue = ((Decimal128) convertFromValue).bigDecimalValue();
            }
            return SingleValueType.fromObject(convertFromValue, (Class<SingleValueType<?, ?>>) targetType.getType());
        }
    }
}
