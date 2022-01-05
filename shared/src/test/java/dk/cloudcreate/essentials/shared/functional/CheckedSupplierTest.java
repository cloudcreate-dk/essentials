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
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

class CheckedSupplierTest {

    @Test
    void test_the_CheckedSuppliers_get_method_works_like_Suppliers_get_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var result = runTester(CheckedSupplier.safe(() -> {
            control.getCalled = true;
            return "test";
        }));

        assertThat(control.getCalled).isTrue();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void test_the_CheckedSuppliers_get_method_with_contextmessage_works_like_Suppliers_get_method_when_no_exception_has_been_thrown() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var result = runTester(CheckedSupplier.safe("context-message",
                                       () -> {
                                           control.getCalled = true;
                                           return "test";
                                       }));

        assertThat(control.getCalled).isTrue();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void test_the_CheckedSuppliers_get_method_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedSupplier.safe(() -> {
                                       control.getCalled = true;
                                       throw rootCause;
                                   }))
                          ).hasCause(rootCause);
        assertThat(control.getCalled).isTrue();
    }

    @Test
    void test_the_CheckedSuppliers_get_method_with_contextmessage_catches_and_rethrows_a_checked_exception() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var rootCause = new IOException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedSupplier.safe("context-message",
                                                                  () -> {
                                                                      control.getCalled = true;
                                                                      throw rootCause;
                                                                  }))
                          ).isInstanceOf(CheckedExceptionRethrownException.class)
                           .hasMessage("context-message")
                           .hasRootCause(rootCause);
        assertThat(control.getCalled).isTrue();
    }

    @Test
    void test_the_CheckedSuppliers_get_method_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedSupplier.safe(() -> {
                                       control.getCalled = true;
                                       throw rootCause;
                                   }))
                          ).isEqualTo(rootCause);
        assertThat(control.getCalled).isTrue();
    }

    @Test
    void test_the_CheckedSuppliers_get_method_with_contextmessage_does_not_catch_a_runtime_exception() {
        var control = new Object() {
            boolean getCalled = false;
        };

        var rootCause = new RuntimeException("Test");
        assertThatThrownBy(() ->
                                   runTester(CheckedSupplier.safe("context-message",
                                                                  () -> {
                                                                      control.getCalled = true;
                                                                      throw rootCause;
                                                                  }))
                          ).isEqualTo(rootCause);
        assertThat(control.getCalled).isTrue();
    }

    private static <R> R runTester(Supplier<R> supplier) {
        return supplier.get();
    }

}