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

import dk.cloudcreate.essentials.shared.FailFast;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * The result of {@link StopWatch#time(Supplier)}
 *
 * @param <R> the type of result
 */
public class TimingResult<R> {
    /**
     * The result of the operation performed
     */
    public final R        result;
    /**
     * How long did the operation performed take
     */
    public final Duration duration;

    /**
     * Create a new {@link TimingResult}
     * @param result The result of the operation performed
     * @param duration  How long did the operation performed take
     * @return the {@link TimingResult}
     */
    public TimingResult(R result, Duration duration) {
        this.result = result;
        this.duration = FailFast.requireNonNull(duration, "duration is null");
    }

    /**
     * Create a new {@link TimingResult}
     * @param result The result of the operation performed
     * @param duration  How long did the operation performed take
     * @param <R>  the type of result
     * @return the {@link TimingResult}
     */
    public static <R> TimingResult<R> of(R result, Duration duration) {
        return new TimingResult<>(result, duration);
    }

    /**
     * The result of the operation performed
     * @return The result of the operation performed
     */
    public R getResult() {
        return result;
    }

    /**
     * How long did the operation performed take
     * @return How long did the operation performed take
     */
    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "TimingResult{" +
                "duration=" + duration +
                ", result=" + result +
                '}';
    }
}
