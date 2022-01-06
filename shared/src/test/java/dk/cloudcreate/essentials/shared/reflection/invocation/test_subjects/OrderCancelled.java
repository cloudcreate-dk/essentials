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

package dk.cloudcreate.essentials.shared.reflection.invocation.test_subjects;

import java.time.LocalDateTime;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

public class OrderCancelled implements OrderEvent {
    public final String        orderId;
    public final LocalDateTime cancelledOn;

    public OrderCancelled(String orderId) {
        this.orderId = requireNonNull(orderId);
        cancelledOn = LocalDateTime.now();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }
}
