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
import dk.cloudcreate.essentials.types.springdata.mongo.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class ProductRepositoryIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void cleanUp() {
        this.productRepository.deleteAll();
    }

    @Test
    void test_we_can_store_a_product_and_load_it_again() {
        var storedProduct = new Product(ProductId.random(),
                                        "Test Product",
                                        new Price(Amount.of("100.5"), CurrencyCode.of("DKK")));
        productRepository.save(storedProduct);

        var loadedProduct = productRepository.findById(storedProduct.getId());

        assertThat(loadedProduct).isPresent();
        assertThat(loadedProduct.get()).isEqualTo(storedProduct);
    }

    @Test
    void test_a_new_product_without_an_id_automatically_gets_an_id_assigned() {
        var storedProduct = productRepository.save(new Product("Test Product",
                                                               new Price(Amount.of("100.5"), CurrencyCode.of("DKK"))));

        assertThat((CharSequence) storedProduct.getId()).isNotNull();

        var loadedProduct = productRepository.findById(storedProduct.getId());

        assertThat(loadedProduct).isPresent();
        assertThat(loadedProduct.get()).isEqualTo(storedProduct);
    }

    @Test
    void test_we_can_store_a_product_and_query_it_again() {
        var storedProduct = new Product(ProductId.random(),
                                        "Test Product",
                                        new Price(Amount.of("121.5"), CurrencyCode.of("USD")));
        productRepository.save(storedProduct);

        var products = mongoTemplate.find(Query.query(Criteria.where("price.amount").gte(Amount.of("120"))), Product.class);

        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0)).isEqualTo(storedProduct);
    }

}