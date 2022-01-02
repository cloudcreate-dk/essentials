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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageTest {
    @Test
    void test_from() {
        var percentage = Percentage.from("50");
        assertThat(percentage.value).isEqualTo(new BigDecimal("50.00"));
        assertThat(percentage.toString()).isEqualTo("50.00%");
    }

    @Test
    void test_from_with_pct_sign() {
        var percentage = Percentage.from("51.5 %");
        assertThat(percentage.value).isEqualTo(new BigDecimal("51.50"));
        assertThat(percentage.toString()).isEqualTo("51.50%");
    }

    @Test
    void test_of() {
        var amount = Amount.of("1500.95");
        var result = Percentage.from("40").of(amount);

        assertThat(result.value).isEqualTo(new BigDecimal("600.38"));
    }

    @Test
    void test_equals_and_hashCode() {
        var amount1 = Amount.of("1500.95");
        var amount2 = Amount.of("1500.95");

        assertThat(amount1).isEqualTo(amount2);
        assertThat(amount1.hashCode()).isEqualTo(amount2.hashCode());
    }

    @Test
    void test_toString() {
        var amount = Amount.of("1500.95");

        assertThat(amount.toString()).isEqualTo("1500.95");
    }
}