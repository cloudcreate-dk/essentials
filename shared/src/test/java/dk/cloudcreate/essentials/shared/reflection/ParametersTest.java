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

package dk.cloudcreate.essentials.shared.reflection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ParametersTest {

    @Test
    void test_parameterTypesMatches() {
        var parameterTypeMatchTestCases = List.of(new ParameterTypeMatchTestCase(params(), params(), true, true),
                                                  new ParameterTypeMatchTestCase(params(), params(), false, true),
                                                  new ParameterTypeMatchTestCase(params(String.class), params(String.class), true, true),
                                                  new ParameterTypeMatchTestCase(params(String.class), params(String.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(long.class), params(long.class), true, true),
                                                  new ParameterTypeMatchTestCase(params(long.class), params(long.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(long.class), params(Long.class), true, false),
                                                  new ParameterTypeMatchTestCase(params(long.class), params(Long.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(Long.class), params(long.class), true, false),
                                                  new ParameterTypeMatchTestCase(params(Long.class), params(long.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(String.class, Long.class, TestEnum.class), params(String.class, Long.class, TestEnum.class), true, true),
                                                  new ParameterTypeMatchTestCase(params(String.class, Long.class, TestEnum.class), params(String.class, Long.class, TestEnum.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(String.class, long.class, TestEnum.class), params(String.class, Long.class, TestEnum.class), true, false),
                                                  new ParameterTypeMatchTestCase(params(String.class, long.class, TestEnum.class), params(String.class, Long.class, TestEnum.class), false, true),
                                                  new ParameterTypeMatchTestCase(params(String.class, Long.class, TestEnum.class), params(String.class, long.class, TestEnum.class), true, false),
                                                  new ParameterTypeMatchTestCase(params(String.class, Long.class, TestEnum.class), params(String.class, long.class, TestEnum.class), false, true)
                                                 );
        parameterTypeMatchTestCases.forEach(testCase -> {
            assertThat(Parameters.parameterTypesMatches(testCase.actualArgumentTypes, testCase.declaredArgumentTypes, testCase.compareAsExactMatch))
                    .describedAs("Actual type argument types: %s", Arrays.toString(testCase.actualArgumentTypes))
                    .isEqualTo(testCase.matches);
        });
    }

    @Test
    void test_argumentTypes() {
        var argumentsVsArgumentsTypes = Map.of("Test", new Class<?>[]{String.class},
                                               3L, new Class<?>[]{Long.class},
                                               3, new Class<?>[]{Integer.class},
                                               TestEnum.A, new Class<?>[]{TestEnum.class},
                                               Parameters.class, new Class<?>[]{Class.class});

        // Corner case test
        assertThat(Parameters.argumentTypes(null))
                .isEqualTo(new Class<?>[0]);

        argumentsVsArgumentsTypes.forEach((arguments, argumentTypes) -> {
            var resolvedArgumentTypes = Parameters.argumentTypes(arguments);
            assertThat(resolvedArgumentTypes)
                    .describedAs("Arguments: %s", Arrays.toString(argumentTypes))
                    .isEqualTo(argumentTypes);
        });

        // Multiple arguments
        assertThat(Parameters.argumentTypes("Test", 3L, TestEnum.B))
                .isEqualTo(new Class<?>[]{String.class, Long.class, TestEnum.class});
        assertThat(Parameters.argumentTypes("Test", null, TestEnum.B))
                .isEqualTo(new Class<?>[]{String.class, Parameters.NULL_ARGUMENT_TYPE.class, TestEnum.class});

    }

    private enum TestEnum {
        A, B, C
    }

    private static class ParameterTypeMatchTestCase {
        private final Class<?>[] actualArgumentTypes;
        private final Class<?>[] declaredArgumentTypes;
        private final boolean    compareAsExactMatch;
        private final boolean    matches;

        private ParameterTypeMatchTestCase(Class<?>[] actualArgumentTypes, Class<?>[] declaredArgumentTypes, boolean compareAsExactMatch, boolean matches) {
            this.actualArgumentTypes = actualArgumentTypes;
            this.declaredArgumentTypes = declaredArgumentTypes;
            this.compareAsExactMatch = compareAsExactMatch;
            this.matches = matches;
        }
    }

    private Class<?>[] params(Class<?>... params) {
        return params;
    }
}