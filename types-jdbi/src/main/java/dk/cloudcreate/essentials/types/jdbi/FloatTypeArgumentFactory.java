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

package dk.cloudcreate.essentials.types.jdbi;

import dk.cloudcreate.essentials.types.FloatType;
import org.jdbi.v3.core.argument.*;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

/**
 * Base implementation for a {@link FloatType}.<br>
 * Extend this class to support your concrete {@link FloatType} sub-type:
 * <pre>{@code
 * public class AmountArgumentFactory extends FloatTypeArgumentFactory<Amount> {
 * }}</pre>
 *
 * @param <T> the concrete {@link FloatType} subclass
 */
public abstract class FloatTypeArgumentFactory<T extends FloatType<T>> extends AbstractArgumentFactory<T> {
    public FloatTypeArgumentFactory() {
        super(Types.FLOAT);
    }

    @Override
    protected Argument build(T value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setFloat(position, value.value());
    }
}
