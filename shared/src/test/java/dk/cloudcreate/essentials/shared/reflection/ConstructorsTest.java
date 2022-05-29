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


import dk.cloudcreate.essentials.shared.reflection.test_subjects.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorsTest {
    @Test
    void test_hasDefaultConstructor() {
        assertThat(Reflector.reflectOn(TestReflectionClass.class).hasDefaultConstructor()).isTrue();
        assertThat(Reflector.reflectOn(BaseTestReflectionClass.class).hasDefaultConstructor()).isFalse();
    }

    @Test
    void test_constructors() {
        var constructorsAndParametersMap = Map.of(BaseInterface.class, List.of(),
                                                  TestInterface.class, List.of(),
                                                  BaseTestReflectionClass.class, listWithOneArrayElement(new Class[]{String.class}),
                                                  TestReflectionClass.class, List.of(new Class[0], new Class[]{String.class, boolean.class}));

        constructorsAndParametersMap.forEach((_class, constructorsAndParameters) -> {
            var constructorsFound = Constructors.constructors(_class);
            constructorsAndParameters.forEach(parameters -> {
                assertThat(constructorsFound.stream().filter(constructor -> Parameters.parameterTypesMatches(constructor.getParameterTypes(), ((Class<?>[]) parameters), true)).count())
                        .describedAs("Type '%' - constructor parameters '%s'", _class.getName(), Arrays.toString((Class<?>[]) parameters))
                        .isEqualTo(1);
            });
            assertThat(constructorsFound.size())
                    .describedAs("Type '%'", _class.getName())
                    .isEqualTo(constructorsAndParameters.size());
        });
    }

    private List<Class<?>[]> listWithOneArrayElement(Class<?>[] element) {
        List<Class<?>[]> list = new ArrayList<>(1);
        list.add(element);
        return list;
    }
}