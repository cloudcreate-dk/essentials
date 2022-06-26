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

package dk.cloudcreate.essentials.types.jdbi;

import dk.cloudcreate.essentials.types.*;
import dk.cloudcreate.essentials.types.jdbi.model.*;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SingleValueTypeArgumentsTest {
    @Test
    void test_jdbi_argument_factories() {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test");
        jdbi.registerArgument(new AmountArgumentFactory());
        jdbi.registerColumnMapper(new AmountColumnMapper());
        jdbi.registerArgument(new CountryCodeArgumentFactory());
        jdbi.registerArgument(new CurrencyCodeArgumentFactory());
        jdbi.registerArgument(new EmailAddressArgumentFactory());
        jdbi.registerArgument(new PercentageArgumentFactory());

        jdbi.registerArgument(new OrderIdArgumentFactory());
        jdbi.registerArgument(new CustomerIdArgumentFactory());
        jdbi.registerArgument(new ProductIdArgumentFactory());
        jdbi.registerArgument(new AccountIdArgumentFactory());

        jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE orders (" +
                                   "id BIGINT PRIMARY KEY, " +
                                   "customer_id VARCHAR, " +
                                   "product_id VARCHAR, " +
                                   "account_id BIGINT," +
                                   "amount NUMERIC(20, 3)," +
                                   "country VARCHAR," +
                                   "currency VARCHAR," +
                                   "email VARCHAR," +
                                   "percentage NUMERIC(20, 2)" +
                                   ")");

            var orderId    = OrderId.random();
            var customerId = CustomerId.random();
            var productId  = ProductId.random();
            var accountId  = AccountId.random();
            var amount     = Amount.of("123.456");
            var country    = CountryCode.of("DK");
            var currency   = CurrencyCode.of("DKK");
            var email      = EmailAddress.of("john@nonexistingdomain.com");
            var percentage = Percentage.from("40.5%");

            handle.inTransaction(_handle -> handle.createUpdate("INSERT INTO orders(id, customer_id, product_id, account_id, amount, country, currency, email, percentage) " +
                                                                        "VALUES (:id, :customerId, :productId, :accountId, :amount, :country, :currency, :email, :percentage)")
                                                  .bind("id", orderId)
                                                  .bind("customerId", customerId)
                                                  .bind("productId", productId)
                                                  .bind("accountId", accountId)
                                                  .bind("amount", amount)
                                                  .bind("country", country)
                                                  .bind("currency", currency)
                                                  .bind("email", email)
                                                  .bind("percentage", percentage)
                                                  .execute());
            var results = handle.inTransaction(_handle -> handle.createQuery("SELECT * from orders WHERE id = :id")
                                                                .bind("id", orderId)
                                                                .mapToMap());

            assertThat(results).isNotNull();

            var result = results.one();
            assertThat((long) result.get("id")).isEqualTo(orderId.value());
            assertThat(result.get("customer_id")).isEqualTo(customerId.value());
            assertThat(result.get("product_id")).isEqualTo(productId.value());
            assertThat((long) result.get("account_id")).isEqualTo(accountId.value());
            assertThat(result.get("amount")).isEqualTo(amount.value());
            assertThat(result.get("country")).isEqualTo(country.value());
            assertThat(result.get("currency")).isEqualTo(currency.value());
            assertThat(result.get("email")).isEqualTo(email.value());
            assertThat(result.get("percentage")).isEqualTo(percentage.value());


            var amountResult = handle.inTransaction(_handle -> handle.createQuery("SELECT amount from orders WHERE id = :id")
                                                               .bind("id", orderId)
                                                               .mapTo(Amount.class))
                               .one();
            assertThat(amountResult).isInstanceOf(Amount.class);
            assertThat(amountResult).isEqualTo(amount);
        });

    }
}