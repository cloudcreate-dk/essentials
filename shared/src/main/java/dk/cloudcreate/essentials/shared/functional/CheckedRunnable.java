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

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Variant of {@link Runnable} that behaves like {@link Runnable}, but which allows checked {@link Exception}'s to be thrown
 * from its {@link #run()} method<br>
 *
 * @see #safe(CheckedRunnable)
 * @see #safe(String, CheckedRunnable)
 */
@FunctionalInterface
public interface CheckedRunnable {
    /**
     * Wraps a {@link CheckedRunnable} (basically a lambda with no arguments that doesn't return any result and which throws a Checked {@link Exception}) by returning a new {@link Runnable} instance<br>
     * The returned {@link Runnable#run()} method delegates directly to the {@link CheckedRunnable#run()} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Unless you provide a context-message (using {@link #safe(String, CheckedRunnable)} then any caught checked {@link Exception}'s message also becomes the {@link CheckedExceptionRethrownException}'s message.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Runnable} with the purpose of the calling the {@link Runnable#run()}.<br>
     * <pre>{@code
     * public void someOperation(Runnable operation) {
     *      // ... Logic ...
     *
     *      operation.run();
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Runnable#run()} occurs when {@link Runnable} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Runnable#run()} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(() -> {
     *                      try {
     *                          // Logic that uses the File API
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedRunnable} comes to the aid as its {@link #run()} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedRunnable)}
     * will return a new {@link Runnable} instance with a {@link Runnable#run()} method that ensures that the {@link CheckedRunnable#run()} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedRunnable.safe(() -> {
     *                      // Logic that uses the File API that throws IOException
     *                }));
     * }
     * </pre>
     *
     * @param runnableThatCanFailWithACheckedException the {@link CheckedRunnable} instance that will be wrapped as a {@link Runnable}
     * @return a {@link Runnable} with a {@link Runnable#run()} method that ensures that the {@link CheckedRunnable#run()} method is called when {@link Runnable#run()} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedRunnable#run()} method throws a checked {@link Exception}
     */
    static Runnable safe(CheckedRunnable runnableThatCanFailWithACheckedException) {
        requireNonNull(runnableThatCanFailWithACheckedException, "No CheckedRunnable instance provided");
        return () -> {
            try {
                runnableThatCanFailWithACheckedException.run();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new CheckedExceptionRethrownException(e.getMessage(), e);
            }
        };
    }

    /**
     * Wraps a {@link CheckedRunnable} (basically a lambda with no arguments that doesn't return any result and which throws a Checked {@link Exception}) by returning a new {@link Runnable} instance<br>
     * The returned {@link Runnable#run()} method delegates directly to the {@link CheckedRunnable#run()} and catches any thrown checked {@link Exception}'s and rethrows them as a {@link CheckedExceptionRethrownException}<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.<br>
     * <br>
     * Usage example:<br>
     * Let's say we have a method called <code>someOperation</code> that cannot change, but which accepts a {@link Runnable} with the purpose of the calling the {@link Runnable#run()}.<br>
     * <pre>{@code
     * public void someOperation(Runnable operation) {
     *      // ... Logic ...
     *
     *      operation.run();
     *
     *      // ... More logic ---
     * }
     * }</pre>
     * The problem will {@link Runnable#run()} occurs when {@link Runnable} calls any API that throws a checked {@link Exception}, e.g. the {@link java.io.File} API.<br>
     * Since {@link Runnable#run()} doesn't define that it throws any {@link Exception}'s we're forced to add a try/catch to handle the {@link java.io.IOException} for the code to compile:
     * <pre>{@code
     *  someOperation(() -> {
     *                      try {
     *                          // Logic that uses the File API
     *                      } catch (IOException e) {
     *                          throw new RuntimeException(e);
     *                      }
     *                }));
     * }</pre>
     * <br>
     * This is where the {@link CheckedRunnable} comes to the aid as its {@link #run()} methods defines that it throws a Checked {@link Exception} and its {@link #safe(CheckedRunnable)}
     * will return a new {@link Runnable} instance with a {@link Runnable#run()} method that ensures that the {@link CheckedRunnable#run()} method is called and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}:
     * <pre>{@code
     *  someOperation(CheckedRunnable.safe(msg("Processing file {}", fileName),
     *                                     () -> {
     *                                        // Logic that uses the File API that throws IOException
     *                                     }));
     * }
     * </pre>
     *
     * @param contextMessage                           a string that the described the content for the {@link CheckedRunnable} and which will be used as message in any {@link CheckedExceptionRethrownException} thrown
     * @param runnableThatCanFailWithACheckedException the {@link CheckedRunnable} instance that will be wrapped as a {@link Runnable}
     * @return a {@link Runnable} with a {@link Runnable#run()} method that ensures that the {@link CheckedRunnable#run()} method is called when {@link Runnable#run()} and any checked {@link Exception}'s thrown will be
     * caught and rethrown as a {@link CheckedExceptionRethrownException}.<br>
     * Any {@link RuntimeException}'s thrown aren't caught and the calling code will receive the original {@link RuntimeException} thrown.
     * @throws CheckedExceptionRethrownException in case the {@link CheckedRunnable#run()} method throws a checked {@link Exception}
     */
    static Runnable safe(String contextMessage,
                         CheckedRunnable runnableThatCanFailWithACheckedException) {
        requireNonNull(contextMessage, "No contextMessage provided");
        requireNonNull(runnableThatCanFailWithACheckedException, "No CheckedRunnable instance provided");
        return () -> {
            try {
                runnableThatCanFailWithACheckedException.run();
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
     * @throws Exception this method can throw both checked {@link Exception}'s as well as {@link RuntimeException}'s
     */
    void run() throws Exception;
}
