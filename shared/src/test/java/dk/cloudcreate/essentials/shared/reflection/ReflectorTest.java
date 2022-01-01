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

import static org.assertj.core.api.Assertions.assertThat;

class ReflectorTest {
    @Test
    void test_reflectOn() {
        var reflector = Reflector.reflectOn(TestSubject.class.getName());

        assertThat(reflector.type).isEqualTo(TestSubject.class);
        assertThat(reflector.type()).isEqualTo(TestSubject.class);
        assertThat(reflector.constructors.size()).isEqualTo(2);
        assertThat(reflector.methods.size()).isEqualTo(4 + Reflector.reflectOn(Object.class).methods.size());
        assertThat(reflector.fields.size()).isEqualTo(3);
    }

    @Test
    void test_reflectOn_has_caching() {
        var reflector  = Reflector.reflectOn(TestSubject.class.getName());
        var reflector2 = Reflector.reflectOn(TestSubject.class);

        assertThat(reflector2).isSameAs(reflector);
    }

    @Test
    void test_hasMatchingConstructor() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        // Default constructor
        assertThat(reflector.hasMatchingConstructorBasedOnArguments()).isTrue();
        assertThat(reflector.hasMatchingConstructorBasedOnParameterTypes()).isTrue();
        assertThat(reflector.hasMatchingConstructorBasedOnArguments("Some string")).isTrue();
        assertThat(reflector.hasMatchingConstructorBasedOnParameterTypes(String.class)).isTrue();
    }

    @Test
    void test_newInstance_defaultConstructor() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance();
        assertThat(instance).isNotNull();
        assertThat(instance.getInstanceVariable()).isEqualTo("default");
    }

    @Test
    void test_newInstance_singleArgumentConstructor() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance("Hello World");
        assertThat(instance).isNotNull();
        assertThat(instance.getInstanceVariable()).isEqualTo("Hello World");
    }

    @Test
    void test_invokeMethod() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance("Hello World");
        assertThat(instance).isNotNull();

        assertThat(reflector.hasMethod("getInstanceVariable", false)).isTrue();
        var setInstanceVariable = reflector.findMatchingMethod("setInstanceVariable", false, String.class);
        assertThat(setInstanceVariable).isPresent();

        // Invoke Get
        String result = reflector.invoke("getInstanceVariable", instance);
        assertThat(result).isEqualTo("Hello World");

        // Invoke Set
        reflector.invoke(setInstanceVariable.get(), instance, "It works");
        assertThat(instance.instanceVariable).isEqualTo("It works");
    }

    @Test
    void test_invokeStaticMethod() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance("Hello World");
        assertThat(instance).isNotNull();

        assertThat(reflector.hasMethod("setStaticVariable", true, String.class)).isTrue();
        var getStaticVariable = reflector.findMatchingMethod("getStaticVariable", true);
        assertThat(getStaticVariable).isPresent();

        // Invoke Get
        String result = reflector.invokeStatic(getStaticVariable.get());
        assertThat(result).isEqualTo(TestSubject.STATIC_VARIABLE);

        // Invoke Set
        reflector.invokeStatic("setStaticVariable", "It works too");
        assertThat(TestSubject.STATIC_VARIABLE).isEqualTo("It works too");
    }

    @Test
    void test_get_set_field() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance("Hello World");
        assertThat(instance).isNotNull();

        // Field exists
        var field = reflector.findFieldByName("instanceVariable");
        assertThat(field).isPresent();

        // Set
        reflector.set(instance,
                      field.get(),
                      "New value");

        // Get
        assertThat((String) reflector.get(instance, "instanceVariable")).isEqualTo("New value");
    }

    @Test
    void test_get_set_static_field() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        TestSubject instance = reflector.newInstance("Hello World");
        assertThat(instance).isNotNull();

        // Static field exists
        var field = reflector.findStaticFieldByName("STATIC_VARIABLE");
        assertThat(field).isPresent();

        // Set
        reflector.setStatic("STATIC_VARIABLE",
                            "New static value!");

        // Get
        assertThat((String) reflector.getStatic(field.get())).isEqualTo("New static value!");
    }

    @Test
    void test_findFieldByAnnotation() {
        var reflector = Reflector.reflectOn(TestSubject.class);

        // Match test
        var match = reflector.findFieldByAnnotation(Deprecated.class);
        assertThat(match).isPresent();
        assertThat(match.get().getName()).isEqualTo("annotationMatchingField");

        // No match test
        match = reflector.findFieldByAnnotation(Test.class);
        assertThat(match).isEmpty();
    }

    private static class TestSubject {
        private static String STATIC_VARIABLE         = "DEFAULT";
        private        String instanceVariable        = "default";
        @Deprecated
        private        String annotationMatchingField = "test";

        public TestSubject() {
        }

        public TestSubject(String instanceVariable) {
            this.instanceVariable = instanceVariable;
        }

        public String getInstanceVariable() {
            return instanceVariable;
        }

        public void setInstanceVariable(String instanceVariable) {
            this.instanceVariable = instanceVariable;
        }

        public static String getStaticVariable() {
            return STATIC_VARIABLE;
        }

        public static void setStaticVariable(String staticVariable) {
            STATIC_VARIABLE = staticVariable;
        }
    }
}