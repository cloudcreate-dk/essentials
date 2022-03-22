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


import dk.cloudcreate.essentials.types.ids.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CharSequenceTypeTest {
    @Test
    void test_value() {
        assertThat(CustomerId.of("Test").value()).isEqualTo("Test");
        assertThat(new CustomerId("Test").value()).isEqualTo("Test");
    }

    @Test
    void test_equals() {
        assertThat((CharSequence) CustomerId.of("Test")).isEqualTo(new CustomerId("Test"));

        assertThat((CharSequence) CustomerId.of("Test")).isEqualTo(CustomerId.of("Test"));
        assertThat(CustomerId.of("Test").equals(CustomerId.of("Test"))).isTrue();

        assertThat((CharSequence) CustomerId.of("Test")).isNotEqualTo(CustomerId.of("Test2"));
        assertThat(CustomerId.of("Test").equals(CustomerId.of("Test2"))).isFalse();

        // A CustomerId and a ProductId with the same value aren't equal
        assertThat((CharSequence) ProductId.of("Test")).isNotEqualTo(CustomerId.of("Test2"));
        assertThat(ProductId.of("Test").equals(CustomerId.of("Test2"))).isFalse();
    }

    @Test
    void test_hashCode() {
        assertThat(CustomerId.of("Test").hashCode()).isEqualTo(CustomerId.of("Test").hashCode());
        assertThat(CustomerId.of("Test").hashCode()).isNotEqualTo(CustomerId.of("Test2").hashCode());
    }

    @Test
    void test_comparable() {
        assertThat(CustomerId.of("Test").compareTo(CustomerId.of("Test"))).isEqualTo(0);
        assertThat(CustomerId.of("Test").compareTo("Test")).isEqualTo(0);

        assertThat(CustomerId.of("Test").compareTo(CustomerId.of("Test2"))).isNotEqualTo(0);
        assertThat(CustomerId.of("Test").compareTo("Test2")).isNotEqualTo(0);
    }

    @Test
    void test_toString() {
        assertThat(CustomerId.of("Test").toString()).isEqualTo("Test");
    }

    @Test
    void test_substring() {
        ProductId productId = ProductId.of("Some-product-id");
        ProductId partOfId = productId.substring(2);
        assertThat((CharSequence) partOfId).isEqualTo(ProductId.of("me-product-id"));
    }

    @Test
    void test_substring_with_index() {
        ProductId productId = ProductId.of("Some-product-id");
        ProductId partOfId = productId.substring(2,5);
        assertThat((CharSequence) partOfId).isEqualTo(ProductId.of("me-"));
    }
}
