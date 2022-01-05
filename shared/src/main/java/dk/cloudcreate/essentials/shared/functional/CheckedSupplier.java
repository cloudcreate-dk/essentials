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

import java.util.function.Supplier;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Variant of {@link Supplier} that behaves like {@link Supplier}, but which allows checked {@link Exception}'s to be thrown
 * from its {@link #get()} method<br>
 *
 * @param <R> the return type
 *
 * @see #safe(CheckedSupplier)
 * @see #safe(String, CheckedSupplier)
 */
@FunctionalInterface
public interface CheckedSupplier<R> {
    /**
     * Wraps a {@link CheckedSupplier} (basically a lambda with no arguments that returns a result and which throws a Checked {@link Exception}) by returning a new {@link Supplier} instance<br>
     * The returned {@link Supplier#get()} method delegates directly to the {@link CheckedSupplier#get()} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Unless you provide a context-message (using {@link #safe(String, CheckedSupplier)} then any caught checked {@link Exception}'s message also becomes the {@link CheckedExceptionRethrownException}'s message.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Supplier} with the purpose of the calling the {@link Supplier#get()}.<br>
     * <pre>{@code
     * public void someOperation(Supplier<String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.get();
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Supplier#get()} occurs when {@link Supplier} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Supplier#get()} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(() -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedSupplier} comes to the aid as its {@link #get()} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedSupplier)}
     * will return a new {@link Supplier} instance with a {@link Supplier#get()} method that ensures that the {@link CheckedSupplier#get()} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedSupplier.safe(() -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param supplierThatCanFailedWithACheckedException the {@link CheckedSupplier} instance that will be wrapped as a {@link Supplier}
     * @param <R>                                        the return type
     * @return a {@link Supplier} with a {@link Supplier#get()} method that ensures that the {@link CheckedSupplier#get()} method is called when {@link Supplier#get()} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedSupplier#get()} method throws a checked {@link Exception}
     */
    static <R> Supplier<R> safe(CheckedSupplier<R> supplierThatCanFailedWithACheckedException) {
        requireNonNull(supplierThatCanFailedWithACheckedException, "No CheckedSupplier instance provided");
        return () -> {
            try {
                return supplierThatCanFailedWithACheckedException.get();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(e.getMessage(), e);
            }
        };
    }

    /**
     * Wraps a {@link CheckedSupplier} (basically a lambda with no arguments that returns a result and which throws a Checked {@link Exception}) by returning a new {@link Supplier} instance<br>
     * The returned {@link Supplier#get()} method delegates directly to the {@link CheckedSupplier#get()} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Supplier} with the purpose of the calling the {@link Supplier#get()}.<br>
     * <pre>{@code
     * public void someOperation(Supplier<String> operation) {
     *      // ... Logic ...
     *
     *      String value = operation.get();
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Supplier#get()} occurs when {@link Supplier} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Supplier#get()} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(() -> {
     *                      try {
     *                          // Logic that uses the File API
     *                          return "some-value";
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedSupplier} comes to the aid as its {@link #get()} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedSupplier)}
     * will return a new {@link Supplier} instance with a {@link Supplier#get()} method that ensures that the {@link CheckedSupplier#get()} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedSupplier.safe(msg("Processing file {}", fileName),
     *                () -> {
     *                      // Logic that uses the File API that throws IOException
     *                      return "some-value";
     *                }));
     * }
     * </pre>
     *
     * @param contextMessage                             a string that the described the content for the {@link CheckedSupplier} and which will be used as message in any {@link CheckedExceptionRethrownException} thrown
     * @param supplierThatCanFailedWithACheckedException the {@link CheckedSupplier} instance that will be wrapped as a {@link Supplier}
     * @param <R>                                        the return type
     * @return a {@link Supplier} with a {@link Supplier#get()} method that ensures that the {@link CheckedSupplier#get()} method is called when {@link Supplier#get()} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedSupplier#get()} method throws a checked {@link Exception}
     */
    static <R> Supplier<R> safe(String contextMessage, CheckedSupplier<R> supplierThatCanFailedWithACheckedException) {
        requireNonNull(contextMessage, "No contextMessage provided");
        requireNonNull(supplierThatCanFailedWithACheckedException, "No CheckedSupplier instance provided");
        return () -> {
            try {
                return supplierThatCanFailedWithACheckedException.get();
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
     * @return the result of performing the operation
     * @throws Exception this method can throw both checked {@link Exception}'s as well as {@link RuntimeException}'s
     */
    R get() throws Exception;
}
