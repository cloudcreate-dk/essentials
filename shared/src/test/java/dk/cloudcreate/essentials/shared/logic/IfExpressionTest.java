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

package dk.cloudcreate.essentials.shared.logic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.*;

import static dk.cloudcreate.essentials.shared.logic.IfExpression.If;
import static org.assertj.core.api.Assertions.assertThat;

class IfExpressionTest {
    @Test
    void test_If_ElseIf_Else_ElseIf_Else_With_No_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(value > 2,
                                                   "Higher").
                                                ElseIf(value == 2,
                                                       "Same").
                                                Else("Lower")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower", "Same", "Higher"));
    }

    @Test
    void test_If_ElseIf_Else_With_ReturnValue_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(value > 2,
                                                   () -> "Higher").
                                                ElseIf(value == 2,
                                                        () -> "Same").
                                                Else(() -> "Lower")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower", "Same", "Higher"));
    }

    @Test
    void test_If_ElseIf_Else_With_IfPredicate_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(() -> value > 2,
                                                   "Higher").
                                                ElseIf(() -> value == 2,
                                                        "Same").
                                                Else("Lower")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower", "Same", "Higher"));
    }

    @Test
    void test_If_ElseIf_Else_With_IfPredicate_and_ReturnValue_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(() -> value > 2,
                                                   () -> "Higher").
                                                ElseIf(() -> value == 2,
                                                        () -> "Same").
                                                Else(() -> "Lower")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower", "Same", "Higher"));
    }

    // ------------ If Multiple Else If  ---------------
    @Test
    void test_If_ElseIf_ElseIf_Else_With_IfPredicate_and_ReturnValue_Lambdas() {
        var result = IntStream.range(0, 4)
                              .mapToObj(value ->
                                                If(() -> value == 0,
                                                   () -> "Zero").
                                                ElseIf(() -> value == 2,
                                                        () -> "Same").
                                                ElseIf(() -> value > 2,
                                                        () -> "Higher").
                                                Else(() -> "Lower")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Zero", "Lower", "Same", "Higher"));
    }

    // ------------ If Else ---------------
    @Test
    void test_If_Else_ElseIf_Else_With_No_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(value > 2,
                                                   "Higher").
                                                Else("Lower or Same")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower or Same", "Lower or Same", "Higher"));
    }

    @Test
    void test_If_Else_With_ReturnValue_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(value > 2,
                                                   () -> "Higher").
                                                Else(() -> "Lower or Same")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower or Same", "Lower or Same", "Higher"));
    }

    @Test
    void test_If_Else_With_IfPredicate_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(() -> value > 2,
                                                   "Higher").
                                                Else("Lower or Same")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower or Same", "Lower or Same", "Higher"));
    }

    @Test
    void test_If_Else_With_IfPredicate_and_ReturnValue_Lambdas() {
        var result = IntStream.range(1, 4)
                              .mapToObj(value ->
                                                If(() -> value > 2,
                                                   () -> "Higher").
                                                Else(() -> "Lower or Same")
                                       ).collect(Collectors.toList());

        assertThat(result).isEqualTo(List.of("Lower or Same", "Lower or Same", "Higher"));
    }
}