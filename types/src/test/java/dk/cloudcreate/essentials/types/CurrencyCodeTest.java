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

import static org.assertj.core.api.Assertions.*;

class CurrencyCodeTest {
    @Test
    void verify_that_some_of_the_most_common_currencies_are_known() {
        var knownCurrencyCodes = CurrencyCode.getKnownCurrencyCodes();
        assertThat(knownCurrencyCodes).contains("EUR");
        assertThat(knownCurrencyCodes).contains("GBP");
        assertThat(knownCurrencyCodes).contains("USD");
        assertThat(knownCurrencyCodes).contains("DKK");
        assertThat(knownCurrencyCodes).contains("NOK");
        assertThat(knownCurrencyCodes).contains("SEK");
    }

    @Test
    void test_a_known_currency_code_can_be_converted_to_a_typed_currency_code() {
        var DKK = CurrencyCode.of("DKK");
        var dkk = new CurrencyCode("dkk");

        assertThat((CharSequence) DKK).isNotNull();
        assertThat(DKK.toString()).isEqualTo("DKK");
        assertThat((CharSequence) dkk).isNotNull();
        assertThat(dkk.toString()).isEqualTo("DKK");

        assertThat((CharSequence) dkk).isEqualTo(DKK);
    }

    @Test
    void test_an_unknown_currency_code_can_not_be_converted_to_a_typed_currency_code() {
        assertThatThrownBy(() -> CurrencyCode.of("UWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_an_a_non_3_characters_currency_code_can_not_be_converted_to_a_typed_currency_code() {
        assertThatThrownBy(() -> new CurrencyCode("DK"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CurrencyCode.of("DKK1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_an_unknown_currency_code_can_be_added_and_afterwards_removed() {
        var currencyCode = "MMM";
        assertThatThrownBy(() -> CurrencyCode.of(currencyCode))
                .isInstanceOf(IllegalArgumentException.class);
        CurrencyCode.addKnownCurrencyCode(currencyCode);

        assertThat(CurrencyCode.of(currencyCode).toString()).isEqualTo(currencyCode);

        CurrencyCode.removeKnownCurrencyCode(currencyCode);

        assertThatThrownBy(() -> CurrencyCode.of(currencyCode))
                .isInstanceOf(IllegalArgumentException.class);
    }
}