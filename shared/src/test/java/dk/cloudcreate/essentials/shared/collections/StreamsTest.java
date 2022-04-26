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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.assertThat;

class StreamsTest {
    @Test
    void test_zipOrderedAndEqualSizedStreams() {
        var tStream = Stream.of("A", "B", "C", "D", "E");
        var uStream = Stream.of("1", "2", "3", "4", "5");

        List<String> result = Streams.zipOrderedAndEqualSizedStreams(tStream, uStream, (t, u) -> t + ":" + u)
                                .collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("A:1", "B:2", "C:3", "D:4", "E:5"));
    }
}