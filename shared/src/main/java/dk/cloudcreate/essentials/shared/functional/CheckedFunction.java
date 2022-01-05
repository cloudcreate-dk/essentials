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

import java.util.function.Function;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Variant of {@link Function} that behaves like {@link Function}, but which allows checked {@link Exception}'s to be thrown
 * from its {@link #apply(Object)} method<br>
 *
 *
 * <T> – the function argument type
 * <R> – the function result type
 *
 * @see #safe(CheckedFunction)
 * @see #safe(String, CheckedFunction)
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    /**
     * Wraps a {@link CheckedFunction} (basically a lambda with one argument that returns a result and which throws a Checked {@link Exception}) by returning a new {@link Function} instance<br>
     * The returned {@link Function#apply(Object)} method delegates directly to the {@link CheckedFunction#apply(Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Unless you provide a context-message (using {@link #safe(String, CheckedFunction)} then any caught checked {@link Exception}'s message also becomes the {@link CheckedExceptionRethrownException}'s message.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Function} with the purpose of the calling the {@link Function#apply(Object)}.<br>
     * <pre>{@code
     * public void someOperation(Function<Integer, String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.apply(10);
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Function#apply(Object)} occurs when {@link Function} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Function#apply(Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(value -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedFunction} comes to the aid as its {@link #apply(Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedFunction)}
     * will return a new {@link Function} instance with a {@link Function#apply(Object)} method that ensures that the {@link CheckedFunction#apply(Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckFunction.safe(value -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param functionThatCanFailWithACheckedException the {@link CheckedFunction} instance that will be wrapped as a {@link Function}
     * @param <T>                                      the argument type
     * @param <R>                                      the return type
     * @return a {@link Function} with a {@link Function#apply(Object)} method that ensures that the {@link CheckedFunction#apply(Object)} method is called when {@link Function#apply(Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedFunction#apply(Object)} method throws a checked {@link Exception}
     */
    static <T, R> Function<T, R> safe(CheckedFunction<T, R> functionThatCanFailWithACheckedException) {
        requireNonNull(functionThatCanFailWithACheckedException, "No CheckedFunction instance provided");
        return t -> {
            try {
                return functionThatCanFailWithACheckedException.apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(e.getMessage(), e);
            }
        };
    }

    /**
     * Wraps a {@link CheckedFunction} (basically a lambda with one argument that returns a result and which throws a Checked {@link Exception}) by returning a new {@link Function} instance<br>
     * The returned {@link Function#apply(Object)} method delegates directly to the {@link CheckedFunction#apply(Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Function} with the purpose of the calling the {@link Function#apply(Object)}.<br>
     * <pre>{@code
     * public void someOperation(Function<Integer, String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.apply(10);
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Function#apply(Object)} occurs when {@link Function} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Function#apply(Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(value -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedFunction} comes to the aid as its {@link #apply(Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedFunction)}
     * will return a new {@link Function} instance with a {@link Function#apply(Object)} method that ensures that the {@link CheckedFunction#apply(Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckFunction.safe(msg("Processing file {}", fileName),
     *                value -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param contextMessage                           a string that the described the content for the {@link CheckedFunction} and which will be used as message in any {@link CheckedExceptionRethrownException} thrown
     * @param functionThatCanFailWithACheckedException the {@link CheckedFunction} instance that will be wrapped as a {@link Function}
     * @param <T>                                      the argument type
     * @param <R>                                      the return type
     * @return a {@link Function} with a {@link Function#apply(Object)} method that ensures that the {@link CheckedFunction#apply(Object)} method is called when {@link Function#apply(Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedFunction#apply(Object)} method throws a checked {@link Exception}
     */
    static <T, R> Function<T, R> safe(String contextMessage,
                                      CheckedFunction<T, R> functionThatCanFailWithACheckedException) {
        requireNonNull(contextMessage, "No contextMessage provided");
        requireNonNull(functionThatCanFailWithACheckedException, "No CheckedFunction instance provided");
        return t -> {
            try {
                return functionThatCanFailWithACheckedException.apply(t);
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
     * @param argument the function argument
     * @return the result of performing the function
     * @throws Exception this method can throw both checked {@link Exception}'s as well as {@link RuntimeException}'s
     */
    R apply(T argument) throws Exception;
}
