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

package dk.cloudcreate.essentials.shared;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static dk.cloudcreate.essentials.shared.MessageFormatter.NamedArgumentBinding.arg;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;
import static org.assertj.core.api.Assertions.assertThat;

class MessageFormatterTest {
    @Test
    void msg_with_no_placeholders_returns_the_original_message() {
        var given  = "This is a simple message example";
        var result = msg(given);
        assertThat(result).isEqualTo(given);
    }

    @Test
    void msg_with_placeholders_will_get_placeholder_values_merged_into_the_original_message() {
        var given  = "This {} a {} message {}";
        var result = msg(given, "is", "simple", "example");
        assertThat(result).isEqualTo("This is a simple message example");
    }

    @Test
    void test_bind() {
        var danishText = "Kære {:firstName} {:lastName}";

        var mergedDanishText = MessageFormatter.bind(danishText,
                                                     arg("firstName", "John"),
                                                     arg("lastName", "Doe"));
        assertThat(mergedDanishText).isEqualTo("Kære John Doe");

        var englishText = "Dear {:lastName}, {:firstName}";
        var mergedEnglishText = MessageFormatter.bind(englishText,
                                                      Map.of("firstName", "John",
                                                             "lastName", "Doe"));
        assertThat(mergedEnglishText).isEqualTo("Dear Doe, John");
    }
}