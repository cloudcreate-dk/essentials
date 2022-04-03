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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cloudcreate.essentials.types.*;
import dk.cloudcreate.essentials.types.spring.web.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class WebMvcControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(new Order(OrderId.random(),
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
                                                                     Money.of(Amount.of("55.5"), CurrencyCode.EUR))));
    }

    @Test
    public void getOrderForCustomer() throws Exception {
        var customerId = CustomerId.random();
        mockMvc.perform(MockMvcRequestBuilders.get("/order/for-customer/{customerId}", customerId))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.customerId", is(customerId.toString())));
    }

    @Test
    public void findById() throws Exception {
        var orderId = OrderId.random();
        mockMvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(orderId.longValue())));
    }

    @Test
    public void updatePrice() throws Exception {
        var customerId = CustomerId.random();
        var price      = Amount.of("100.5");
        mockMvc.perform(MockMvcRequestBuilders.post("/order/for-customer/{customerId}/update/total-price?price={price}", customerId, price))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.customerId", is(customerId.toString())))
               .andExpect(jsonPath("$.totalPrice.amount", is(price.doubleValue())))
               .andExpect(jsonPath("$.totalPrice.currency", is("EUR")));
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
        mockMvc.perform(MockMvcRequestBuilders.put("/order")
                                              .contentType("application/json")
                                              .content(objectMapper.writeValueAsString(order)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.content().string(order.id.toString()));
    }
}
