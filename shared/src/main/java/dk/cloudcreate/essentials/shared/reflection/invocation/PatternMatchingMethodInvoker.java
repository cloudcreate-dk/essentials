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

import dk.cloudcreate.essentials.shared.functional.*;
import dk.cloudcreate.essentials.shared.reflection.*;
import org.slf4j.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.cloudcreate.essentials.shared.Exceptions.sneakyThrow;
import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;
import static dk.cloudcreate.essentials.shared.time.StopWatch.time;

/**
 * The purpose of the {@link PatternMatchingMethodInvoker} is to support pattern matching a set of typed methods against a
 * concrete <code>argument</code> and have the method(s) that match the type resolved by the {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)} will be invoked by the {@link MethodPatternMatcher#invokeMethod(Method, Object, Object, Class)} with the argument<br>
 * <p>
 * Example: Say we have a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
 * In this case <code>OrderEvent</code> will be our <code>ARGUMENT_ROOT_TYPE</code> as it forms the root of the type hierarchy.
 * </p>
 * After deciding on the <code>ARGUMENT_ROOT_TYPE</code> we need to determine how to match the methods.<br>
 * This involves several parts:
 * <b>1. Method Pattern using {@link MethodPatternMatcher#isInvokableMethod(Method)}</b>
 * Example of methods patterns that matches the intention of this class:
 * <ul>
 *     <li><b>Annotation based</b> - where an annotation marks the methods that should be matched:
 *     <pre>
 *      {@literal @EventHandler}
 *       private void some_name_that_we_do_not_care_about(OrderCreated t, ...) { ... }
 *
 *      {@literal @EventHandler}
 *       private void some_name_that_we_do_not_care_about(OrderAccepted t, ...) { ... }
 *     </pre>
 *     </li>
 *     <li><b>Name based</b> - where a specific name, e.g. "<code><u>on</u></code>" in the example below, the methods that should be matched:
 *     <pre>
 *      private void <u>on</u>(OrderCreated t) { ... }
 *
 *      private void <u>on</u>(OrderAccepted t) { ... }
 *     </pre>
 *     </li>
 *     <li>Or some other patterns that you're interested in supporting</li>
 * </ul>
 * <b>2. Resolve the concreted Argument Type the method support accepts using {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromMethodDefinition(Method)}</b>
 * <b>3. Discover all methods matching the <b>argument</b> instance supplied to the {@link #invoke(Object)}/{@link #invoke(Object, NoMatchingMethodsHandler)} methods</b>
 * After we've filtered out and found the methods that match our <b>pattern</b>, next phase is to figure out what methods match a concrete argument instance.<br>
 * Example: Let's say the <code>argument</code> object is an instance of <code>OrderAccepted</code> and we're matching against the following methods
 *     <pre>
 *      {@literal @EventHandler}
 *       private void method1(OrderEvent t) { ... }
 *
 *      {@literal @EventHandler}
 *       private void method2(OrderShipped t) { ... }
 *
 *      {@literal @EventHandler}
 *       private void method3(OrderAccepted t) { ... }
 *     </pre>
 * <p>
 * In this case both <code>method1</code> and <code>method3</code> match on type of the argument instance (<code>OrderAccepted</code>), because <code>OrderEvent</code> (of <code>method1</code>) match by hierarchy,
 * and <code>OrderAccepted</code> (of <code>method3</code>) match directly by type.
 * </p>
 * <b>4. Choose which method(s) to invoke</b>
 * Next step is to check the {@link InvocationStrategy} to determine which method(s) to invoke based on the concrete <b>argument</b> instance supplied to the {@link #invoke(Object)}/{@link #invoke(Object, NoMatchingMethodsHandler)} methods
 * <ul>
 *     <li><b>{@link InvocationStrategy#InvokeMostSpecificTypeMatched}</b> - in which case only <code>method3</code> will be called, since <code>OrderAccepted</code> is a more specific type than the more generic type <code>OrderEvent</code></li>
 *     <li><b>{@link InvocationStrategy#InvokeAllMatches}</b> - in which case BOTH <code>method1</code> and <code>method3</code> will be called in sequence (notice: the order of the methods called is not deterministic)</li>
 * </ul>
 *
 * <b>{@link MethodPatternMatcher}</b>
 * For some methods, it's not necessarily the first argument that decides what argument-type the method supports and the method may need to be called with multiple arguments and not just one.<br>
 * E.g. when handling enveloped types (such as Messages that encapsulate a payload) we may want to match methods against the message' payload type instead of the message type<br>
 * What methods is a candidate for matching, what type a given method supports and how the method is invoked is determined by {@link MethodPatternMatcher}.<br>
 * For more simple cases the {@link SingleArgumentAnnotatedMethodPatternMatcher} provides a good default implementation.
 *
 * <b>Logging</b>
 * Using a setup where the {@link PatternMatchingMethodInvoker} is invoking methods on an instance of <code>dk.cloudcreate.handlers.OrderEventsHandler</code>
 * <pre>{@code
 * var patternMatchingInvoker = new PatternMatchingMethodInvoker<>(new dk.cloudcreate.handlers.OrderEventsHandler(),
 *                                                                 new SingleArgumentAnnotatedMethodPatternMatcher(EventHandler.class,
 *                                                                                                                 OrderEvent.class),
 *                                                                 InvocationStrategy.InvokeMostSpecificTypeMatched,
 *                                                                 Optional.empty());
 * }</pre>
 * and you want {@link PatternMatchingMethodInvoker} logging then you should set the logging level for <code>dk.cloudcreate.handlers.OrderEventsHandler</code> to <code>TRACE</code><br>
 * <br>
 *
 * @param <ARGUMENT_COMMON_ROOT_TYPE> The method argument common root type (i.e. a common superclass or common interface) for the argument-type that we're performing pattern matching on. <br>
 *                                    If there isn't a common root type, then you can specify {@link Object} instead<p>
 *                                    Example: Within a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
 *                                    In this case <code>OrderEvent</code> will be our <code>ARGUMENT_ROOT_TYPE</code> as it forms the root of the type hierarchy.
 * @see SingleArgumentAnnotatedMethodPatternMatcher
 */
public final class PatternMatchingMethodInvoker<ARGUMENT_COMMON_ROOT_TYPE> {
    private final Logger                                          log;
    private final MethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> methodPatternMatcher;
    private final InvocationStrategy                              invocationStrategy;
    private final NoMatchingMethodsHandler                        defaultNoMatchingMethodsHandler;
    /**
     * The object that contains the methods that we will perform pattern matching and invoke methods on
     */
    private final Object                                          invokeMethodsOn;
    /**
     * All the methods and the type of the argument they accept. This is the result of scanning {@link #invokeMethodsOn} using the {@link MethodPatternMatcher}<br>
     * Key: is the <b>method-invocation-argument-type</b>, which is result of {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromMethodDefinition(Method)} on the method that's the value for this key<br>
     * Value: is the method that must be invoked when we get an argument that matches the Key (see {@link MethodPatternMatcher} for the selection of candidate methods)
     */
    private       Map<Class<?>, Method>                           invokableMethods;

    // ------------------ Caches ---------------
    /**
     * Key: withObjectType (as provided as 2nd parameter to {@link #invokeMostSpecificTypeMatched(Object, Class, NoMatchingMethodsHandler)})<br>
     * Value: the MOST specific type that can be used for looking up an invokable method in {@link #invokableMethods}
     */
    private final ConcurrentMap<Class<?>, Class<?>>      invokeMostSpecificTypeMatchCache = new ConcurrentHashMap<>();
    /**
     * Key: withObjectType (as provided as 2nd parameter to {@link #invoke(Object, NoMatchingMethodsHandler)})<br>
     * Value: All the matching method types that can be used for looking up an invokable method in {@link #invokableMethods}
     */
    private final ConcurrentMap<Class<?>, Set<Class<?>>> invokeAllMatchesCache            = new ConcurrentHashMap<>();

    /**
     * Setup a pattern matching method invoker using the default no-op {@link NoMatchingMethodsHandler}
     *
     * @param invokeMethodsOn      The object that contains the methods that we will perform pattern matching and invoke methods on
     * @param methodPatternMatcher The strategy that determines the methods that can be invoked as well as determine which type of argument the
     *                             given method supports and how the method later is going to be invoked
     * @param invocationStrategy   When {@link #invoke(Object)} or {@link #invoke(Object, NoMatchingMethodsHandler)} is called this strategy determines which Methods, among all the methods that match the argument,
     *                             will be invoked
     */
    public PatternMatchingMethodInvoker(Object invokeMethodsOn,
                                        MethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> methodPatternMatcher,
                                        InvocationStrategy invocationStrategy) {
        this(invokeMethodsOn,
             methodPatternMatcher,
             invocationStrategy,
             Optional.empty());
    }

    /**
     * @param invokeMethodsOn                 The object that contains the methods that we will perform pattern matching and invoke methods on
     * @param methodPatternMatcher            The strategy that determines the methods that can be invoked as well as determine which type of argument the
     *                                        given method supports and how the method later is going to be invoked
     * @param invocationStrategy              When {@link #invoke(Object)} or {@link #invoke(Object, NoMatchingMethodsHandler)} is called this strategy determines which Methods, among all the methods that match the argument,
     *                                        will be invoked
     * @param defaultNoMatchingMethodsHandler default consumer that will be called if {@link #invoke(Object)} is called with an argument that doesn't match any methods
     */
    public PatternMatchingMethodInvoker(Object invokeMethodsOn,
                                        MethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> methodPatternMatcher,
                                        InvocationStrategy invocationStrategy,
                                        Optional<NoMatchingMethodsHandler> defaultNoMatchingMethodsHandler) {
        requireNonNull(defaultNoMatchingMethodsHandler, "No defaultNoMatchingMethodsHandler instance provided");
        this.invokeMethodsOn = requireNonNull(invokeMethodsOn, "You must provide an object where methods will be invoked on - aka invokeMethodsOn");
        this.methodPatternMatcher = requireNonNull(methodPatternMatcher, "You must provide a methodPatternMatcher");
        this.invocationStrategy = requireNonNull(invocationStrategy, "You must provide an invocationStrategy");
        this.defaultNoMatchingMethodsHandler = defaultNoMatchingMethodsHandler.orElseGet(() -> argument -> {
        });

        log = LoggerFactory.getLogger(invokeMethodsOn.getClass());
        resolveInvokableMethods();
    }

    private void resolveInvokableMethods() {
        var onClass = invokeMethodsOn.getClass();
        invokableMethods = Methods.methods(onClass)
                                  .stream()
                                  .filter(method -> method.getDeclaringClass() != Object.class)
                                  .filter(methodPatternMatcher::isInvokableMethod)
                                  .collect(Collectors.toMap(methodPatternMatcher::resolveInvocationArgumentTypeFromMethodDefinition, Function.identity()));
        if (log.isTraceEnabled()) {
            log.trace("Invokable Methods on '{}' using method pattern-matcher: {}", onClass, methodPatternMatcher.getClass().getName());
            log.trace("--------------------------------------------------------------------------------------------------");
            invokableMethods.forEach((invokeWithType, method) ->
                                             log.trace("Argument of type '{}' can invoke method: {}", invokeWithType.getName(), method.toGenericString()));
        }
    }

    /**
     * Invoke matching methods based on the <code>argument</code> on the {@link #invokeMethodsOn} based on
     * the {@link MethodPatternMatcher} and {@link InvocationStrategy} using the default <code>defaultNoMatchingMethodsHandler</code>
     * defined in the {@link PatternMatchingMethodInvoker#PatternMatchingMethodInvoker(Object, MethodPatternMatcher, InvocationStrategy, Optional)}
     *
     * @param argument The argument that will be forwarded to the {@link MethodPatternMatcher} for method invocation
     */
    public void invoke(Object argument) {
        invoke(argument, defaultNoMatchingMethodsHandler);
    }

    /**
     * Invoke matching methods based on the <code>argument</code> on the {@link #invokeMethodsOn} based on
     * the {@link MethodPatternMatcher} and {@link InvocationStrategy}
     *
     * @param argument                 The argument that will be forwarded to the {@link MethodPatternMatcher} for method invocation
     * @param noMatchingMethodsHandler A Consumer that is called if NO matching methods are found
     */
    public void invoke(Object argument, NoMatchingMethodsHandler noMatchingMethodsHandler) {
        requireNonNull(argument, "No argument supplied");
        requireNonNull(noMatchingMethodsHandler, "No noMatchingMethodsHandler supplied");

        var resolvedInvokeMethodWithArgumentOfType = methodPatternMatcher.resolveInvocationArgumentTypeFromObject(argument);
        if (resolvedInvokeMethodWithArgumentOfType == null) {
            throw new InvocationException(msg("Didn't methodPatternMatcher.resolveInvocationArgumentTypeFromArgumentInstance returned null for argument with type '{}'", argument.getClass()));
        }

        switch (invocationStrategy) {
            case InvokeMostSpecificTypeMatched:
                invokeMostSpecificTypeMatched(argument, resolvedInvokeMethodWithArgumentOfType, noMatchingMethodsHandler);
                break;
            case InvokeAllMatches:
                invokeAllMatches(argument, resolvedInvokeMethodWithArgumentOfType, noMatchingMethodsHandler);
                break;
            default:
                throw new IllegalStateException(msg("Unsupported invocationStrategy '{}'", invocationStrategy));
        }
    }

    /**
     * Invoke all matching methods based on the <code>resolvedInvokeMethodWithArgumentOfType</code>
     *
     * @param argument                               The argument that we want to forward to the {@link MethodPatternMatcher} for method invocation
     * @param resolvedInvokeMethodWithArgumentOfType The resolved invoke-method-with-argument-of-type (as resolved by {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)})
     * @param noMatchingMethodsHandler               A Consumer that is called if NO matching method is found
     */
    private void invokeAllMatches(Object argument, Class<?> resolvedInvokeMethodWithArgumentOfType, NoMatchingMethodsHandler noMatchingMethodsHandler) {
        requireNonNull(argument, "argument may not be NULL");
        requireNonNull(resolvedInvokeMethodWithArgumentOfType, "resolvedInvokeMethodWithArgumentOfType may not be NULL");
        requireNonNull(noMatchingMethodsHandler, "noMatchingMethodsHandler may not be NULL");
        var allMatchingArgumentTypes = invokeAllMatchesCache.computeIfAbsent(resolvedInvokeMethodWithArgumentOfType,
                                                                             _ignore_ -> invokableMethods.keySet()
                                                                                                         .stream()
                                                                                                         .filter(argumentType -> argumentType.isAssignableFrom(resolvedInvokeMethodWithArgumentOfType))
                                                                                                         .collect(Collectors.toSet()));

        var contextDescription = msg("resolvedInvokeMethodWithArgumentOfType: '{}' and argument-type: '{}'", resolvedInvokeMethodWithArgumentOfType.getName(), argument.getClass().getName());
        if (allMatchingArgumentTypes.isEmpty()) {
            log.trace("invokeAllMatches: Didn't find any matching methods for {}, calling the noMatchingMethodsHandler", contextDescription);
            noMatchingMethodsHandler.noMatchesFor(argument);
        } else {
            log.trace("invokeAllMatches: Found {} matching methods for {}", allMatchingArgumentTypes.size(), contextDescription);
            allMatchingArgumentTypes.forEach(methodInvokableType -> {
                var methodToInvoke = invokableMethods.get(methodInvokableType);
                if (methodToInvoke == null) {
                    throw new IllegalStateException(msg("Expected to find a Method in invokableMethods that matched resolved invokableMethodType: '{}' based on {}", methodInvokableType.getName(), contextDescription));
                }

                log.trace("invokeAllMatches: Invoking method {} based on {}", methodToInvoke.toGenericString(), contextDescription);
                invoke(methodToInvoke, argument, resolvedInvokeMethodWithArgumentOfType);
            });
        }
    }

    /**
     * Invoke THE single method with the most specific type (in a type hierarchy) based on the <code>resolvedInvokeMethodWithArgumentOfType</code>
     *
     * @param argument                               The argument that we want to forward to the {@link MethodPatternMatcher} for method invocation
     * @param resolvedInvokeMethodWithArgumentOfType The resolved withObjectType (as resolved by {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)})
     * @param noMatchingMethodsHandler               A Consumer that is called if NO matching method is found
     */
    private void invokeMostSpecificTypeMatched(Object argument, Class<?> resolvedInvokeMethodWithArgumentOfType, NoMatchingMethodsHandler noMatchingMethodsHandler) {
        requireNonNull(argument, "argument may not be NULL");
        requireNonNull(resolvedInvokeMethodWithArgumentOfType, "resolvedInvokeMethodWithArgumentOfType may not be NULL");
        requireNonNull(noMatchingMethodsHandler, "noMatchingMethodsHandler may not be NULL");

        var mostSpecificMatchingArgumentType = invokeMostSpecificTypeMatchCache.computeIfAbsent(resolvedInvokeMethodWithArgumentOfType, _ignore_ ->
                invokableMethods.keySet()
                                .stream()
                                .filter(cls -> cls.isAssignableFrom(resolvedInvokeMethodWithArgumentOfType))
                                .max(Classes::compareTypeSpecificity)
                                .orElse(null));

        var contextDescription = msg("resolvedInvokeMethodWithArgumentOfType: '{}' and argument-type: '{}'", resolvedInvokeMethodWithArgumentOfType.getName(), argument.getClass().getName());
        if (mostSpecificMatchingArgumentType != null) {
            var methodToInvoke = invokableMethods.get(mostSpecificMatchingArgumentType);
            if (methodToInvoke == null) {
                throw new InvocationException(msg("Expected to find a Method that matched resolved mostSpecificMatchingArgumentType: '{}' based on {}", mostSpecificMatchingArgumentType.getName(), contextDescription));
            }

            log.trace("invokeMostSpecificTypeMatched: Resolved mostSpecificMatchingArgumentType '{}' and methodToInvoke: {} based on {}", mostSpecificMatchingArgumentType.getName(), methodToInvoke.toGenericString(), contextDescription);
            invoke(methodToInvoke, argument, resolvedInvokeMethodWithArgumentOfType);
        } else {
            log.trace("invokeMostSpecificTypeMatched: Didn't find a matching method for {}, calling the noMatchingMethodsHandler", contextDescription);
            noMatchingMethodsHandler.noMatchesFor(argument);
        }
    }

    /**
     * Delegate the invoke method to the {@link MethodPatternMatcher}
     *
     * @param methodToInvoke                         the method that must be reflectively invoked. You can assume that the method is Acessible already
     * @param argument                               The argument instance passed to {@link PatternMatchingMethodInvoker#invoke(Object)}/{@link PatternMatchingMethodInvoker#invoke(Object, NoMatchingMethodsHandler)}
     *                                               and which was used in the call to {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)}<br>
     *                                               In simple cases this will be the same type as the <code>argument</code>'s, but for enveloped types
     *                                               (such as Messages that encapsulate a payload) we may want to match methods against
     *                                               the message' payload type instead of the message type.
     * @param resolvedInvokeMethodWithArgumentOfType The resolved withObjectType (as resolved by {@link MethodPatternMatcher#resolveInvocationArgumentTypeFromObject(Object)})
     */
    private void invoke(Method methodToInvoke, Object argument, Class<?> resolvedInvokeMethodWithArgumentOfType) {
        requireNonNull(methodToInvoke, "No methodToInvoke supplied");
        requireNonNull(argument, "No argument supplied");

        var contextDescription = msg("method '{}' argument '{}' on '{}'", methodToInvoke.toGenericString(), argument, invokeMethodsOn);

        try {
            log.trace("Invoking {}", contextDescription);
            var duration = time(CheckedRunnable.safe(() -> methodPatternMatcher.invokeMethod(methodToInvoke, argument, invokeMethodsOn, resolvedInvokeMethodWithArgumentOfType)));
            log.trace("Took {} ms to invoke {}", duration.toMillis(), contextDescription);
        } catch (CheckedExceptionRethrownException e) {
            log.debug(msg("Failed to invoke {}", contextDescription), e.getCause());
            // Unwrap the real cause and rethrow this exception
            sneakyThrow(e.getCause());
        } catch (RuntimeException e) {
            log.debug(msg("Failed to invoke {}", contextDescription), e);
            throw e;
        } catch (Throwable e) {
            log.debug(msg("Failed to invoke {}", contextDescription), e);
            sneakyThrow(e);
        }
    }
}