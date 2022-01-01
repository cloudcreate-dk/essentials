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

class InterfacesTest {

    @Test
    void test_interfaces() {
        Map<Class<? extends BaseInterface>, Set<? extends Class<?>>> typeVsImplementedInterfaces = Map.of(BaseInterface.class, Set.of(),
                                                                                                          TestInterface.class, Set.of(BaseInterface.class),
                                                                                                          BaseTestReflectionClass.class, Set.of(BaseInterface.class),
                                                                                                          TestReflectionClass.class, Set.of(BaseInterface.class, TestInterface.class));

        typeVsImplementedInterfaces.forEach((type, implementedInterfaces) -> {
            var resolvedInterfaces = Interfaces.interfaces(type);
            assertThat(resolvedInterfaces.size())
                    .describedAs("Type '%s'", type.getName())
                    .isEqualTo(implementedInterfaces.size());
            assertThat(resolvedInterfaces.containsAll(implementedInterfaces))
                    .describedAs("Type '%s'", type.getName())
                    .isTrue();
        });
    }
}