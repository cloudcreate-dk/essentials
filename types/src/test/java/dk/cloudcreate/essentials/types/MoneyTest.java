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

import org.junit.jupiter.api.Test;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {
    @Test
    void test_constructor_and_of_methods() {
        var amountAsString   = "125.67";
        var amount           = Amount.of(amountAsString);
        var currencyAsString = "DKK";
        var currencyCode     = CurrencyCode.of(currencyAsString);

        // Constructor
        var expectedMoney = new Money(amount, currencyCode);
        assertThat(expectedMoney.getAmount().value).isEqualTo(new BigDecimal(amountAsString));
        assertThat(expectedMoney.getAmount()).isEqualTo(amount);
        assertThat(expectedMoney.getCurrency().value()).isEqualTo(currencyAsString);
        assertThat((CharSequence) expectedMoney.getCurrency()).isEqualTo(currencyCode);
        assertThat(expectedMoney).isEqualTo(expectedMoney);
        assertThat(expectedMoney.hashCode()).isEqualTo(expectedMoney.hashCode());

        // Of methods
        assertThat(Money.of(amount, currencyCode)).isEqualTo(expectedMoney);
        assertThat(Money.of(amount, currencyAsString)).isEqualTo(expectedMoney);
        assertThat(Money.of(new BigDecimal(amountAsString), currencyCode)).isEqualTo(expectedMoney);
        assertThat(Money.of(new BigDecimal(amountAsString), currencyAsString)).isEqualTo(expectedMoney);
        assertThat(Money.of(amountAsString, currencyCode)).isEqualTo(expectedMoney);
        assertThat(Money.of(amountAsString, currencyAsString)).isEqualTo(expectedMoney);
    }

    @Test
    void test_constructor_and_of_methods_null_checks() {
        // Constructor
        assertThatThrownBy(() ->  new Money(null, CurrencyCode.DKK))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  new Money(Amount.of("100"), null))
                .isInstanceOf(IllegalArgumentException.class);

        // Of methods
        assertThatThrownBy(() ->  Money.of((Amount)null, CurrencyCode.DKK))
                           .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of(Amount.of("100"), (CurrencyCode) null))
                           .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->  Money.of((Amount)null, "DKK"))
                           .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of(Amount.of("100"), (String)null))
                           .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->  Money.of(new BigDecimal("100"), (CurrencyCode) null))
                           .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of((BigDecimal)null, CurrencyCode.DKK))
                           .isInstanceOf(IllegalArgumentException.class);


        assertThatThrownBy(() ->  Money.of(new BigDecimal("100"), (String) null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of((BigDecimal)null, "DKK"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->  Money.of((String)null, CurrencyCode.DKK))
                           .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of("100", (CurrencyCode) null))
                           .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->  Money.of((String)null, "DKK"))
                           .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->  Money.of("100", (String) null))
                           .isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    void test_toString() {
        var amountAsString   = "125.67";
        var amount           = Amount.of(amountAsString);
        var currencyAsString = "DKK";
        var currencyCode     = CurrencyCode.of(currencyAsString);
        var money            = new Money(amount, currencyCode);

        assertThat(money.toString()).isEqualTo(amountAsString + " " + currencyAsString);
        assertThat(money.toString()).isEqualTo(amount + " " + currencyCode);
    }

    @Test
    void verify_methods_accepting_a_money_as_argument_will_not_accept_different_currencies() {
        var thisMoney    = Money.of("100", CurrencyCode.DKK);
        var thatMoney    = Money.of("100", CurrencyCode.EUR);
        var mc           = new MathContext(10, RoundingMode.HALF_UP);
        var roundingMode = RoundingMode.HALF_UP;

        assertThatThrownBy(() -> thisMoney.add(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.add(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.subtract(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.subtract(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.multiply(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.multiply(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.divide(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.divide(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.divide(thatMoney, roundingMode))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.divide(thatMoney, 2, roundingMode))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.remainder(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.remainder(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.remainder(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
        assertThatThrownBy(() -> thisMoney.remainder(thatMoney, mc))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);

        assertThatThrownBy(() -> thisMoney.compareTo(thatMoney))
                .isInstanceOf(Money.NotTheSameCurrenciesException.class);
    }

    @Test
    void test_methods_accepting_a_money_as_argument() {
        var thisMoney    = Money.of("99.50", CurrencyCode.DKK);
        var thatMoney    = Money.of("101.50", CurrencyCode.DKK);
        var mc           = new MathContext(10, RoundingMode.HALF_UP);
        var roundingMode = RoundingMode.HALF_UP;

        assertThat(thisMoney.add(thatMoney))
                .isEqualTo(Money.of("201.00", CurrencyCode.DKK));
        assertThat(thisMoney.add(thatMoney, mc))
                .isEqualTo(Money.of("201.00", CurrencyCode.DKK));

        assertThat(thisMoney.subtract(thatMoney))
                .isEqualTo(Money.of("-2.00", CurrencyCode.DKK));
        assertThat(thisMoney.subtract(thatMoney, mc))
                .isEqualTo(Money.of("-2.00", CurrencyCode.DKK));

        assertThat(thisMoney.multiply(thatMoney))
                .isEqualTo(Money.of("10099.2500", CurrencyCode.DKK));
        assertThat(thisMoney.multiply(thatMoney, mc))
                .isEqualTo(Money.of("10099.2500", CurrencyCode.DKK));

        assertThat(Money.of("50.00", CurrencyCode.DKK).divide(Money.of("100.00", CurrencyCode.DKK)))
                .isEqualTo(Money.of("0.5", CurrencyCode.DKK));
        assertThat(thisMoney.divide(thatMoney, mc))
                .isEqualTo(Money.of("0.9802955665", CurrencyCode.DKK));
        assertThat(thisMoney.divide(thatMoney, roundingMode))
                .isEqualTo(Money.of("0.98", CurrencyCode.DKK));
        assertThat(thisMoney.divide(thatMoney, 4, roundingMode))
                .isEqualTo(Money.of("0.9803", CurrencyCode.DKK));


        assertThat(thisMoney.remainder(thatMoney))
                .isEqualTo(thisMoney);
        assertThat(thatMoney.remainder(thisMoney))
                .isEqualTo(Money.of("2.00", CurrencyCode.DKK));

        assertThat(thisMoney.remainder(thatMoney, mc))
                .isEqualTo(thisMoney);
        assertThat(thatMoney.remainder(thisMoney, mc))
                .isEqualTo(Money.of("2.00", CurrencyCode.DKK));

        assertThat(thisMoney.compareTo(thatMoney))
                .isEqualTo(-1);
        assertThat(thisMoney.compareTo(thisMoney))
                .isEqualTo(0);
        assertThat(thatMoney.compareTo(thisMoney))
                .isEqualTo(1);
    }

    @Test
    void test_remaining_methods() {
        var money        = Money.of("-999.50", CurrencyCode.DKK);
        var mc           = new MathContext(4, RoundingMode.HALF_UP);
        var roundingMode = RoundingMode.HALF_UP;

        assertThat(money.abs())
                .isEqualTo(Money.of("999.50", CurrencyCode.DKK));
        assertThat(money.abs(mc))
                .isEqualTo(Money.of("999.5", CurrencyCode.DKK));

        assertThat(money.negate())
                .isEqualTo(Money.of("999.50", CurrencyCode.DKK));
        assertThat(money.negate(mc))
                .isEqualTo(Money.of("999.5", CurrencyCode.DKK));

        assertThat(money.plus())
                .isEqualTo(Money.of("-999.50", CurrencyCode.DKK));
        assertThat(money.plus(mc))
                .isEqualTo(Money.of("-999.5", CurrencyCode.DKK));

        assertThat(money.signum())
                .isEqualTo(-1);
        assertThat(Money.of("0.00", CurrencyCode.DKK).signum())
                .isEqualTo(0);
        assertThat(money.negate().signum())
                .isEqualTo(1);

        assertThat(money.scale())
                .isEqualTo(2);

        assertThat(money.precision())
                .isEqualTo(5);

        assertThat(money.round(mc))
                .isEqualTo(Money.of("-999.5", CurrencyCode.DKK));
    }
}