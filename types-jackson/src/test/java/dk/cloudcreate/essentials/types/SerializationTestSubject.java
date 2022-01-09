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

package dk.cloudcreate.essentials.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

public class SerializationTestSubject {
    private CustomerId customerId;
    private OrderId    orderId;
    private ProductId  productId;
    private AccountId  accountId;
    private Amount     amount;
    private Percentage percentage;

    private CurrencyCode currency;
    private CountryCode  country;
    private EmailAddress email;

    @JsonDeserialize(keyUsing = ProductIdKeyDeserializer.class)
    public Map<ProductId, Quantity> orderLines;

    private Money totalPrice;

    public SerializationTestSubject(CustomerId customerId,
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
        this.orderLines = orderLines;
        this.totalPrice = totalPrice;
    }

    public SerializationTestSubject() {
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
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

    public Map<ProductId, Quantity> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Map<ProductId, Quantity> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializationTestSubject that = (SerializationTestSubject) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId) && Objects.equals(accountId, that.accountId) && Objects.equals(amount, that.amount) && Objects.equals(percentage, that.percentage) && Objects.equals(currency, that.currency) && Objects.equals(country, that.country) && Objects.equals(email, that.email) && Objects.equals(orderLines, that.orderLines) && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, productId, accountId, amount, percentage, currency, country, email, orderLines, totalPrice);
    }
}
