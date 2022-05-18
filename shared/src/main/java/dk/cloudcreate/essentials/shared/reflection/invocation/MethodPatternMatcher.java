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

import java.lang.reflect.Method;

/**
 * Strategy interface that determines the mechanics of which methods are candidates for matching,
 * what type a given method supports and how the method is invoked
 *
 * @param <ARGUMENT_COMMON_ROOT_TYPE> The method argument common root type (i.e. a common superclass or common interface) for the argument-type that we're performing pattern matching on.<br>
 *                                    If there isn't a common root type, then you can specify {@link Object} instead<p>
 *                                    Example: Within a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
 *                                    In this case <code>OrderEvent</code> will be our <code>ARGUMENT_ROOT_TYPE</code> as it forms the root of the type hierarchy.
 */
public interface MethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> {
    /**
     * Determines if a candidateMethod is a candidate for being invoked by {@link PatternMatchingMethodInvoker}
     *
     * @param candidateMethod the candidate method to test for invocation
     * @return true if the candidateMethod is invokable, otherwise false
     */
    boolean isInvokableMethod(Method candidateMethod);

    /**
     * Returns the (single) type this method can be invoked with. This method MUST return the type supported OR throw a {@link RuntimeException}
     * in case the method doesn't allow resolving a type
     *
     * @param method the method to resolve the invokeWithObject type (requirement the method has passed the {@link #isInvokableMethod(Method)} test)
     * @return The type supported
     */
    Class<?> resolveInvocationArgumentTypeFromMethodDefinition(Method method);

    /**
     * Resolves which type should be used for looking up matching invokable methods<br>
     * In simple cases this will be the same type as the <code>argument</code>'s, but for enveloped types
     * (such as Messages that encapsulate a payload) we may want to match methods against
     * the message' payload type instead of the message type
     *
     * @param argument the argument instance
     * @return the type that should be used for looking up matching invokable methods
     */
    Class<?> resolveInvocationArgumentTypeFromObject(Object argument);

    /**
     * This method is responsible for calling the <code>methodToInvoke</code> on the <code>invokeMethodOnObject</code> object using the
     * right parameters based on the <code>withObject</code>.<br>
     * For simple cases the argument will only be the <code>withObject</code>, where it for other cases may involve extracting the real object
     * to pass onto the <code>methodToInvoke</code> from within the <code>withObject</code> (e.g. in case of a wrapped type, such as a message object that contains
     * the real payload to supply to the <code>methodToInvoke</code>)
     *
     * @param methodToInvoke                         the method that must be reflectively invoked. You can assume that the method is Acessible already
     * @param argument                               The argument instance passed to {@link PatternMatchingMethodInvoker#invoke(Object)}/{@link PatternMatchingMethodInvoker#invoke(Object, NoMatchingMethodsHandler)}
     *                                               and which was used in the call to {@link #resolveInvocationArgumentTypeFromObject(Object)}<br>
     *                                               In simple cases this will be the same type as the <code>argument</code>'s, but for enveloped types
     *                                               (such as Messages that encapsulate a payload) we may want to match methods against
     *                                               the message' payload type instead of the message type.
     * @param invokeMethodOn                         the object that the <code>methodToInvoke</code> must be invoked on (the target object of the {@link Method#invoke(Object, Object...)})
     * @param resolvedInvokeMethodWithArgumentOfType The resolved withObjectType (as resolved by {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)})
     * @throws Exception Feel free to throw any Reflection exceptions, etc.<br>
     *                   The {@link PatternMatchingMethodInvoker} will ensure to either unwrap the real cause or wrap any checked exceptions in a Runtime exception.
     */
    void invokeMethod(Method methodToInvoke, Object argument, Object invokeMethodOn, Class<?> resolvedInvokeMethodWithArgumentOfType) throws Exception;
}
