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

package dk.cloudcreate.essentials.immutable;

import dk.cloudcreate.essentials.immutable.model.*;
import dk.cloudcreate.essentials.types.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableValueObjectTest {

    @Test
    void test_equals_with_self() {
        var testSubject = new ImmutableOrder(OrderId.of(123456789),
                                             CustomerId.of("CustomerId1"),
                                             Percentage.from("50%"),
                                             EmailAddress.of("john@nonexistingdomain.com"),
                                             Map.of(ProductId.of("Product1"), Quantity.of(10)),
                                             Money.of("1234.56", CurrencyCode.DKK));

        assertThat(testSubject).isEqualTo(testSubject);
    }

    @Test
    void test_equals_with_null() {
        var testSubject = new ImmutableOrder(OrderId.of(123456789),
                                             CustomerId.of("CustomerId1"),
                                             Percentage.from("50%"),
                                             EmailAddress.of("john@nonexistingdomain.com"),
                                             Map.of(ProductId.of("Product1"), Quantity.of(10)),
                                             Money.of("1234.56", CurrencyCode.DKK));

        assertThat(testSubject).isNotEqualTo(null);
    }

    @Test
    void test_toString_with_only_non_null_values() {
        // Given
        var testSubject = new ImmutableOrder(OrderId.of(123456789),
                                             CustomerId.of("CustomerId1"),
                                             Percentage.from("50%"),
                                             EmailAddress.of("john@nonexistingdomain.com"),
                                             Map.of(ProductId.of("Product1"), Quantity.of(10)),
                                             Money.of("1234.56", CurrencyCode.DKK));

        // When
        var toString = testSubject.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).isEqualTo("ImmutableOrder { customerId: CustomerId1, email: john@nonexistingdomain.com, orderId: 123456789, orderLines: {Product1=10}, percentage: 50.00% }");
    }

    @Test
    void test_hashCode_with_only_non_null_values() {
        // Given (fields in hashCode calculation order)
        var customerId   = CustomerId.of("CustomerId1");
        var emailAddress = EmailAddress.of("john@nonexistingdomain.com");
        var orderId      = OrderId.of(123456789);
        var orderLines   = Map.of(ProductId.of("Product1"), Quantity.of(10));
        var percentage   = Percentage.from("50%");
        var totalPrice   = Money.of("1234.56", CurrencyCode.DKK);

        var testSubject = new ImmutableOrder(orderId,
                                             customerId,
                                             percentage,
                                             emailAddress,
                                             orderLines,
                                             totalPrice);

        // When
        var hashCode = testSubject.hashCode();

        // Then
        assertThat(hashCode).isEqualTo(Objects.hash(customerId,
                                                    emailAddress,
                                                    orderId,
                                                    percentage,
                                                    totalPrice));
    }

    @Test
    void test_equals_with_only_non_null_values() {
        // Given (fields in hashCode calculation order)
        var customerId   = CustomerId.of("CustomerId1");
        var emailAddress = EmailAddress.of("john@nonexistingdomain.com");
        var orderId      = OrderId.of(123456789);
        var percentage   = Percentage.from("50%");
        var totalPrice   = Money.of("1234.56", CurrencyCode.DKK);

        var thisOrder = new ImmutableOrder(orderId,
                                           customerId,
                                           percentage,
                                           emailAddress,
                                           Map.of(ProductId.of("Product1"), Quantity.of(10)), // Excluded
                                           totalPrice);
        var thatOrder = new ImmutableOrder(orderId,
                                           customerId,
                                           percentage,
                                           emailAddress,
                                           Map.of(ProductId.of("Product2"), Quantity.of(10)), // Excluded
                                           totalPrice);

        // When
        assertThat(thisOrder).isEqualTo(thatOrder);
    }

    @Test
    void test_toString_with_only_null_values() {
        // Given
        var testSubject = new ImmutableOrder(null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null);

        // When
        var toString = testSubject.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).isEqualTo("ImmutableOrder { customerId: null, email: null, orderId: null, orderLines: null, percentage: null }");
    }

    @Test
    void test_hashCode_with_only_null_values() {
        // Given (fields in hashCode calculation order)
        CustomerId               customerId   = null;
        EmailAddress             emailAddress = null;
        OrderId                  orderId      = null;
        Map<ProductId, Quantity> orderLines   = null;
        Percentage               percentage   = null;
        Money                    totalPrice   = null;

        var testSubject = new ImmutableOrder(orderId,
                                             customerId,
                                             percentage,
                                             emailAddress,
                                             orderLines,
                                             totalPrice);

        // When
        var hashCode = testSubject.hashCode();

        // Then
        assertThat(hashCode).isEqualTo(Objects.hash(customerId,
                                                    emailAddress,
                                                    orderId,
                                                    percentage,
                                                    totalPrice));
    }

    @Test
    void test_equals_with_only_null_values() {
        // Given (fields in hashCode calculation order)
        CustomerId               customerId   = null;
        EmailAddress             emailAddress = null;
        OrderId                  orderId      = null;
        Map<ProductId, Quantity> orderLines   = null;
        Percentage               percentage   = null;
        Money                    totalPrice   = null;

        var thisOrder = new ImmutableOrder(orderId,
                                           customerId,
                                           percentage,
                                           emailAddress,
                                           orderLines,
                                           totalPrice);
        var thatOrder = new ImmutableOrder(orderId,
                                           customerId,
                                           percentage,
                                           emailAddress,
                                           orderLines,
                                           totalPrice);

        // Then
        assertThat(thisOrder).isEqualTo(thatOrder);
    }

    @Test
    void test_toString_with_both_null_and_non_null_values() {
        // Given
        var testSubject = new ImmutableOrder(OrderId.of(123456789),
                                             CustomerId.of("CustomerId1"),
                                             null,
                                             null,
                                             Map.of(ProductId.of("Product1"), Quantity.of(10)),
                                             Money.of("1234.56", CurrencyCode.DKK));

        // When
        var toString = testSubject.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).isEqualTo("ImmutableOrder { customerId: CustomerId1, orderId: 123456789, orderLines: {Product1=10}, email: null, percentage: null }");
    }

    @Test
    void test_hashCode_with_both_null_and_non_null_values() {
        // Given (fields in hashCode calculation order)
        CustomerId               customerId   = CustomerId.of("CustomerId1");
        EmailAddress             emailAddress = null;
        OrderId                  orderId      = OrderId.of(123456789);
        Map<ProductId, Quantity> orderLines   = Map.of(ProductId.of("Product1"), Quantity.of(10));
        Percentage               percentage   = null;
        Money                    totalPrice   = Money.of("1234.56", CurrencyCode.DKK);

        var testSubject = new ImmutableOrder(orderId,
                                             customerId,
                                             percentage,
                                             emailAddress,
                                             orderLines,
                                             totalPrice);

        // When
        var hashCode = testSubject.hashCode();

        // Then
        assertThat(hashCode).isEqualTo(Objects.hash(customerId,
                                                    emailAddress,
                                                    orderId,
                                                    percentage,
                                                    totalPrice));
    }

    @Test
    void test_equals_with_both_null_and_non_null_values() {
        // Given (fields in hashCode calculation order)
        CustomerId               customerId   = CustomerId.of("CustomerId1");
        EmailAddress             emailAddress = EmailAddress.of("john@nonexistingdomain.com");
        OrderId                  orderId      = OrderId.of(123456789);
        Map<ProductId, Quantity> orderLines   = Map.of(ProductId.of("Product1"), Quantity.of(10));
        Percentage               percentage   = Percentage.from("25%");
        Money                    totalPrice   = Money.of("1234.56", CurrencyCode.DKK);

        var thisOrder = new ImmutableOrder(orderId,
                                           customerId,
                                           percentage,
                                           emailAddress,
                                           orderLines,
                                           totalPrice);

        assertThat(thisOrder).isEqualTo(new ImmutableOrder(orderId,
                                                           customerId,
                                                           percentage,
                                                           emailAddress,
                                                           null, // Excluded
                                                           totalPrice));

        assertThat(thisOrder).isNotEqualTo(new ImmutableOrder(null,
                                                              customerId,
                                                              percentage,
                                                              emailAddress,
                                                              orderLines,
                                                              totalPrice));

        assertThat(thisOrder).isNotEqualTo(new ImmutableOrder(orderId,
                                                              null,
                                                              percentage,
                                                              emailAddress,
                                                              orderLines,
                                                              totalPrice));

        assertThat(thisOrder).isNotEqualTo(new ImmutableOrder(orderId,
                                                              customerId,
                                                              null,
                                                              emailAddress,
                                                              orderLines,
                                                              totalPrice));

        assertThat(thisOrder).isNotEqualTo(new ImmutableOrder(orderId,
                                                              customerId,
                                                              percentage,
                                                              null,
                                                              orderLines,
                                                              totalPrice));
        assertThat(thisOrder).isNotEqualTo(new ImmutableOrder(orderId,
                                                              customerId,
                                                              percentage,
                                                              emailAddress,
                                                              orderLines,
                                                              null));
    }

}
