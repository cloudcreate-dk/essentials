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

package dk.cloudcreate.essentials.types.spring.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cloudcreate.essentials.types.*;
import dk.cloudcreate.essentials.types.spring.web.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebFluxControllerIT {
    @Autowired
    private WebTestClient testClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getOrderForCustomer() throws Exception {
        var customerId = CustomerId.random();
        var result = testClient.get()
                               .uri("/reactive-order/for-customer/{customerId}", customerId)
                               .exchange()
                               .expectStatus()
                               .isOk()
                               .expectBody(Order.class)
                               .returnResult();

        assertThat((CharSequence) result.getResponseBody().customerId).isEqualTo(customerId);
    }


    @Test
    public void findById() throws Exception {
        var orderId = OrderId.random();
        var result = testClient.get()
                               .uri("/reactive-order/{orderId}", orderId)
                               .exchange()
                               .expectStatus()
                               .isOk()
                               .expectBody(Order.class)
                               .returnResult();

        assertThat(result.getResponseBody().id).isEqualTo(orderId);
    }

    @Test
    public void updatePrice() throws Exception {
        var customerId = CustomerId.random();
        var price      = Amount.of("100.5");
        var result = testClient.post()
                               .uri("/reactive-order/for-customer/{customerId}/update/total-price?price={price}", customerId, price)
                               .exchange()
                               .expectStatus()
                               .isOk()
                               .expectBody(Order.class)
                               .returnResult();
        assertThat((CharSequence) result.getResponseBody().customerId).isEqualTo(customerId);
        assertThat(result.getResponseBody().getTotalPrice().getAmount()).isEqualTo(price);
        assertThat((CharSequence) result.getResponseBody().getTotalPrice().getCurrency()).isEqualTo(CurrencyCode.EUR);
    }

    @Test
    public void addOrder() throws Exception {
        var order = new Order(OrderId.random(),
                              CustomerId.random(),
                              AccountId.random(),
                              Map.of(ProductId.random(), Quantity.of(10),
                                     ProductId.random(), Quantity.of(5),
                                     ProductId.random(), Quantity.of(1)),
                              Amount.of("123.456"),
                              Percentage.from("40.5%"),
                              CurrencyCode.of("DKK"),
                              CountryCode.of("DK"),
                              EmailAddress.of("john@nonexistingdomain.com"),
                              Money.of("102.75", CurrencyCode.EUR));
        var result = testClient.put()
                               .uri("/reactive-order")
                               .contentType(MediaType.APPLICATION_JSON)
                               .bodyValue(objectMapper.writeValueAsString(order))
                               .exchange()
                               .expectStatus()
                               .isOk()
                               .expectBody(OrderId.class)
                               .returnResult();
        assertThat(result.getResponseBody()).isEqualTo(order.getId());
    }
}
