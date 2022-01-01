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

package dk.cloudcreate.essentials.types.springdata.jpa.converters;

import dk.cloudcreate.essentials.types.*;

import javax.persistence.AttributeConverter;

/**
 * Base implementation for all JPA {@link AttributeConverter}'s that can convert between a concrete {@link CharSequenceType} sub-class (e.g. such as {@link CurrencyCode})
 * and a database String value<br>
 * Example:
 * <pre>{@code
 * @Converter(autoApply = true)
 * public class CustomerIdAttributeConverter extends BaseCharSequenceTypeAttributeConverter<CustomerId> {
 *     @Override
 *     protected Class<CustomerId> getConcreteCharSequenceType() {
 *         return CustomerId.class;
 *     }
 * }
 * }</pre>
 *
 * @param <T> the concrete type of {@link CharSequenceType} supported by this converter
 */
public abstract class BaseCharSequenceTypeAttributeConverter<T extends CharSequenceType<T>> implements AttributeConverter<T, String> {
    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        return SingleValueType.from(dbData, getConcreteCharSequenceType());
    }

    /**
     * Override this method to return the concrete {@link CharSequenceType} sub-class (e.g. such as {@link CurrencyCode}) supported by this converter
     */
    protected abstract Class<T> getConcreteCharSequenceType();
}
