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

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class CheckedRunnableTest {

    @Test
    void test_the_CheckedRunnables_run_method_works_like_Runnables_run_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean runCalled = false;
        };

        runTester(CheckedRunnable.safe(() -> {
            control.runCalled = true;
        }));

        assertThat(control.runCalled).isTrue();
    }

    @Test
    void test_the_CheckedRunnables_run_method_with_contextmessage_works_like_Runnables_run_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean runCalled = false;
        };

        runTester(CheckedRunnable.safe("context-message",
                                       () -> {
                                           control.runCalled = true;
                                       }));

        assertThat(control.runCalled).isTrue();
    }

    @Test
    void test_the_CheckedRunnables_run_method_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean runCalled = false;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedRunnable.safe(() -> {
                                       control.runCalled = true;
                                       throw rootCause;
                                   }))
                          ).hasCause(rootCause);
        assertThat(control.runCalled).isTrue();
    }

    @Test
    void test_the_CheckedRunnables_run_method_with_contextmessage_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean runCalled = false;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedRunnable.safe("context-message",
                                                                  () -> {
                                                                      control.runCalled = true;
                                                                      throw rootCause;
                                                                  }))
                          ).isInstanceOf(CheckedExceptionRethrownException.class)
                           .hasMessage("context-message")
                           .hasRootCause(rootCause);
        assertThat(control.runCalled).isTrue();
    }

    @Test
    void test_the_CheckedRunnables_run_method_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean runCalled = false;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedRunnable.safe(() -> {
                                       control.runCalled = true;
                                       throw rootCause;
                                   }))
                          ).isEqualTo(rootCause);
        assertThat(control.runCalled).isTrue();
    }

    @Test
    void test_the_CheckedRunnables_run_method_with_contextmessage_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean runCalled = false;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedRunnable.safe("context-message",
                                                                  () -> {
                                                                      control.runCalled = true;
                                                                      throw rootCause;
                                                                  }))
                          ).isEqualTo(rootCause);
        assertThat(control.runCalled).isTrue();
    }

    private static void runTester(Runnable runnable) {
        runnable.run();
    }

}