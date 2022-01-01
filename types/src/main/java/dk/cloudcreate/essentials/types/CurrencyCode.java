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

import dk.cloudcreate.essentials.shared.*;

/**
 * ISO-4217 3 character currency code. Any values provided to the constructor or {@link #of(String)}
 * will be validated for length and will ensure that the code is in UPPER CASE.<br>
 * The validation does not check if the value is a known currency code.
 */
public class CurrencyCode extends CharSequenceType<CurrencyCode> {
    public CurrencyCode(String value) {
        super(validate(value));
    }

    private static CharSequence validate(String currencyCode) {
        FailFast.requireNonNull(currencyCode, "currencyCode is null");
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException(MessageFormatter.msg("CurrencyCode is invalid (must be 3 characters): '{}'", currencyCode));
        }
        return currencyCode.toUpperCase();
    }

    public static CurrencyCode of(String currencyCode) {
        return new CurrencyCode(currencyCode);
    }
}
