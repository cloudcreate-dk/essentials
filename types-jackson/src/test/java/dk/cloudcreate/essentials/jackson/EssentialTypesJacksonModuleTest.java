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

package dk.cloudcreate.essentials.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cloudcreate.essentials.jackson.model.*;
import dk.cloudcreate.essentials.jackson.types.*;
import dk.cloudcreate.essentials.types.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EssentialTypesJacksonModuleTest {
    private final ObjectMapper objectMapper = EssentialTypesJacksonModule.createObjectMapper(new Jdk8Module(), new JavaTimeModule());

    @Test
    void test_serialization_and_deserialization() throws IOException {
        var amount     = Amount.of("123.45");
        var percentage = Percentage.from("30%");
        var orderLineProductId     = ProductId.random();
        var testSubject = new SerializationTestSubject(CustomerId.random(),
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
        var deserializedSubject = objectMapper.readValue(serialized.getBytes(StandardCharsets.UTF_8), SerializationTestSubject.class);

        assertThat(deserializedSubject.getAccountId()).isInstanceOf(AccountId.class);
        assertThat((CharSequence) deserializedSubject.getCustomerId()).isInstanceOf(CustomerId.class);
        assertThat(deserializedSubject.getOrderId()).isInstanceOf(OrderId.class);
        assertThat((CharSequence) deserializedSubject.getProductId()).isInstanceOf(ProductId.class);
        assertThat(deserializedSubject.getAmount()).isInstanceOf(Amount.class);
        assertThat(deserializedSubject.getPercentage()).isInstanceOf(Percentage.class);
        assertThat((CharSequence) deserializedSubject.getCurrency()).isInstanceOf(CurrencyCode.class);
        assertThat((CharSequence) deserializedSubject.getCountry()).isInstanceOf(CountryCode.class);
        assertThat((CharSequence) deserializedSubject.getEmail()).isInstanceOf(EmailAddress.class);
        assertThat(deserializedSubject.getTotalPrice()).isInstanceOf(Money.class);
        assertThat(deserializedSubject.getTotalPrice().getAmount()).isInstanceOf(Amount.class);
        assertThat((CharSequence) deserializedSubject.getTotalPrice().getCurrency()).isInstanceOf(CurrencyCode.class);
        assertThat(deserializedSubject.getOrderLines().get(orderLineProductId)).isEqualTo(testSubject.getOrderLines().get(orderLineProductId));

        assertThat(deserializedSubject).isEqualTo(testSubject);
    }
}