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

import java.util.*;

public class OrderEventHandlerWithoutFallback {
    public Map<String, OrderEvent> methodCalledWithArgument = new HashMap<>();

    @EventHandler
    private void orderCreated(OrderCreated orderCreated) {
        methodCalledWithArgument.put("orderCreated", orderCreated);
    }

    @EventHandler
    private void orderCancelled(OrderCancelled orderCancelled) {
        methodCalledWithArgument.put("orderCancelled", orderCancelled);
    }

    // Won't be called as it's lacking the EventHandler annotation
    public void orderAccepted(OrderAccepted orderAccepted) {
        methodCalledWithArgument.put("orderAccepted", orderAccepted);
    }
}
