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

package dk.cloudcreate.essentials.shared.collections;

import dk.cloudcreate.essentials.shared.functional.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ListsTest {
    @Test
    void an_empty_list_returns_an_empty_stream() {
        assertThat(Lists.toIndexedStream(List.of())).isEmpty();
    }

    @Test
    void an_indexed_stream_is_returned() {
        // Given
        var elements = List.of("A", "B", "C");

        // When
        var indexedStream = Lists.toIndexedStream(elements);

        // Then
        var result = indexedStream.collect(Collectors.toList());
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo(Tuple.of(0, "A"));
        assertThat(result.get(1)).isEqualTo(Tuple.of(1, "B"));
        assertThat(result.get(2)).isEqualTo(Tuple.of(2, "C"));
    }
}