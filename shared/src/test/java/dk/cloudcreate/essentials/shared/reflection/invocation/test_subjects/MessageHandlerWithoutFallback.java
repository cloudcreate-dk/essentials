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

import dk.cloudcreate.essentials.shared.functional.tuple.*;

import java.util.*;

public class MessageHandlerWithoutFallback {
    public Map<String, Pair<OrderEvent, Message>> methodCalledWithArgument = new HashMap<>();

    @MessageHandler
    public void orderCreated(OrderCreated orderCreated, Message message) {
        methodCalledWithArgument.put("orderCreated", Tuple.of(orderCreated, message));
    }

    @MessageHandler
    public void orderCancelled(OrderCancelled orderCancelled, Message message) {
        methodCalledWithArgument.put("orderCancelled", Tuple.of(orderCancelled, message));
    }
}
