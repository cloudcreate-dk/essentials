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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cloudcreate.essentials.jackson.types.EssentialTypesJacksonModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EssentialTypesJacksonModuleTest {
    private final ObjectMapper objectMapper = EssentialTypesJacksonModule.createObjectMapper(new Jdk8Module(), new JavaTimeModule());

    @Test
    void test_serialization_and_deserialization() throws IOException {
        var testSubject = new SerializationTestSubject(CustomerId.random(),
                                                       OrderId.random(),
                                                       ProductId.random(),
                                                       AccountId.random(),
                                                       Amount.of("123.45"),
                                                       Percentage.from("30"));

        var serialized = objectMapper.writeValueAsString(testSubject);
        System.out.println(serialized);
        var deserializedSubject = objectMapper.readValue(serialized.getBytes(StandardCharsets.UTF_8), SerializationTestSubject.class);

        assertThat(deserializedSubject.getAccountId()).isInstanceOf(AccountId.class);
        assertThat((CharSequence) deserializedSubject.getCustomerId()).isInstanceOf(CustomerId.class);
        assertThat(deserializedSubject.getOrderId()).isInstanceOf(OrderId.class);
        assertThat((CharSequence) deserializedSubject.getProductId()).isInstanceOf(ProductId.class);
        assertThat(deserializedSubject.getAmount()).isInstanceOf(Amount.class);
        assertThat(deserializedSubject.getPercentage()).isInstanceOf(Percentage.class);
        assertThat(deserializedSubject).isEqualTo(testSubject);
    }
}