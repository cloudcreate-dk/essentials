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
    void test_resolveGenericType_with_Class_as_argument() {
        var genericType = new GenericType<String>(){};
        var actualTypeOption      = GenericType.resolveGenericTypeOnSuperClass(genericType.getClass(), 0);
        assertThat(actualTypeOption).isNotNull();
        assertThat(actualTypeOption).isEqualTo(String.class);
    }

    @Test
    void test_using_a_ParameterizedType_as_argument() {
        var genericType = new GenericType<TestSubject<String>>(){};
        assertThat(genericType.getType()).isEqualTo(TestSubject.class);
        assertThat(genericType.getGenericType()).isInstanceOf(ParameterizedType.class);
        assertThat(((ParameterizedType)genericType.getGenericType()).getRawType()).isEqualTo(TestSubject.class);
    }

    @Test
    void test_resolveGenericTypeOnSuperClass_with_ParameterizedType_as_argument() {
        var genericType = new GenericType<TestSubject<String>>(){};
        var actualTypeOption      = GenericType.resolveGenericTypeOnSuperClass(genericType.getClass(), 0);
        assertThat(actualTypeOption).isNotNull();
        assertThat(actualTypeOption).isEqualTo(TestSubject.class);
    }

    @Test
    void test_resolveGenericTypeForInterface() {
        var actualTypeWithIndex0 = GenericType.resolveGenericTypeForInterface(Order.class,
                                                                    WithState.class,
                                                                    0);
        var actualTypeWithIndex1 = GenericType.resolveGenericTypeForInterface(Order.class,
                                                                    WithState.class,
                                                                    1);
        var actualTypeWithIndex2 = GenericType.resolveGenericTypeForInterface(Order.class,
                                                                    WithState.class,
                                                                    2);
        assertThat(actualTypeWithIndex0).isEqualTo(String.class);
        assertThat(actualTypeWithIndex1).isEqualTo(Order.class);
        assertThat(actualTypeWithIndex2).isEqualTo(OrderState.class);
    }

    // --------------- Abstract classes that may optionally implement WithState -------------
    public static abstract class AggregateRoot<ID, AGGREGATE_TYPE extends AggregateRoot<ID, AGGREGATE_TYPE>> {

    }

    public static abstract class AggregateState<ID, AGGREGATE_TYPE extends AggregateRoot<ID, AGGREGATE_TYPE>> {

    }

    public interface WithState<ID, AGGREGATE_TYPE extends AggregateRoot<ID, AGGREGATE_TYPE>, AGGREGATE_STATE extends AggregateState<ID, AGGREGATE_TYPE>> {
    }

    // ---------------- Concrete classes ------------
    public static class Order extends AggregateRoot<String, Order> implements WithState<String, Order, OrderState> {

    }

    public static class OrderState extends AggregateState<String, Order> {

    }

}