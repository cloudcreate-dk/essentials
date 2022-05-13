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

package dk.cloudcreate.essentials.shared.types;

import java.lang.reflect.*;
import java.util.Optional;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

/**
 * Using this class makes it possible to capture a generic/parameterized argument type, such as <code>List&lt;Money></code>,
 * instead of having to rely on the classical <code><b>.class</b></code> construct.<br>
 * When you specify a type reference using <code><b>.class</b></code> you loose any Generic/Parameterized information, as you cannot write <code>List&lt;Money>.class</code>,
 * only <code>List.class</code>.<br>
 * <br>
 * <b>With {@link GenericType} you can specify and capture parameterized type information</b><br>
 * <br>
 * <b>You can specify a parameterized type using {@link GenericType}:</b><br>
 * <blockquote>
 * <code>{@literal var genericType = new GenericType<List<Money>>(){};}</code><br>
 * <br>
 * The {@link GenericType#type}/{@link GenericType#getType()} will return <code>List.class</code><br>
 * And {@link GenericType#genericType}/{@link GenericType#getGenericType()} will return {@link ParameterizedType}, which can be introspected further<br>
 * </blockquote>
 * <br>
 * <br>
 * <b>You can also supply a non-parameterized type to {@link GenericType}:</b><br>
 * <blockquote>
 * <code>{@literal var genericType = new GenericType<Money>(){};}</code><br>
 * <br>
 * In which case {@link GenericType#type}/{@link GenericType#getType()} will return <code>Money.class</code><br>
 * And {@link GenericType#genericType}/{@link GenericType#getGenericType()} will return <code>Money.class</code>
 * </blockquote>
 *
 * @param <T> the type provided to the {@link GenericType}
 */
public abstract class GenericType<T> {
    public final Class<T> type;
    public final Type     genericType;

    @SuppressWarnings("unchecked")
    public GenericType() {
        var genericSuperClass = this.getClass().getGenericSuperclass();
        if (genericSuperClass instanceof ParameterizedType) {
            genericType = ((ParameterizedType) genericSuperClass).getActualTypeArguments()[0];
            if (genericType instanceof Class) {
                type = (Class<T>) genericType;
            } else {
                // Use the raw type
                type = (Class<T>) ((ParameterizedType) genericType).getRawType();
            }
        } else {
            throw new IllegalStateException("No generic type information available");
        }
    }

    /**
     * Resolve the concrete parameterized type a given <code>forType</code> has specified with its direct super class.<br>
     * Example:<br>
     * <b>Given super-class:</b><br>
     * <code>{@literal class AggregateRoot<ID, AGGREGATE_TYPE extends AggregateRoot<ID, AGGREGATE_TYPE>> implements Aggregate<ID>}</code><br>
     * <br>
     * <b>And sub/concrete class:</b><br>
     * <code>{@literal class Order extends AggregateRoot<OrderId, Order>}</code>br>
     * <br>
     * <b>Then:</b><br>
     * <code>GenericType.resolveGenericType(Order.class, 0)</code> will return <code>OrderId.class</code><br>
     * <br>
     * <b>and</b><br>
     * <code>GenericType.resolveGenericType(Order.class, 1)</code> will return <code>Order.class</code><br>
     *
     * @param forType           the type that is specifying a parameterized type with its super-class
     * @param typeArgumentIndex the index in the superclasses type parameters (0 based)
     * @return an optional with the parameterized type or {@link Optional#empty()} if the type couldn't resolved
     */
    public static Optional<Class<?>> resolveGenericType(Class<?> forType, int typeArgumentIndex) {
        requireNonNull(forType, "No forType provided");
        var genericSuperClass = forType.getGenericSuperclass();
        if (genericSuperClass instanceof ParameterizedType) {
            var genericType = ((ParameterizedType) genericSuperClass).getActualTypeArguments()[typeArgumentIndex];
            if (genericType instanceof Class) {
                return Optional.of((Class<?>) genericType);
            } else {
                // Use the raw type
                return Optional.of((Class<?>) ((ParameterizedType) genericType).getRawType());
            }
        } else {
            throw new IllegalStateException(msg("No generic type information available on type '{}' for typeArgument with index {}",
                                                forType.getName(),
                                                typeArgumentIndex));
        }
    }

    public Class<T> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }
}
