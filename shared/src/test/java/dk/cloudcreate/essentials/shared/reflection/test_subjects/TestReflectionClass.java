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

package dk.cloudcreate.essentials.shared.reflection.test_subjects;

import java.util.Optional;

public class TestReflectionClass extends BaseTestReflectionClass implements TestInterface {
    private Double baseField;
    private String additionalField;

    private TestReflectionClass() {
        super("Test");
    }

    public TestReflectionClass(String baseField, boolean other) {
        super(baseField);
    }

    private String noArgumentMethod() {
        return "Test-noArgumentMethod";
    }

    protected void noReturnValueMethod(String anArgument) {
        throw new RuntimeException(anArgument);
    }

    @Override
    protected String abstractBaseClassMethod() {
        return "Test-abstractBaseClassMethod";
    }

    @Override
    public String baseInterfaceMethod(String argument1, long argument2) {
        return argument1 + argument2;
    }

    @Override
    public String baseInterfaceMethod2(String argument1, long argument2, boolean argument3) {
        return argument1 + argument2 + argument3;
    }

    @Override
    public Optional<String> otherBaseInterfaceMethod() {
        return Optional.of("Test-otherBaseInterfaceMethod");
    }
}
