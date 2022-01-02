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

class CountryCodeTest {
    @Test
    void test_a_known_country_code_can_be_converted_to_a_typed_country_code() {
        var DK = CountryCode.of("DK");
        var dk = new CountryCode("dk");

        assertThat((CharSequence) DK).isNotNull();
        assertThat(DK.toString()).isEqualTo("DK");
        assertThat((CharSequence) dk).isNotNull();
        assertThat(dk.toString()).isEqualTo("DK");

        assertThat((CharSequence) dk).isEqualTo(DK);
    }

    @Test
    void test_an_unknown_country_code_can_not_be_converted_to_a_typed_country_code() {
        assertThatThrownBy(() -> CountryCode.of("DI"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_an_a_non_3_characters_country_code_can_not_be_converted_to_a_typed_country_code() {
        assertThatThrownBy(() -> new CountryCode("A"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CountryCode.of("DKK"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}