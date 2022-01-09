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

package dk.cloudcreate.essentials.types.spring.web.model;

import com.fasterxml.jackson.databind.annotation.*;
import dk.cloudcreate.essentials.types.*;

import java.util.*;

public class Order {
    public OrderId                  id;
    public CustomerId               customerId;
    public AccountId                accountId;
    @JsonDeserialize(keyUsing = ProductIdKeyDeserializer.class)
    public Map<ProductId, Quantity> orderLines;

    private Amount     amount;
    private Percentage percentage;

    private CurrencyCode currency;
    private CountryCode  country;
    private EmailAddress email;

    private Money totalPrice;

    public Order() {
    }

    public Order(OrderId id,
                 CustomerId customerId,
                 AccountId accountId,
                 Map<ProductId, Quantity> orderLines,
                 Amount amount,
                 Percentage percentage,
                 CurrencyCode currency,
                 CountryCode country,
                 EmailAddress email,
                 Money totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.accountId = accountId;
        this.orderLines = orderLines;
        this.amount = amount;
        this.percentage = percentage;
        this.currency = currency;
        this.country = country;
        this.email = email;
        this.totalPrice = totalPrice;
    }

    public Order(CustomerId customerId,
                 AccountId accountId,
                 Map<ProductId, Quantity> orderLines,
                 Amount amount,
                 Percentage percentage,
                 CurrencyCode currency,
                 CountryCode country,
                 EmailAddress email,
                 Money totalPrice) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.orderLines = orderLines;
        this.amount = amount;
        this.percentage = percentage;
        this.currency = currency;
        this.country = country;
        this.email = email;
        this.totalPrice = totalPrice;
    }

    public OrderId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    public Map<ProductId, Quantity> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Map<ProductId, Quantity> orderLines) {
        this.orderLines = orderLines;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Percentage getPercentage() {
        return percentage;
    }

    public void setPercentage(Percentage percentage) {
        this.percentage = percentage;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public CountryCode getCountry() {
        return country;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public void setEmail(EmailAddress email) {
        this.email = email;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(customerId, order.customerId) && Objects.equals(accountId, order.accountId) &&
                Objects.equals(orderLines, order.orderLines) && Objects.equals(amount, order.amount) && Objects.equals(percentage, order.percentage) &&
                Objects.equals(currency, order.currency) && Objects.equals(country, order.country) && Objects.equals(email, order.email) && Objects.equals(totalPrice, order.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, accountId, orderLines, amount, percentage, currency, country, email, totalPrice);
    }
}
