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

package dk.cloudcreate.essentials.shared.reflection.invocation;

import dk.cloudcreate.essentials.shared.reflection.invocation.test_subjects.*;
import dk.cloudcreate.essentials.shared.types.GenericType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PatternMatchingMethodInvokerTest {
    @Test
    void test_InvokeMostSpecificTypeMatched_on_OrderEventHandlerWithFallback_using_SingleArgumentAnnotatedMethodPatternMatcher() {
        // Given
        var testSubject              = new OrderEventHandlerWithFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new SingleArgumentAnnotatedMethodPatternMatcher<>(EventHandler.class,
                                                                                                                          OrderEvent.class),
                                                                        InvocationStrategy.InvokeMostSpecificTypeMatched,
                                                                        Optional.of(noMatchingMethodsHandler));

        // When
        var orderCreated   = new OrderCreated("1");
        var orderAccepted  = new OrderAccepted("1");
        var orderCancelled = new OrderCancelled("1");
        patternMatchingInvoker.invoke(orderCreated);
        patternMatchingInvoker.invoke(orderAccepted);
        patternMatchingInvoker.invoke(orderCancelled);

        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")).isSameAs(orderCreated);
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")).isSameAs(orderCancelled);
        // The fallback method matching on OrderEvent was called for OrderAccepted as it didn't have a matching method
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")).isSameAs(orderAccepted);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(3);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();
    }

    @Test
    void test_InvokeMostSpecificTypeMatched_on_OrderEventHandlerWithoutFallback_using_SingleArgumentAnnotatedMethodPatternMatcher() {
        // Given
        var testSubject              = new OrderEventHandlerWithoutFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new SingleArgumentAnnotatedMethodPatternMatcher<>(EventHandler.class,
                                                                                                                          new GenericType<OrderEvent>() {}),
                                                                        InvocationStrategy.InvokeMostSpecificTypeMatched,
                                                                        Optional.empty());

        // And
        var orderCreated   = new OrderCreated("1");
        var orderAccepted  = new OrderAccepted("1");
        var orderCancelled = new OrderCancelled("1");

        // When
        patternMatchingInvoker.invoke(orderCreated, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")).isSameAs(orderCreated);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderCancelled, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")).isSameAs(orderCancelled);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        patternMatchingInvoker.invoke(orderAccepted, noMatchingMethodsHandler);

        // Then
        testSubject.methodCalledWithArgument.clear();
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(0);
        // The fallback method matching on OrderEvent was called for OrderAccepted as it didn't have a matching method
        assertThat(noMatchingMethodsHandler.calledWithArgument).isSameAs(orderAccepted);
    }

    @Test
    void test_InvokeAllMatches_on_OrderEventHandlerWithFallback_using_SingleArgumentAnnotatedMethodPatternMatcher() {
        // Given
        var testSubject              = new OrderEventHandlerWithFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new SingleArgumentAnnotatedMethodPatternMatcher<>(EventHandler.class,
                                                                                                                          OrderEvent.class),
                                                                        InvocationStrategy.InvokeAllMatches,
                                                                        Optional.of(noMatchingMethodsHandler));

        // And
        var orderCreated  = new OrderCreated("1");
        var orderAccepted = new OrderAccepted("1");

        // When
        patternMatchingInvoker.invoke(orderCreated);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")).isSameAs(orderCreated);
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")).isSameAs(orderCreated);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(2);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderAccepted);

        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")).isSameAs(orderAccepted);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();
    }

    @Test
    void test_InvokeAllMatches_on_OrderEventHandlerWithoutFallback_using_SingleArgumentAnnotatedMethodPatternMatcher() {
        // Given
        var testSubject              = new OrderEventHandlerWithoutFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new SingleArgumentAnnotatedMethodPatternMatcher<>(EventHandler.class,
                                                                                                                          OrderEvent.class),
                                                                        InvocationStrategy.InvokeAllMatches,
                                                                        Optional.empty());

        // And
        var orderCreated  = new OrderCreated("1");
        var orderAccepted = new OrderAccepted("1");

        // When
        patternMatchingInvoker.invoke(orderCreated, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")).isSameAs(orderCreated);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderAccepted, noMatchingMethodsHandler);

        // Then
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(0);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isSameAs(orderAccepted);
    }


    private static class TestNoMatchingMethodsHandler implements NoMatchingMethodsHandler {
        public Object calledWithArgument;

        @Override
        public void noMatchesFor(Object argument) {
            calledWithArgument = argument;
        }
    }
}