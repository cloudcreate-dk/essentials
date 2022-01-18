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

import java.util.function.Consumer;

/**
 * Error Handler interface for the {@link LocalEventBus}
 *
 * @param <EVENT_TYPE> the event type being published by the event bus
 */
@FunctionalInterface
public interface OnErrorHandler<EVENT_TYPE> {
    /**
     * Will be called if a given consumer/subscriber fails to handle a message
     *
     * @param failingSubscriber the subscriber instance that failed
     * @param event             the event the subscriber failed to handle
     * @param exception         the exception thrown by the <code>failingSubscriber</code>
     */
    void handle(Consumer<EVENT_TYPE> failingSubscriber, EVENT_TYPE event, Exception exception);
}
