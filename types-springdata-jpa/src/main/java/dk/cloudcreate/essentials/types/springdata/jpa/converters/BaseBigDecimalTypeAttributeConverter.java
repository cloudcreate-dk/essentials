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
import java.math.BigDecimal;

/**
 * Base implementation for all JPA {@link AttributeConverter}'s that can convert between a concrete {@link BigDecimalType} sub-class
 * and a database {@link Long} value.<br>
 * Example:
 * <pre>{@code
 * @Converter(autoApply = true)
 * public class AmountAttributeConverter extends BaseBigDecimalTypeAttributeConverter<Amount> {
 *     @Override
 *     protected Class<Amount> getConcreteBigDecimalType() {
 *         return Amount.class;
 *     }
 * }}</pre>
 *
 * @param <T> the concrete type of {@link BigDecimalType} supported by this converter
 */
public abstract class BaseBigDecimalTypeAttributeConverter<T extends BigDecimalType<T>> implements AttributeConverter<T, Double> {
    @Override
    public Double convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.doubleValue() : null;
    }

    @Override
    public T convertToEntityAttribute(Double dbData) {
        if (dbData == null) return null;
        return SingleValueType.from(new BigDecimal(dbData), getConcreteBigDecimalType());
    }

    /**
     * Override this method to return the concrete {@link BigDecimalType} sub-class  supported by this converter
     */
    protected abstract Class<T> getConcreteBigDecimalType();
}
