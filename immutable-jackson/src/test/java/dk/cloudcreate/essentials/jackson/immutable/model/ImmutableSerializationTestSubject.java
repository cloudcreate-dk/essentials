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

package dk.cloudcreate.essentials.jackson.immutable.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dk.cloudcreate.essentials.types.*;

import java.util.*;

public class ImmutableSerializationTestSubject {
    private final CustomerId customerId;
    private final OrderId    orderId;
    private final ProductId  productId;
    private final AccountId  accountId;
    private final Amount     amount;
    private final Percentage percentage;

    private final CurrencyCode currency;
    private final CountryCode  country;
    private final EmailAddress email;

    // Note it's recommended to use a REAL immutable data structure such as VAVR's Map instead of relying on Java's mutable Map
    // This is especially relevant AFTER deserialization where the Map supplied will be mutable and not wrapped using Collections#unmodifiableMap(Map)
    @JsonDeserialize(keyUsing = ProductIdKeyDeserializer.class)
    public final Map<ProductId, Quantity> orderLines;

    private final Money totalPrice;

    public ImmutableSerializationTestSubject(CustomerId customerId,
                                             OrderId orderId,
                                             ProductId productId,
                                             AccountId accountId,
                                             Amount amount,
                                             Percentage percentage,
                                             CurrencyCode currency,
                                             CountryCode country,
                                             EmailAddress email,
                                             Map<ProductId, Quantity> orderLines,
                                             Money totalPrice) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.productId = productId;
        this.accountId = accountId;
        this.amount = amount;
        this.percentage = percentage;
        this.currency = currency;
        this.country = country;
        this.email = email;
        this.orderLines = Collections.unmodifiableMap(orderLines);
        this.totalPrice = totalPrice;
    }


    public CustomerId getCustomerId() {
        return customerId;
    }


    public OrderId getOrderId() {
        return orderId;
    }


    public ProductId getProductId() {
        return productId;
    }


    public AccountId getAccountId() {
        return accountId;
    }


    public Amount getAmount() {
        return amount;
    }


    public Percentage getPercentage() {
        return percentage;
    }


    public CurrencyCode getCurrency() {
        return currency;
    }


    public CountryCode getCountry() {
        return country;
    }


    public EmailAddress getEmail() {
        return email;
    }


    public Money getTotalPrice() {
        return totalPrice;
    }


    public Map<ProductId, Quantity> getOrderLines() {
        return orderLines;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableSerializationTestSubject that = (ImmutableSerializationTestSubject) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId) && Objects.equals(accountId, that.accountId) && Objects.equals(amount, that.amount) && Objects.equals(percentage, that.percentage) && Objects.equals(currency, that.currency) && Objects.equals(country, that.country) && Objects.equals(email, that.email) && Objects.equals(orderLines, that.orderLines) && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, productId, accountId, amount, percentage, currency, country, email, orderLines, totalPrice);
    }
}
