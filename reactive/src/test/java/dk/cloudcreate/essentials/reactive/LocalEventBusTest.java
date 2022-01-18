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

package dk.cloudcreate.essentials.reactive;


import dk.cloudcreate.essentials.shared.functional.tuple.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LocalEventBusTest {
    @Test
    void test_with_both_sync_and_async_subscribers() {
        var onErrorHandler   = new TestOnErrorHandler();
        var localEventBus    = new LocalEventBus<>("Test", 3, onErrorHandler);
        var asyncSubscriber1 = new RecordingEventConsumer();
        var asyncSubscriber2 = new RecordingEventConsumer();
        var syncSubscriber1  = new RecordingEventConsumer();
        var syncSubscriber2  = new RecordingEventConsumer();

        localEventBus.addAsyncSubscriber(asyncSubscriber1);
        localEventBus.addAsyncSubscriber(asyncSubscriber2);
        localEventBus.addSyncSubscriber(syncSubscriber1);
        localEventBus.addSyncSubscriber(syncSubscriber2);
        var events = List.of(new OrderCreatedEvent(), new OrderPaidEvent(), new OrderCancelledEvent());

        // When
        events.forEach(localEventBus::publish);

        // Then
        Awaitility.waitAtMost(Duration.ofMillis(1000))
                  .untilAsserted(() -> assertThat(asyncSubscriber1.eventsReceived).isEqualTo(events));
        assertThat(asyncSubscriber2.eventsReceived).isEqualTo(events);
        assertThat(syncSubscriber1.eventsReceived).isEqualTo(events);
        assertThat(syncSubscriber2.eventsReceived).isEqualTo(events);
        assertThat(onErrorHandler.errorsHandled).isEmpty();
    }

    @Test
    void test_with_async_subscriber_throwing_an_exception() {
        var onErrorHandler = new TestOnErrorHandler();
        var localEventBus  = new LocalEventBus<>("Test", 3, onErrorHandler);
        var syncSubscriber = new RecordingEventConsumer();
        localEventBus.addSyncSubscriber(syncSubscriber);

        Consumer<OrderEvent> asyncSubscriber = orderEvent -> {
            throw new RuntimeException("On purpose");
        };
        localEventBus.addAsyncSubscriber(asyncSubscriber);
        var events = List.of(new OrderCreatedEvent(), new OrderPaidEvent(), new OrderCancelledEvent());

        // When
        events.forEach(localEventBus::publish);

        // Then
        assertThat(syncSubscriber.eventsReceived).isEqualTo(events);
        Awaitility.waitAtMost(Duration.ofMillis(1000))
                  .untilAsserted(() -> assertThat(onErrorHandler.errorsHandled.size()).isEqualTo(events.size()));
        assertThat(onErrorHandler.errorsHandled.stream().filter(failure -> failure._1 == asyncSubscriber).count()).isEqualTo(events.size());
        assertThat(onErrorHandler.errorsHandled.stream().map(failure -> failure._2).collect(Collectors.toList())).isEqualTo(events);
    }

    @Test
    void test_with_sync_subscriber_throwing_an_exception() {
        var onErrorHandler  = new TestOnErrorHandler();
        var localEventBus   = new LocalEventBus<>("Test", 3, onErrorHandler);
        var asyncSubscriber = new RecordingEventConsumer();
        localEventBus.addAsyncSubscriber(asyncSubscriber);

        Consumer<OrderEvent> syncSubscriber = orderEvent -> {
            throw new RuntimeException("On purpose");
        };
        localEventBus.addSyncSubscriber(syncSubscriber);
        var events = List.of(new OrderCreatedEvent(), new OrderPaidEvent(), new OrderCancelledEvent());

        // When
        events.forEach(localEventBus::publish);

        // Then
        Awaitility.waitAtMost(Duration.ofMillis(1000))
                  .untilAsserted(() -> assertThat(asyncSubscriber.eventsReceived).isEqualTo(events));
        assertThat(onErrorHandler.errorsHandled.size()).isEqualTo(events.size());
        assertThat(onErrorHandler.errorsHandled.stream().filter(failure -> failure._1 == syncSubscriber).count()).isEqualTo(events.size());
        assertThat(onErrorHandler.errorsHandled.stream().map(failure -> failure._2).collect(Collectors.toList())).isEqualTo(events);
    }


    // ------------------------------------------------------------------------------------
    private abstract static class OrderEvent {
    }

    private static class OrderCreatedEvent extends OrderEvent {
    }

    private static class OrderPaidEvent extends OrderEvent {
    }

    private static class OrderCancelledEvent extends OrderEvent {
    }

    private static class RecordingEventConsumer implements Consumer<OrderEvent> {
        final List<OrderEvent> eventsReceived = new ArrayList<>();

        @Override
        public void accept(OrderEvent orderEvent) {
            eventsReceived.add(orderEvent);
        }
    }

    private static class TestOnErrorHandler implements OnErrorHandler<OrderEvent> {
        final List<Triple<Consumer<OrderEvent>, OrderEvent, Exception>> errorsHandled = new ArrayList<>();

        @Override
        public void handle(Consumer<OrderEvent> failingSubscriber, OrderEvent event, Exception error) {
            errorsHandled.add(Tuple.of(failingSubscriber, event, error));
        }

    }
}