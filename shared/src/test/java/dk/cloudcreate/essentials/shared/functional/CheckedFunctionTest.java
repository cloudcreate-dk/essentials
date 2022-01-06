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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

class CheckedFunctionTest {

    @Test
    void test_the_CheckedFunctions_apply_method_works_like_Functions_apply_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var result = runTester(100, CheckedFunction.safe(argument -> {
            control.applyCalled = true;
            control.argumentValue = argument;
            return "test";
        }));

        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
        assertThat(result).isEqualTo("test");

    }

    @Test
    void test_the_CheckedFunctions_apply_method_with_contextmessage_works_like_Functions_apply_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var result = runTester(100, CheckedFunction.safe("context-message",
                                                         argument -> {
                                                             control.applyCalled = true;
                                                             control.argumentValue = argument;
                                                             return "test";
                                                         }));

        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
        assertThat(result).isEqualTo("test");
    }

    @Test
    void test_the_CheckedFunctions_apply_method_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(100, CheckedFunction.safe(argument -> {
                                       control.applyCalled = true;
                                       control.argumentValue = argument;
                                       throw rootCause;
                                   }))
                          ).hasCause(rootCause);
        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
    }

    @Test
    void test_the_CheckedFunctions_apply_method_with_contextmessage_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(100, CheckedFunction.safe("context-message",
                                                                       argument -> {
                                                                           control.applyCalled = true;
                                                                           control.argumentValue = argument;
                                                                           throw rootCause;
                                                                       }))
                          ).isInstanceOf(CheckedExceptionRethrownException.class)
                           .hasMessage("context-message")
                           .hasRootCause(rootCause);
        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
    }

    @Test
    void test_the_CheckedFunctions_apply_method_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(100, CheckedFunction.safe(argument -> {
                                       control.applyCalled = true;
                                       control.argumentValue = argument;
                                       throw rootCause;
                                   }))
                          ).isEqualTo(rootCause);
        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
    }

    @Test
    void test_the_CheckedFunctions_apply_method_with_contextmessage_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean applyCalled = false;
            Integer argumentValue = null;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(100, CheckedFunction.safe("context-message",
                                                                       argument -> {
                                                                           control.applyCalled = true;
                                                                           control.argumentValue = argument;
                                                                           throw rootCause;
                                                                       }))
                          ).isEqualTo(rootCause);
        assertThat(control.applyCalled).isTrue();
        assertThat(control.argumentValue).isEqualTo(100);
    }

    private static <T, R> R runTester(T t, Function<T, R> function) {
        return function.apply(t);
    }

}