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

class AmountTest {
    @Test
    void test_constructor_and_of_methods() {
        var amountAsString   = "125.67";
        var amountAsBigDecimal = new BigDecimal(amountAsString);

        // Constructor
        var amount = new Amount(amountAsBigDecimal);
        assertThat(amount.value).isEqualTo(new BigDecimal(amountAsString));
        assertThat(amount).isEqualTo(amount);
        assertThat(amount).isEqualTo(amount);
        assertThat(amount.hashCode()).isEqualTo(amount.hashCode());

        // Of methods
        assertThat(Amount.of(amountAsString)).isEqualTo(amount);
        assertThat(Amount.of(amountAsBigDecimal)).isEqualTo(amount);
    }

    @Test
    void test_constructor_and_of_methods_argument_null_check() {
        assertThatThrownBy(() -> new Amount(null))
                .isInstanceOf(IllegalArgumentException.class);

        // Of methods
        assertThatThrownBy(() -> Amount.of((String)null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Amount.of((BigDecimal)null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_toString() {
        var amountAsString   = "125.67";
        var amount           = Amount.of(amountAsString);

        assertThat(amount.toString()).isEqualTo(amountAsString);
    }

    /**
     * Verification that: <code>(CONCRETE_TYPE) SingleValueType.from(value.add(augend.value), this.getClass());</code> works
     */
    @Test
    void test_add() {
        // Given
        var amount1 = Amount.of("100.5");
        assertThat(amount1.value).isEqualTo(new BigDecimal("100.5"));
        var amount2 = Amount.of("95.5");
        assertThat(amount2.value).isEqualTo(new BigDecimal("95.5"));

        // When
        var sum = amount1.add(amount2);
        assertThat(sum).isEqualTo(Amount.of("196.0"));
        assertThat(sum.toString()).isEqualTo("196.0");
        assertThat(sum.value).isEqualTo(new BigDecimal("196.0"));
    }

    @Test
    void test_calculating_total_amount_with_sales_tax_calculated_using_percentage() {
        // Given
        Amount     totalAmountWithoutSalesTax = Amount.of("100.00");
        Percentage salesTaxPct                = Percentage.from("25");

        // When
        Amount     salesTaxAmount             = salesTaxPct.of(totalAmountWithoutSalesTax);
        Amount     totalSales                 = totalAmountWithoutSalesTax.add(salesTaxAmount);

        // Then
        assertThat(totalSales).isEqualTo(Amount.of("125.00"));
    }

    @Test
    void test_methods_accepting_a_amount_as_argument() {
        var thisAmount   = Amount.of("99.50");
        var thatAmount   = Amount.of("101.50");
        var mc           = new MathContext(10, RoundingMode.HALF_UP);
        var roundingMode = RoundingMode.HALF_UP;

        assertThat(thisAmount.add(thatAmount))
                .isEqualTo(Amount.of("201.00"));
        assertThat(thisAmount.add(thatAmount, mc))
                .isEqualTo(Amount.of("201.00"));

        assertThat(thisAmount.subtract(thatAmount))
                .isEqualTo(Amount.of("-2.00"));
        assertThat(thisAmount.subtract(thatAmount, mc))
                .isEqualTo(Amount.of("-2.00"));

        assertThat(thisAmount.multiply(thatAmount))
                .isEqualTo(Amount.of("10099.2500"));
        assertThat(thisAmount.multiply(thatAmount, mc))
                .isEqualTo(Amount.of("10099.2500"));

        assertThat(Amount.of("50.00").divide(Amount.of("100.00")))
                .isEqualTo(Amount.of("0.5"));
        assertThat(thisAmount.divide(thatAmount, mc))
                .isEqualTo(Amount.of("0.9802955665"));
        assertThat(thisAmount.divide(thatAmount, roundingMode))
                .isEqualTo(Amount.of("0.98"));
        assertThat(thisAmount.divide(thatAmount, 4, roundingMode))
                .isEqualTo(Amount.of("0.9803"));


        assertThat(thisAmount.remainder(thatAmount))
                .isEqualTo(thisAmount);
        assertThat(thatAmount.remainder(thisAmount))
                .isEqualTo(Amount.of("2.00"));

        assertThat(thisAmount.remainder(thatAmount, mc))
                .isEqualTo(thisAmount);
        assertThat(thatAmount.remainder(thisAmount, mc))
                .isEqualTo(Amount.of("2.00"));

        assertThat(thisAmount.compareTo(thatAmount))
                .isEqualTo(-1);
        assertThat(thisAmount.compareTo(thisAmount))
                .isEqualTo(0);
        assertThat(thatAmount.compareTo(thisAmount))
                .isEqualTo(1);
    }

    @Test
    void test_remaining_methods() {
        var amount       = Amount.of("-999.50");
        var mc           = new MathContext(4, RoundingMode.HALF_UP);
        var roundingMode = RoundingMode.HALF_UP;

        assertThat(amount.abs())
                .isEqualTo(Amount.of("999.50"));
        assertThat(amount.abs(mc))
                .isEqualTo(Amount.of("999.5"));

        assertThat(amount.negate())
                .isEqualTo(Amount.of("999.50"));
        assertThat(amount.negate(mc))
                .isEqualTo(Amount.of("999.5"));

        assertThat(amount.plus())
                .isEqualTo(Amount.of("-999.50"));
        assertThat(amount.plus(mc))
                .isEqualTo(Amount.of("-999.5"));

        assertThat(amount.signum())
                .isEqualTo(-1);
        assertThat(Amount.of("0.00").signum())
                .isEqualTo(0);
        assertThat(amount.negate().signum())
                .isEqualTo(1);

        assertThat(amount.scale())
                .isEqualTo(2);

        assertThat(amount.precision())
                .isEqualTo(5);

        assertThat(amount.round(mc))
                .isEqualTo(Amount.of("-999.5"));
    }
}