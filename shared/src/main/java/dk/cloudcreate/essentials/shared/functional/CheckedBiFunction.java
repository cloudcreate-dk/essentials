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

import java.util.function.BiFunction;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Variant of {@link BiFunction} that behaves like {@link BiFunction}, but which allows checked {@link Exception}'s to be thrown
 * from its {@link #apply(Object, Object)} method<br>
 *
 *
 * <T1> – the first function argument type
 * <T2> – the second function argument type
 * <R> – the function result type
 *
 * @see #safe(CheckedBiFunction)
 * @see #safe(String, CheckedBiFunction)
 */
@FunctionalInterface
public interface CheckedBiFunction<T1, T2, R> {
    /**
     * Wraps a {@link CheckedBiFunction} (basically a lambda with two arguments that returns a result and which throws a Checked {@link Exception}) by returning a new {@link BiFunction} instance<br>
     * The returned {@link BiFunction#apply(Object, Object)} method delegates directly to the {@link CheckedBiFunction#apply(Object, Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Unless you provide a context-message (using {@link #safe(String, CheckedBiFunction)} then any caught checked {@link Exception}'s message also becomes the {@link CheckedExceptionRethrownException}'s message.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link BiFunction} with the purpose of the calling the {@link BiFunction#apply(Object, Object)}.<br>
     * <pre>{@code
     * public void someOperation(Function<Integer, Boolean, String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.apply(10, true);
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link BiFunction#apply(Object, Object)} occurs when {@link BiFunction} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link BiFunction#apply(Object, Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation((value, enabled) -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedBiFunction} comes to the aid as its {@link #apply(Object, Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedBiFunction)}
     * will return a new {@link BiFunction} instance with a {@link BiFunction#apply(Object, Object)} method that ensures that the {@link CheckedBiFunction#apply(Object, Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedBiFunction.safe((value, enabled) -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param functionThatCanFailWithACheckedException the {@link CheckedBiFunction} instance that will be wrapped as a {@link BiFunction}
     * @param <T1>                                     the first argument type
     * @param <T2>                                     the second argument type
     * @param <R>                                      the return type
     * @return a {@link BiFunction} with a {@link BiFunction#apply(Object, Object)} method that ensures that the {@link CheckedBiFunction#apply(Object, Object)} method is called when {@link BiFunction#apply(Object, Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedBiFunction#apply(Object, Object)} method throws a checked {@link Exception}
     */
    static <T1, T2, R> BiFunction<T1, T2, R> safe(CheckedBiFunction<T1, T2, R> functionThatCanFailWithACheckedException) {
        requireNonNull(functionThatCanFailWithACheckedException, "No CheckedBiFunction instance provided");
        return (t1, t2) -> {
            try {
                return functionThatCanFailWithACheckedException.apply(t1, t2);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(e.getMessage(), e);
            }
        };
    }

    /**
     * Wraps a {@link CheckedBiFunction} (basically a lambda with two arguments that returns a result and which throws a Checked {@link Exception}) by returning a new {@link BiFunction} instance<br>
     * The returned {@link BiFunction#apply(Object, Object)} method delegates directly to the {@link CheckedBiFunction#apply(Object, Object)} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link BiFunction} with the purpose of the calling the {@link BiFunction#apply(Object, Object)}.<br>
     * <pre>{@code
     * public void someOperation(Function<Integer, Boolean, String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.apply(10, true);
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link BiFunction#apply(Object, Object)} occurs when {@link BiFunction} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link BiFunction#apply(Object, Object)} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation((value, enabled) -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedBiFunction} comes to the aid as its {@link #apply(Object, Object)} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedBiFunction)}
     * will return a new {@link BiFunction} instance with a {@link BiFunction#apply(Object, Object)} method that ensures that the {@link CheckedBiFunction#apply(Object, Object)} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedBiFunction.safe(msg("Processing file {}", fileName),
     *                (value, enabled) -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param contextMessage                           a string that the described the content for the {@link CheckedBiFunction} and which will be used as message in any {@link CheckedExceptionRethrownException} thrown
     * @param functionThatCanFailWithACheckedException the {@link CheckedBiFunction} instance that will be wrapped as a {@link BiFunction}
     * @param <T1>                                     the first argument type
     * @param <T2>                                     the second argument type
     * @param <R>                                      the return type
     * @return a {@link BiFunction} with a {@link BiFunction#apply(Object, Object)} method that ensures that the {@link CheckedBiFunction#apply(Object, Object)} method is called when {@link BiFunction#apply(Object, Object)} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedBiFunction#apply(Object, Object)} method throws a checked {@link Exception}
     */
    static <T1, T2, R> BiFunction<T1, T2, R> safe(String contextMessage,
                                                  CheckedBiFunction<T1, T2, R> functionThatCanFailWithACheckedException) {
        requireNonNull(contextMessage, "No contextMessage provided");
        requireNonNull(functionThatCanFailWithACheckedException, "No CheckedBiFunction instance provided");
        return (t1, t2) -> {
            try {
                return functionThatCanFailWithACheckedException.apply(t1, t2);
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
     * @param arg1 the first function argument
     * @param arg2 the second function argument
     * @return the result of performing the function
     * @throws Exception this method can throw both checked {@link Exception}'s as well as {@link RuntimeException}'s
     */
    R apply(T1 arg1, T2 arg2) throws Exception;
}
