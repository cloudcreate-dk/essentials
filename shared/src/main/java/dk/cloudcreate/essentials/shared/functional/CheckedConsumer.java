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

package dk.cloudcreate.essentials.shared.functional;

import java.util.function.Consumer;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Variant of {@link Consumer} that behaves like {@link Consumer}, but which allows checked {@link Exception}'s to be thrown
 * from its {@link #accept(Object)} method<br>
 *
 * @see #safe(CheckedConsumer)
 * @see #safe(String, CheckedConsumer)
 * <T> – the consumer argument type
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
    /**
     * Wraps a {@link CheckedConsumer} (basically a lambda with one argument that doesn't return any result and which throws a Checked {@link Exception}) by returning a new {@link Consumer} instance<br>
     * The returned {@link Consumer#accept(Object)} method delegates directly to the {@link CheckedConsumer#accept(Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Unless you provide a context-message (using {@link #safe(String, CheckedConsumer)} then any caught checked {@link Exception}'s message also becomes the {@link CheckedExceptionRethrownException}'s message.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Consumer} with the purpose of the calling the {@link Consumer#accept(Object)}.<br>
     * <pre>{@code
     * public void someOperation(Consumer<String> operation) {
     *      // ... Logic ...
     *
     *      operation.accept("some-value");
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Consumer#accept(Object)} occurs when {@link Consumer} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Consumer#accept(Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(value -> {
     *                      try {
     *                          // Logic that uses the File API
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedConsumer} comes to the aid as its {@link #accept(Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedConsumer)}
     * will return a new {@link Consumer} instance with a {@link Consumer#accept(Object)} method that ensures that the {@link CheckedConsumer#accept(Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedConsumer.safe(value -> {
     *                      // Logic that uses the File API that throws IOException
     *                }));
     * }
     * </pre>
     *
     * @param runnableThatCanFailWithACheckedException the {@link CheckedConsumer} instance that will be wrapped as a {@link Consumer}
     * @return a {@link Consumer} with a {@link Consumer#accept(Object)} method that ensures that the {@link CheckedConsumer#accept(Object)} method is called when {@link Consumer#accept(Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedConsumer#accept(Object)} method throws a checked {@link Exception}
     */
    static <T> Consumer<T> safe(CheckedConsumer<T> runnableThatCanFailWithACheckedException) {
        requireNonNull(runnableThatCanFailWithACheckedException, "No CheckedConsumer instance provided");
        return t -> {
            try {
                runnableThatCanFailWithACheckedException.accept(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(e.getMessage(), e);
            }
        };
    }

    /**
     * Wraps a {@link CheckedConsumer} (basically a lambda with one argument that doesn't return any result and which throws a Checked {@link Exception}) by returning a new {@link Consumer} instance<br>
     * The returned {@link Consumer#accept(Object)} method delegates directly to the {@link CheckedConsumer#accept(Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Consumer} with the purpose of the calling the {@link Consumer#accept(Object)}.<br>
     * <pre>{@code
     * public void someOperation(Consumer<String> operation) {
     *      // ... Logic ...
     *
     *      operation.accept("some-value");
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Consumer#accept(Object)} occurs when {@link Consumer} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Consumer#accept(Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(value -> {
     *                      try {
     *                          // Logic that uses the File API
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedConsumer} comes to the aid as its {@link #accept(Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedConsumer)}
     * will return a new {@link Consumer} instance with a {@link Consumer#accept(Object)} method that ensures that the {@link CheckedConsumer#accept(Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedConsumer.safe(msg("Processing file {}", fileName),
     *                                     value -> {
     *                                        // Logic that uses the File API that throws IOException
     *                                     }));
     * }
     * </pre>
     *
     * @param contextMessage                           a string that the described the content for the {@link CheckedConsumer} and which will be used as message in any {@link CheckedExceptionRethrownException} thrown
     * @param runnableThatCanFailWithACheckedException the {@link CheckedConsumer} instance that will be wrapped as a {@link Consumer}
     * @return a {@link Consumer} with a {@link Consumer#accept(Object)} method that ensures that the {@link CheckedConsumer#accept(Object)} method is called when {@link Consumer#accept(Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedConsumer#accept(Object)} method throws a checked {@link Exception}
     */
    static <T> Consumer<T> safe(String contextMessage,
                                CheckedConsumer<T> runnableThatCanFailWithACheckedException) {
        requireNonNull(contextMessage, "No contextMessage provided");
        requireNonNull(runnableThatCanFailWithACheckedException, "No CheckedConsumer instance provided");
        return t -> {
            try {
                runnableThatCanFailWithACheckedException.accept(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(contextMessage, e);
            }
        };
    }

    /**
     * This method performs the operation that may fail with a Checked {@link Exception}
     *
     * @param arg the argument to the consumer
     * @throws Exception this method can throw both checked {@link Exception}'s as well as {@link RuntimeException}'s
     */
    void accept(T arg) throws Exception;
}
