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

package dk.cloudcreate.essentials.shared.functional.tuple;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PairTest {
    @Test
    void verify_equals_and_hashCode() {
        var pair1 = Pair.of("Text", 100);
        var pair2 = Pair.of("Text", 100);
        var pair3 = Pair.of("Text!", 100);
        var pair4 = Pair.of("Text", 101);
        var pair5 = Pair.of(100, "Text");

        assertThat(pair1).isEqualTo(pair1);
        assertThat(pair1).isEqualTo(pair2);
        assertThat(pair1).isNotEqualTo(pair3);
        assertThat(pair1).isNotEqualTo(pair4);
        assertThat(pair1).isNotEqualTo(pair5);

        assertThat(pair1.hashCode()).isEqualTo(pair1.hashCode());
        assertThat(pair1.hashCode()).isEqualTo(pair2.hashCode());
        assertThat(pair1.hashCode()).isNotEqualTo(pair3.hashCode());
        assertThat(pair1.hashCode()).isNotEqualTo(pair4.hashCode());
        assertThat(pair1.hashCode()).isNotEqualTo(pair5.hashCode());

        assertThat(pair1.swap()).isEqualTo(pair5);
        assertThat(pair1.swap().hashCode()).isEqualTo(pair5.hashCode());
    }
}