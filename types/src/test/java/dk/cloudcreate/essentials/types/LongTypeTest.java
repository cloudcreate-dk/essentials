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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LongTypeTest {

    @Test
    void test_value() {
        assertThat(OrderId.of(10L).value()).isEqualTo(10L);
        assertThat(new OrderId(10L).value()).isEqualTo(10L);
        assertThat(OrderId.of(10L).longValue()).isEqualTo(10L);
        assertThat(new OrderId(10L).longValue()).isEqualTo(10L);
        assertThat(OrderId.of(10L).intValue()).isEqualTo(10);
        assertThat(new OrderId(10L).intValue()).isEqualTo(10);
    }

    @Test
    void test_equals() {
        assertThat(new OrderId(10L)).isEqualTo(OrderId.of(10L));
        assertThat(OrderId.of(10L).equals(OrderId.of(10L))).isTrue();

        assertThat(OrderId.of(10L)).isNotEqualTo(OrderId.of(9L));
        assertThat(OrderId.of(10L).equals(OrderId.of(9L))).isFalse();

        // A OrderId and an AccountId with the same value aren't equal
        assertThat(OrderId.of(10L)).isNotEqualTo(AccountId.of(10L));
        assertThat(OrderId.of(10L).equals(AccountId.of(10L))).isFalse();
    }

    @Test
    void test_hashCode() {
        assertThat(OrderId.of(10L).hashCode()).isEqualTo(OrderId.of(10L).hashCode());
        assertThat(OrderId.of(10L).hashCode()).isNotEqualTo(OrderId.of(9L).hashCode());
    }

    @Test
    void test_comparable() {
        assertThat(OrderId.of(10L).compareTo(OrderId.of(10L))).isEqualTo(0);
        assertThat(OrderId.of(10L).compareTo(OrderId.of(9L))).isNotEqualTo(0);
    }

    @Test
    void test_toString() {
        assertThat(OrderId.of(10L).toString()).isEqualTo("10");
    }
}