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

import java.math.BigDecimal;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Represents an immutable Monetary Amount without any {@link CurrencyCode}
 *
 * @see Money
 */
public class Amount extends BigDecimalType<Amount> {
    public Amount(BigDecimal value) {
        super(value);
    }

    public static Amount of(String value) {
        return new Amount(new BigDecimal(requireNonNull(value, "value is null")));
    }

    public static Amount ofNullable(String value) {
        return value != null ? new Amount(new BigDecimal(value)) : null;
    }

    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }

    public static Amount ofNullable(BigDecimal value) {
        return value != null ? new Amount(value) : null;
    }
}
