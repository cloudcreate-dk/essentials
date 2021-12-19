/*
 * Copyright 2021 the original author or authors.
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

import java.util.Objects;

public class SerializationTestSubject {
    private CustomerId customerId;
    private OrderId    orderId;
    private ProductId  productId;
    private AccountId  accountId;
    private Amount     amount;
    private Percentage percentage;

    public SerializationTestSubject(CustomerId customerId, OrderId orderId, ProductId productId, AccountId accountId, Amount amount, Percentage percentage) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.productId = productId;
        this.accountId = accountId;
        this.amount = amount;
        this.percentage = percentage;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializationTestSubject that = (SerializationTestSubject) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId) && Objects.equals(accountId, that.accountId) && Objects.equals(amount, that.amount) && Objects.equals(percentage, that.percentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, productId, accountId, amount, percentage);
    }
}
