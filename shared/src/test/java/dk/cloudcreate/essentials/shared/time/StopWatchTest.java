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

import dk.cloudcreate.essentials.shared.functional.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StopWatchTest {

    @Test
    void time() {
        var duration = StopWatch.time(CheckedRunnable.safe(() -> Thread.sleep(500)));
        assertThat(duration).isGreaterThanOrEqualTo(Duration.ofMillis(500));
    }

    @Test
    void timeWithResult() {
        var resultValue = "some-value";
        var result = StopWatch.time(CheckedSupplier.safe(() -> {
            Thread.sleep(500);
            return resultValue;
        }));
        assertThat(result).isNotNull();
        assertThat(result.getDuration()).isGreaterThanOrEqualTo(Duration.ofMillis(500));
        assertThat(result.duration).isGreaterThanOrEqualTo(Duration.ofMillis(500));
        assertThat(result.getResult()).isEqualTo(resultValue);
        assertThat(result.result).isEqualTo(resultValue);
    }
}