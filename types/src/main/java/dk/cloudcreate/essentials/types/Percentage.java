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

import java.math.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Immutable Percentage concept, where a {@link BigDecimal} value of "100" is the same as 100%<br>
 */
public class Percentage extends BigDecimalType<Percentage> {
    public static final Percentage _100 = new Percentage(new BigDecimal("100.00"));
    public static final  Percentage _0                        = new Percentage(new BigDecimal("0.00"));

    public Percentage(BigDecimal value) {
        super(validate(value));
    }

    private static BigDecimal validate(BigDecimal value) {
        requireNonNull(value, "value is null");
        return value.scale() < 2 ? value.setScale(2) : value;
    }

    /**
     * Convert a Percentage string representation, where "0" is 0% and "100" is 100%.<br>
     * If the string representation contains a "%" character, then it will be ignored.
     *
     * @param percent the percentage string
     * @return the corresponding Percentage instance
     */
    public static Percentage from(String percent) {
        requireNonNull(percent, "Supplied percent is null");
        var cleanedPercentageString = percent.replace('%', ' ')
                                             .trim();
        return new Percentage(new BigDecimal(cleanedPercentageString));
    }

    /**
     * Convert a Percentage {@link BigDecimal} representation, where "0" is 0% and  "100" is 100%.
     *
     * @param percent the percentage string
     * @return the corresponding Percentage instance
     */
    public static Percentage from(BigDecimal percent) {
        requireNonNull(percent, "Supplied percent is null");
        return new Percentage(percent);
    }

    @Override
    public String toString() {
        return value + "%";
    }

    /**
     * Calculate how much this {@link Percentage} is of the supplied amount<br>
     * If this percentage instance if set to 40%, and you supply an amount of 200, then the returned value will be
     * 40% of 200 = 80
     *
     * @param amount the amount that we want to calculate a percentage of
     * @param <T>    the return type
     * @return the number of percent of the <code>amount</code> as the same type that was supplied.
     */
    @SuppressWarnings("unchecked")
    public <T extends BigDecimalType<T>> T of(T amount) {
        requireNonNull(amount, "Supplied amount is null");
        return (T) SingleValueType.from(amount.value.multiply(this.value().divide(_100.value)).setScale(Math.max(amount.scale(), this.scale()), RoundingMode.HALF_UP),
                                        amount.getClass());
    }

    /**
     * Calculate how much this {@link Percentage} is of the supplied amount<br>
     * If this percentage instance if set to 40%, and you supply an amount of 200, then the returned value will be
     * 40% of 200 = 80
     *
     * @param amount the amount that we want to calculate a percentage of
     * @return the number of percent of the <code>amount</code> as the same type that was supplied.
     */
    public BigDecimal of(BigDecimal amount) {
        requireNonNull(amount, "Supplied amount is null");
        return amount.multiply(this.value().divide(_100.value));
    }
}
