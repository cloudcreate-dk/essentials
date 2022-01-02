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

package dk.cloudcreate.essentials.types.springdata.mongo;

import dk.cloudcreate.essentials.types.*;
import dk.cloudcreate.essentials.types.springdata.mongo.model.Order;
import dk.cloudcreate.essentials.types.springdata.mongo.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class OrderRepositoryIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void cleanUp() {
        this.orderRepository.deleteAll();
    }

    @Test
    void test_we_can_store_an_order_and_load_it_again() {
        var currencyCode = CurrencyCode.of("DKK");
        var amount       = Amount.of("123.456");
        var percentage   = Percentage.from("40.5%");
        var storedOrder = new Order(OrderId.random(),
                                    CustomerId.random(),
                                    AccountId.random(),
                                    Map.of(ProductId.random(), Quantity.of(10),
                                           ProductId.random(), Quantity.of(5),
                                           ProductId.random(), Quantity.of(1)),
                                    amount,
                                    percentage,
                                    currencyCode,
                                    CountryCode.of("DK"),
                                    EmailAddress.of("john@nonexistingdomain.com"),
                                    new Money(amount.add(percentage.of(amount)), currencyCode)
        );
        orderRepository.save(storedOrder);

        var loadedOrder = orderRepository.findById(storedOrder.getId());

        assertThat(loadedOrder).isPresent();
        assertThat(loadedOrder.get()).isEqualTo(storedOrder);
    }

    @Test
    void test_a_new_order_without_an_id_automatically_gets_an_id_assigned() {
        var currencyCode = CurrencyCode.of("DKK");
        var amount       = Amount.of("123.456");
        var percentage   = Percentage.from("40.5%");

        var storedOrder = orderRepository.save(new Order(CustomerId.random(),
                                                         AccountId.random(),
                                                         Map.of(ProductId.random(), Quantity.of(10),
                                                                ProductId.random(), Quantity.of(5),
                                                                ProductId.random(), Quantity.of(1)),
                                                         amount,
                                                         percentage,
                                                         currencyCode,
                                                         CountryCode.of("DK"),
                                                         EmailAddress.of("john@nonexistingdomain.com"),
                                                         new Money(amount.add(percentage.of(amount)), currencyCode)));

        assertThat(storedOrder.getId()).isNotNull();

        var loadedOrder = orderRepository.findById(storedOrder.getId());

        assertThat(loadedOrder).isPresent();
        assertThat(loadedOrder.get()).isEqualTo(storedOrder);
    }

}