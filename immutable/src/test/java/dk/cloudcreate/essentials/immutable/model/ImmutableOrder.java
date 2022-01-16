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

package dk.cloudcreate.essentials.immutable.model;

import dk.cloudcreate.essentials.immutable.ImmutableValueObject;
import dk.cloudcreate.essentials.immutable.annotations.Exclude;
import dk.cloudcreate.essentials.types.*;

import java.util.*;

public class ImmutableOrder extends ImmutableValueObject {
    public final OrderId                  orderId;
    public final CustomerId               customerId;
    public final EmailAddress             email;
    public final Percentage               percentage;
    @Exclude.EqualsAndHashCode
    public final Map<ProductId, Quantity> orderLines;
    @Exclude.ToString
    public final Money                    totalPrice;

    public ImmutableOrder(OrderId orderId,
                          CustomerId customerId,
                          Percentage percentage,
                          EmailAddress email,
                          Map<ProductId, Quantity> orderLines,
                          Money totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.percentage = percentage;
        this.email = email;
        this.orderLines = orderLines;
        this.totalPrice = totalPrice;
    }
}
