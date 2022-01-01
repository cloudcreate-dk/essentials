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

import dk.cloudcreate.essentials.types.LongType;
import org.jdbi.v3.core.argument.*;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

/**
 * Base implementation for a {@link LongType}.<br>
 * Extend this class to support your concrete {@link LongType} sub-type:
 * <pre>{@code
 * public class AccountIdArgumentFactory extends LongTypeArgumentFactory<AccountId> {
 * }}</pre>
 *
 * @param <T> the concrete {@link LongType} subclass
 */
public abstract class LongTypeArgumentFactory<T extends LongType<T>> extends AbstractArgumentFactory<T> {
    public LongTypeArgumentFactory() {
        super(Types.BIGINT);
    }

    @Override
    protected Argument build(T value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setLong(position, value.value());
    }
}
