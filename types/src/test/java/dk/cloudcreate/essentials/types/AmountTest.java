/*
 * Copyright 2021 the original author or authors.
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmountTest {
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
    void test_calculates_total_amount_combined_with_percentage() {
        Amount     totalAmountWithoutSalesTax = Amount.of("100.00");
        Percentage salesTaxPct                = Percentage.from("25");
        Amount     salesTaxAmount             = salesTaxPct.of(totalAmountWithoutSalesTax);
        Amount     totalSales                 = totalAmountWithoutSalesTax.add(salesTaxAmount);

        assertThat(totalSales).isEqualTo(Amount.of("125.00"));

    }
}