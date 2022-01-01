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

package dk.cloudcreate.essentials.types.springdata.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
public class Order {
    @Id
    public OrderId                  id;
    public CustomerId               customerId;
    public AccountId                accountId;
    public Map<ProductId, Quantity> orderLines;

    public Order() {
    }

    public Order(OrderId id, CustomerId customerId, AccountId accountId, Map<ProductId, Quantity> orderLines) {
        this.id = id;
        this.customerId = customerId;
        this.accountId = accountId;
        this.orderLines = orderLines;
    }

    public Order(CustomerId customerId, AccountId accountId, Map<ProductId, Quantity> orderLines) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.orderLines = orderLines;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(customerId, order.customerId) && Objects.equals(accountId, order.accountId) && Objects.equals(orderLines, order.orderLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, accountId, orderLines);
    }
}
