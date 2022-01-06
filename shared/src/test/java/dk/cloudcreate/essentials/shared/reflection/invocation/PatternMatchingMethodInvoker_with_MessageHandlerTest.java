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
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class PatternMatchingMethodInvoker_with_MessageHandlerTest {
    @Test
    void test_InvokeMostSpecificTypeMatched_on_MessageHandlerWithFallback_using_MessageHandlerPatternMatcher() {
        // Given
        var testSubject              = new MessageHandlerWithFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new MessageHandlerPatternMatcher(),
                                                                        InvocationStrategy.InvokeMostSpecificTypeMatched,
                                                                        Optional.of(noMatchingMethodsHandler));

        // When
        var orderCreatedMessage   = new Message(new OrderCreated("1"));
        var orderAcceptedMessage  = new Message(new OrderAccepted("1"));
        var orderCancelledMessage = new Message(new OrderCancelled("1"));
        patternMatchingInvoker.invoke(orderCreatedMessage);
        patternMatchingInvoker.invoke(orderAcceptedMessage);
        patternMatchingInvoker.invoke(orderCancelledMessage);

        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._1).isSameAs(orderCreatedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._2).isSameAs(orderCreatedMessage);
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")._1).isSameAs(orderCancelledMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")._2).isSameAs(orderCancelledMessage);
        // The fallback method matching on OrderEvent was called for OrderAccepted as it didn't have a matching method
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._1).isSameAs(orderAcceptedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._2).isSameAs(orderAcceptedMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(3);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();
    }

    @Test
    void test_InvokeMostSpecificTypeMatched_on_MessageHandlerWithoutFallback_using_MessageHandlerPatternMatcher() {
        // Given
        var testSubject              = new MessageHandlerWithoutFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new MessageHandlerPatternMatcher(),
                                                                        InvocationStrategy.InvokeMostSpecificTypeMatched,
                                                                        Optional.empty());

        // And
        var orderCreatedMessage   = new Message(new OrderCreated("1"));
        var orderAcceptedMessage  = new Message(new OrderAccepted("1"));
        var orderCancelledMessage = new Message(new OrderCancelled("1"));

        // When
        patternMatchingInvoker.invoke(orderCreatedMessage, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._1).isSameAs(orderCreatedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._2).isSameAs(orderCreatedMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderCancelledMessage, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")._1).isSameAs(orderCancelledMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCancelled")._2).isSameAs(orderCancelledMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        patternMatchingInvoker.invoke(orderAcceptedMessage, noMatchingMethodsHandler);

        // Then
        testSubject.methodCalledWithArgument.clear();
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(0);
        // The fallback method matching on OrderEvent was called for OrderAccepted as it didn't have a matching method
        assertThat(noMatchingMethodsHandler.calledWithArgument).isSameAs(orderAcceptedMessage);
    }

    @Test
    void test_InvokeAllMatches_on_MessageHandlerWithFallback_using_MessageHandlerPatternMatcher() {
        // Given
        var testSubject              = new MessageHandlerWithFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new MessageHandlerPatternMatcher(),
                                                                        InvocationStrategy.InvokeAllMatches,
                                                                        Optional.of(noMatchingMethodsHandler));

        // And
        var orderCreatedMessage  = new Message(new OrderCreated("1"));
        var orderAcceptedMessage = new Message(new OrderAccepted("1"));

        // When
        patternMatchingInvoker.invoke(orderCreatedMessage);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._1).isSameAs(orderCreatedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._2).isSameAs(orderCreatedMessage);
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._1).isSameAs(orderCreatedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._2).isSameAs(orderCreatedMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(2);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderAcceptedMessage);

        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._1).isSameAs(orderAcceptedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderEvent")._2).isSameAs(orderAcceptedMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();
    }

    @Test
    void test_InvokeAllMatches_on_MessageHandlerWithoutFallback_using_MessageHandlerPatternMatcher() {
        // Given
        var testSubject              = new MessageHandlerWithoutFallback();
        var noMatchingMethodsHandler = new TestNoMatchingMethodsHandler();
        var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(testSubject,
                                                                        new MessageHandlerPatternMatcher(),
                                                                        InvocationStrategy.InvokeAllMatches,
                                                                        Optional.empty());

        // And
        var orderCreatedMessage  = new Message(new OrderCreated("1"));
        var orderAcceptedMessage = new Message(new OrderAccepted("1"));

        // When
        patternMatchingInvoker.invoke(orderCreatedMessage, noMatchingMethodsHandler);
        // Then
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._1).isSameAs(orderCreatedMessage.payload);
        assertThat(testSubject.methodCalledWithArgument.get("orderCreated")._2).isSameAs(orderCreatedMessage);
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(1);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isNull();

        // And When
        testSubject.methodCalledWithArgument.clear();
        patternMatchingInvoker.invoke(orderAcceptedMessage, noMatchingMethodsHandler);

        // Then
        assertThat(testSubject.methodCalledWithArgument.size()).isEqualTo(0);
        assertThat(noMatchingMethodsHandler.calledWithArgument).isSameAs(orderAcceptedMessage);
    }


    private static class TestNoMatchingMethodsHandler implements NoMatchingMethodsHandler {
        public Object calledWithArgument;

        @Override
        public void noMatchesFor(Object argument) {
            calledWithArgument = argument;
        }
    }

    private static class MessageHandlerPatternMatcher implements MethodPatternMatcher<OrderEvent> {
        @Override
        public boolean isInvokableMethod(Method candidateMethod) {
            requireNonNull(candidateMethod, "No candidate method supplied");
            return candidateMethod.isAnnotationPresent(MessageHandler.class) &&
                    candidateMethod.getParameterCount() == 2 &&
                    OrderEvent.class.isAssignableFrom(candidateMethod.getParameterTypes()[0]) &&
                    Message.class.equals(candidateMethod.getParameterTypes()[1]);
        }

        @Override
        public Class<?> resolveInvocationArgumentTypeFromMethodDefinition(Method method) {
            requireNonNull(method, "No method supplied");
            return method.getParameterTypes()[0];
        }

        @Override
        public Class<?> resolveInvocationArgumentTypeFromObject(Object argument) {
            requireNonNull(argument, "No argument supplied");
            return ((Message) argument).payload.getClass();
        }

        @Override
        public void invokeMethod(Method methodToInvoke, Object argument, Object invokeMethodOn, Class<?> resolvedInvokeMethodWithArgumentOfType) throws Exception {
            requireNonNull(methodToInvoke, "No methodToInvoke supplied");
            requireNonNull(argument, "No argument supplied");
            requireNonNull(invokeMethodOn, "No invokeMethodOn supplied");
            requireNonNull(resolvedInvokeMethodWithArgumentOfType, "No resolvedInvokeMethodWithArgumentOfType supplied");
            Message message = (Message) argument;
            methodToInvoke.invoke(invokeMethodOn, message.payload, message);
        }
    }
}