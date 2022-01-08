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

package dk.cloudcreate.essentials.shared.types;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

import static org.assertj.core.api.Assertions.assertThat;


class GenericTypeTest {
    @Test
    void test_using_a_Class_as_argument() {
        var genericType = new GenericType<String>(){};
        assertThat(genericType.getType()).isEqualTo(String.class);
        assertThat(genericType.getGenericType()).isEqualTo(String.class);
    }

    @Test
    void test_using_a_ParameterizedType_as_argument() {
        var genericType = new GenericType<TestSubject<String>>(){};
        assertThat(genericType.getType()).isEqualTo(TestSubject.class);
        assertThat(genericType.getGenericType()).isInstanceOf(ParameterizedType.class);
        assertThat(((ParameterizedType)genericType.getGenericType()).getRawType()).isEqualTo(TestSubject.class);
    }
}