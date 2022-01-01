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

import dk.cloudcreate.essentials.shared.functional.tuple.*;
import dk.cloudcreate.essentials.shared.reflection.test_subjects.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ClassesTest {
    @Test
    void test_forName() {
        // Given
        Map<String, Class<?>> classNameToMatchingClassMap = Map.of(BaseInterface.class.getName(), BaseInterface.class,
                                                                   TestInterface.class.getName(), TestInterface.class,
                                                                   BaseTestReflectionClass.class.getName(), BaseTestReflectionClass.class,
                                                                   TestReflectionClass.class.getName(), TestReflectionClass.class);

        classNameToMatchingClassMap.forEach((className, matchingClass) -> {
            assertThatNoException()
                    .describedAs("Class-name '%s'", className)
                    .isThrownBy(() -> Classes.forName(className));

            assertThat(Classes.forName(className))
                    .describedAs("Class-name '%s'", className)
                    .isEqualTo(matchingClass);
        });
    }

    @Test
    void test_superClasses() {
        // Given
        Map<Class<?>, List<Class<?>>> classToSuperClassesMap = Map.of(BaseInterface.class, List.of(),
                                                                      TestInterface.class, List.of(),
                                                                      BaseTestReflectionClass.class, List.of(Object.class),
                                                                      TestReflectionClass.class, List.of(BaseTestReflectionClass.class, Object.class));

        classToSuperClassesMap.forEach((_class, superClasses) ->
                                               assertThat(Classes.superClasses(_class))
                                                       .describedAs("Class '%s'", _class.getName())
                                                       .isEqualTo(superClasses));
    }

    @Test
    void test_compareTypeSpecificity() {
        // Given
        Map<LeftAndRightSide, Integer> classesVsExpectedCompareValue = Map.of(new LeftAndRightSide(Object.class, Object.class), 0,
                                                                              new LeftAndRightSide(String.class, String.class), 0,
                                                                              new LeftAndRightSide(Object.class, String.class), -1,
                                                                              new LeftAndRightSide(String.class, Object.class), 1,
                                                                              new LeftAndRightSide(CharSequence.class, String.class), -1,
                                                                              new LeftAndRightSide(String.class, CharSequence.class), 1);
        classesVsExpectedCompareValue.forEach((leftAndRightSide, expectedCompareValue) ->
                                                      assertThat(Classes.compareTypeSpecificity(leftAndRightSide._1, leftAndRightSide._2))
                                                              .describedAs("Left: '%s' Right: '%s", leftAndRightSide._1.getName(), leftAndRightSide._2.getName())
                                                              .isEqualTo(expectedCompareValue));
    }

    @Test
    void verify_compareTypeSpecificity_throws_exception_if_types_are_not_in_a_hierarchy() {
        assertThatThrownBy(() -> Classes.compareTypeSpecificity(String.class, Long.class))
                .isInstanceOf(IllegalArgumentException.class);
    }


    private static class LeftAndRightSide extends Pair<Class<?>, Class<?>> {
        /**
         * Construct a new {@link Tuple} with 2 elements
         *
         * @param leftSide  the first element
         * @param rightSide the second element
         */
        public LeftAndRightSide(Class<?> leftSide, Class<?> rightSide) {
            super(leftSide, rightSide);
        }
    }
}
