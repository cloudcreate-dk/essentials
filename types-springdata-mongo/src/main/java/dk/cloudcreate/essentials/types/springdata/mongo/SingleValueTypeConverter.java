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
import org.bson.types.Decimal128;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Set;

/**
 * {@link GenericConverter} that supports converting the following {@link SingleValueType} subtypes:
 * {@link CharSequenceType} to {@link String} and {@link String} to {@link CharSequenceType}<br>
 * {@link NumberType} to {@link Number} and {@link Number}/{@link Decimal128} to {@link NumberType}
 */
public class SingleValueTypeConverter implements GenericConverter {
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(CharSequenceType.class, String.class),
                      new ConvertiblePair(String.class, CharSequenceType.class),
                      new ConvertiblePair(NumberType.class, Number.class),
                      new ConvertiblePair(Number.class, NumberType.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source instanceof SingleValueType) {
            return ((SingleValueType<?, ?>) source).value();
        } else {
            var convertFromValue = source;
            if (convertFromValue instanceof Decimal128) {
                convertFromValue = ((Decimal128) convertFromValue).bigDecimalValue();
            }
            return SingleValueType.fromObject(convertFromValue, (Class<SingleValueType<?, ?>>) targetType.getType());
        }
    }
}
