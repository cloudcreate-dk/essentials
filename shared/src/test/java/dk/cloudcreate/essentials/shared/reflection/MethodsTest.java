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

import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MethodsTest {
    @Test
    void BaseInterface_methods() {
        // When
        var methods = Methods.methods(BaseInterface.class);

        // Then
        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod",
                                           CharSequence.class,
                                           String.class, long.class
                                          )).isPresent();

        assertThat(
                Methods.findMatchingMethod(methods, "otherBaseInterfaceMethod",
                                           Optional.class
                                          )).isPresent();

        var match = Methods.findMatchingMethod(methods, "defaultMethod",
                                               String.class,
                                               String.class
                                              );
        assertThat(match).isPresent();
        assertThat(match.get().isDefault()).isTrue();

        assertThat(methods.size()).isEqualTo(3);
        assertThat(methods.stream().filter(Accessibles::isAccessible).count()).isEqualTo(3);
    }

    @Test
    void TestInterface_methods() {
        // When
        var methods = Methods.methods(TestInterface.class);

        // Then
        // Overridden method "baseInterfaceMethod(String, long)" from BaseInterface with covariant return type String instead of CharSequence
        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod",
                                           String.class,
                                           String.class, long.class
                                          )).isPresent();

        assertThat(
                Methods.findMatchingMethod(methods, "otherBaseInterfaceMethod",
                                           Optional.class
                                          )).isPresent();

        var match = Methods.findMatchingMethod(methods, "defaultMethod",
                                               String.class,
                                               String.class
                                              );
        assertThat(match).isPresent();
        assertThat(match.get().isDefault()).isTrue();

        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod2",
                                           String.class,
                                           String.class, long.class, boolean.class
                                          )).isPresent();

        assertThat(methods.size()).isEqualTo(4);
        assertThat(methods.stream().filter(Accessibles::isAccessible).count()).isEqualTo(4);
    }

    @Test
    void BaseTestReflectionClass_methods() {
        // When
        var methods = Methods.methods(BaseTestReflectionClass.class).stream().filter(method -> !method.getDeclaringClass().equals(Object.class)).collect(Collectors.toSet());

        // Then
        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod",
                                           CharSequence.class,
                                           String.class, long.class
                                          )).isPresent();

        assertThat(
                Methods.findMatchingMethod(methods, "otherBaseInterfaceMethod",
                                           Optional.class
                                          )).isPresent();

        var match = Methods.findMatchingMethod(methods, "defaultMethod",
                                               String.class,
                                               String.class
                                              );
        assertThat(match).isPresent();
        assertThat(match.get().isDefault()).isTrue();

        match = Methods.findMatchingMethod(methods, "baseTestClassMethod",
                                           Long.class,
                                           String[].class
                                          );
        assertThat(match).isPresent();
        assertThat(match.get().isVarArgs()).isTrue();

        match = Methods.findMatchingMethod(methods, "abstractBaseClassMethod",
                                           CharSequence.class
                                          );
        assertThat(match).isPresent();
        assertThat(Modifier.isAbstract(match.get().getModifiers())).isTrue();

        assertThat(methods.size()).isEqualTo(5);
        assertThat(methods.stream().filter(Accessibles::isAccessible).count()).isEqualTo(5);
    }

    @Test
    void TestReflectionClass_methods() {
        // When
        var methods = Methods.methods(TestReflectionClass.class).stream().filter(method -> !method.getDeclaringClass().equals(Object.class)).collect(Collectors.toSet());

        // Then
        // TestReflectionClass applies covariant return type String instead of CharSequence
        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod",
                                           String.class,
                                           String.class, long.class
                                          )).isPresent();

        assertThat(
                Methods.findMatchingMethod(methods, "otherBaseInterfaceMethod",
                                           Optional.class
                                          )).isPresent();

        var match = Methods.findMatchingMethod(methods, "defaultMethod",
                                               String.class,
                                               String.class
                                              );
        assertThat(match).isPresent();
        assertThat(match.get().isDefault()).isTrue();

        assertThat(
                Methods.findMatchingMethod(methods, "baseInterfaceMethod2",
                                           String.class,
                                           String.class, long.class, boolean.class
                                          )).isPresent();

        match = Methods.findMatchingMethod(methods, "baseTestClassMethod",
                                           Long.class,
                                           String[].class
                                          );
        assertThat(match).isPresent();
        assertThat(match.get().isVarArgs()).isTrue();

        // TestReflectionClass applies covariant return type String instead of CharSequence
        match = Methods.findMatchingMethod(methods, "abstractBaseClassMethod",
                                           String.class
                                          );
        assertThat(match).isPresent();
        assertThat(Modifier.isAbstract(match.get().getModifiers())).isFalse();

        assertThat(methods.size()).isEqualTo(8);
        assertThat(methods.stream().filter(Accessibles::isAccessible).count()).isEqualTo(8);
    }
}