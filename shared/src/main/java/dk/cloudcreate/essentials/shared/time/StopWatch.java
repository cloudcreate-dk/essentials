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

package dk.cloudcreate.essentials.shared.time;

import java.time.Duration;
import java.util.function.Supplier;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Stop watch feature that allows you to time operations
 */
public class StopWatch {
    /**
     * Time how long it takes to perform an operation
     *
     * @param operation the operation to perform
     * @return the time it took to perform the operation as a {@link Duration}
     */
    public static Duration time(Runnable operation) {
        requireNonNull(operation, "You must supply an operation to time");
        long start = System.nanoTime();
        operation.run();
        long finish = System.nanoTime();
        return Duration.ofNanos(finish - start);
    }

    /**
     * Time how long it takes to perform an operation
     *
     * @param operation the operation to perform
     * @param <R>       The return type of the operation
     * @return the timing result (the result together with the time it took to perform the operation as a {@link Duration})
     */
    public static <R> TimingResult<R> time(Supplier<R> operation) {
        requireNonNull(operation, "You must supply an operation to time");
        long start = System.nanoTime();
        R result = operation.get();
        long finish = System.nanoTime();
        return TimingResult.of(result, Duration.ofNanos(finish - start));
    }

}
