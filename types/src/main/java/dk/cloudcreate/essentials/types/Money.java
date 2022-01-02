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

import java.io.Serializable;
import java.math.*;
import java.util.Objects;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

/**
 * Represents an immutable Monetary {@link Amount} combined with a {@link CurrencyCode}<br>
 * All operations working on two {@link Money} instances, such as {@link #add(Money)} will
 * throw {@link NotTheSameCurrenciesException} if the two {@link Money} instances don't share the same {@link #getCurrency()}
 */
public class Money implements Serializable, Comparable<Money> {
    private Amount       amount;
    private CurrencyCode currency;

    /**
     * The default constructor purely exists to support frameworks that require
     * a default constructor (such as Hibernate)<br>
     * <b>WARNING: Don't call this constructor</b> from your own code, either use {@link Money#Money(Amount, CurrencyCode)} or one
     * of the many <b>of</b> methods, such as {@link Money#of(Amount, CurrencyCode)}/{@link Money#of(String, String)}/etc
     */
    public Money() {
    }

    public Money(Amount amount, CurrencyCode currency) {
        this.amount = requireNonNull(amount, "You must supply an amount");
        this.currency = requireNonNull(currency, "You must supply a currency");
    }

    public static Money of(String amount, String currencyCode) {
        return new Money(Amount.of(amount), CurrencyCode.of(currencyCode));
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(Amount.of(amount), CurrencyCode.of(currencyCode));
    }

    public static Money of(String amount, CurrencyCode currencyCode) {
        return new Money(Amount.of(amount), currencyCode);
    }

    public static Money of(BigDecimal amount, CurrencyCode currencyCode) {
        return new Money(Amount.of(amount), currencyCode);
    }

    public static Money of(Amount amount, String currencyCode) {
        return new Money(amount, CurrencyCode.of(currencyCode));
    }

    public static Money of(Amount amount, CurrencyCode currencyCode) {
        return new Money(amount, currencyCode);
    }

    public Amount getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    // ---------------------------- Amount related methods ----------------------------

    public Money add(Money augend, MathContext mc) {
        requireNonNull(augend, "augend is null");
        if (!this.currency.equals(augend.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot add. This money has currency {} where as the augend money has currency {}",
                                                        this.currency,
                                                        augend.currency));
        }
        return Money.of(this.amount.add(augend.amount, mc), this.currency);
    }

    public Money subtract(Money subtrahend, MathContext mc) {
        requireNonNull(subtrahend, "subtrahend is null");
        if (!this.currency.equals(subtrahend.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot subtract. This money has currency {} where as the subtrahend money has currency {}",
                                                        this.currency,
                                                        subtrahend.currency));
        }
        return Money.of(this.amount.subtract(subtrahend.amount, mc), this.currency);
    }

    public Money multiply(Money multiplicand, MathContext mc) {
        requireNonNull(multiplicand, "multiplicand is null");
        if (!this.currency.equals(multiplicand.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot multiply. This money has currency {} where as the multiplicand money has currency {}",
                                                        this.currency,
                                                        multiplicand.currency));
        }
        return Money.of(this.amount.multiply(multiplicand.amount, mc), this.currency);
    }

    public Money divide(Money divisor, RoundingMode roundingMode) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot divide. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.divide(divisor.amount, roundingMode), this.currency);
    }

    public Money divide(Money divisor, MathContext mc) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot divide. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.divide(divisor.amount, mc), this.currency);
    }


    public Money remainder(Money divisor, MathContext mc) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot calculate remainder. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.remainder(divisor.amount, mc), this.currency);
    }

    public Money add(Money augend) {
        requireNonNull(augend, "augend is null");
        if (!this.currency.equals(augend.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot add. This money has currency {} where as the augend money has currency {}",
                                                        this.currency,
                                                        augend.currency));
        }
        return Money.of(this.amount.add(augend.amount), this.currency);
    }

    public Money subtract(Money subtrahend) {
        requireNonNull(subtrahend, "subtrahend is null");
        if (!this.currency.equals(subtrahend.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot subtract. This money has currency {} where as the subtrahend money has currency {}",
                                                        this.currency,
                                                        subtrahend.currency));
        }
        return Money.of(this.amount.subtract(subtrahend.amount), this.currency);
    }

    public Money multiply(Money multiplicand) {
        requireNonNull(multiplicand, "multiplicand is null");
        if (!this.currency.equals(multiplicand.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot multiply. This money has currency {} where as the multiplicand money has currency {}",
                                                        this.currency,
                                                        multiplicand.currency));
        }
        return Money.of(this.amount.multiply(multiplicand.amount), this.currency);
    }

    public Money divide(Money divisor, int scale, RoundingMode roundingMode) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot divide. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.divide(divisor.amount, scale, roundingMode), this.currency);
    }

    public Money divide(Money divisor) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot divide. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.divide(divisor.amount), this.currency);
    }

    public Money remainder(Money divisor) {
        requireNonNull(divisor, "divisor is null");
        if (!this.currency.equals(divisor.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot calculate remainder. This money has currency {} where as the divisor money has currency {}",
                                                        this.currency,
                                                        divisor.currency));
        }
        return Money.of(this.amount.remainder(divisor.amount), this.currency);
    }

    public Money abs(MathContext mc) {
        return Money.of(this.amount.abs(mc), this.currency);
    }

    public Money negate(MathContext mc) {
        return Money.of(this.amount.negate(mc), this.currency);
    }

    public Money plus(MathContext mc) {
        return Money.of(this.amount.plus(mc), this.currency);
    }

    public Money setScale(int newScale, RoundingMode roundingMode) {
        return Money.of(this.amount.setScale(newScale, roundingMode), this.currency);
    }

    public Money setScale(int newScale) {
        return Money.of(this.amount.setScale(newScale), this.currency);
    }

    public Money pow(int n) {
        return Money.of(this.amount.pow(n), this.currency);
    }

    public Money abs() {
        return Money.of(this.amount.abs(), this.currency);
    }

    public Money negate() {
        return Money.of(this.amount.negate(), this.currency);
    }

    public Money plus() {
        return Money.of(this.amount.plus(), this.currency);
    }

    public int signum() {
        return this.amount.signum();
    }

    public int scale() {
        return this.amount.scale();
    }

    public int precision() {
        return this.amount.precision();
    }

    public Money round(MathContext mc) {
        return Money.of(this.amount.round(mc), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    /**
     * Compare <code>compareToMoney</code> with this {@link Money} instance.<br>
     * See {@link Comparable#compareTo(Object)}
     *
     * @param compareToMoney the other {@link Money} instance the we want to compare with this {@link Money} instance
     * @return a negative integer, zero, or a positive integer as this {@link Money} is less than, equal to, or greater than the specified <code>compareToMoney</code> instance.
     * @throws NotTheSameCurrenciesException If the two {@link Money} instances don't share the same {@link #getCurrency()}
     */
    @Override
    public int compareTo(Money compareToMoney) {
        if (!this.currency.equals(compareToMoney.currency)) {
            throw new NotTheSameCurrenciesException(msg("Cannot compare. This money has currency {} where as the compareTo money has currency {}",
                                                        this.currency,
                                                        compareToMoney.currency));
        }
        return this.amount.compareTo(compareToMoney.amount);
    }

    public static class NotTheSameCurrenciesException extends RuntimeException {
        public NotTheSameCurrenciesException(String message) {
            super(message);
        }

        public NotTheSameCurrenciesException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
