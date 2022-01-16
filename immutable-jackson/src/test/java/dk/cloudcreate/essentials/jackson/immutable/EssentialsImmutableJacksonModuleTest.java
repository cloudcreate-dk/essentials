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

package dk.cloudcreate.essentials.jackson.immutable;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cloudcreate.essentials.jackson.immutable.model.*;
import dk.cloudcreate.essentials.jackson.types.EssentialTypesJacksonModule;
import dk.cloudcreate.essentials.types.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EssentialsImmutableJacksonModuleTest {
    private final ObjectMapper objectMapper = EssentialsImmutableJacksonModule.createObjectMapper(new EssentialTypesJacksonModule(), new Jdk8Module(), new JavaTimeModule());

    @Test
    void test_serialization_and_deserialization_of_ImmutableOrder() throws IOException {
        var testSubject = new ImmutableOrder(OrderId.random(),
                                             CustomerId.random(),
                                             Money.of("1234.56", CurrencyCode.DKK));

        var serialized = objectMapper.writeValueAsString(testSubject);
        System.out.println(serialized);

        var deserializedSubject = objectMapper.readValue(serialized.getBytes(StandardCharsets.UTF_8), ImmutableOrder.class);
        assertThat(deserializedSubject).isEqualTo(testSubject);
    }


    @Test
    void test_serialization_and_deserialization_of_ImmutableSerializationTestSubject() throws IOException {
        var amount             = Amount.of("123.45");
        var percentage         = Percentage.from("30%");
        var orderLineProductId = ProductId.random();
        var testSubject = new ImmutableSerializationTestSubject(CustomerId.random(),
                                                                OrderId.random(),
                                                                ProductId.random(),
                                                                AccountId.random(),
                                                                amount,
                                                                percentage,
                                                                CurrencyCode.DKK,
                                                                CountryCode.of("DK"),
                                                                EmailAddress.of("john@nonexistingdomain.com"),
                                                                Map.of(orderLineProductId, Quantity.of(10),
                                                                       ProductId.random(), Quantity.of(5),
                                                                       ProductId.random(), Quantity.of(1)),
                                                                Money.of(amount.add(percentage.of(amount)), CurrencyCode.DKK));

        var serialized = objectMapper.writeValueAsString(testSubject);
        System.out.println(serialized);

        var deserializedSubject = objectMapper.readValue(serialized.getBytes(StandardCharsets.UTF_8), ImmutableSerializationTestSubject.class);
        assertThat(deserializedSubject).isEqualTo(testSubject);
    }
}