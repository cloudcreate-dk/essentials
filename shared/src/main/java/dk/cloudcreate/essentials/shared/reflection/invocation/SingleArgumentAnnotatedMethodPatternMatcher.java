package dk.cloudcreate.essentials.shared.reflection.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * A strategy that matches methods annotated with <code>matchOnMethodsAnnotatedWith</code>
 * and have a single method argument that is the same type or a sub-type of the <code>ARGUMENT_COMMON_ROOT_TYPE</code>
 *
 * @param <ARGUMENT_COMMON_ROOT_TYPE> The method argument common root type (i.e. a common superclass or common interface) for the argument-type that we're performing pattern matching on. <br>
 *                                    If there isn't a common root type, then you can specify {@link Object} instead<p>
 *                                    Example: Within a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
 *                                    In this case <code>OrderEvent</code> will be our <code>ARGUMENT_ROOT_TYPE</code> as it forms the root of the type hierarchy.
 */
public class SingleArgumentAnnotatedMethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> implements MethodPatternMatcher<ARGUMENT_COMMON_ROOT_TYPE> {
    private final Class<? extends Annotation> matchOnMethodsAnnotatedWith;
    private final Class<?>                    argumentCommonRootType;

    /**
     * Create a new strategy that matches methods annotated with <code>matchOnMethodsAnnotatedWith</code>
     * and have a single method argument that is the same type or a sub-type of the <code>ARGUMENT_COMMON_ROOT_TYPE</code>
     *
     * @param matchOnMethodsAnnotatedWith the annotation that invokable methods are annotated with
     * @param argumentCommonRootType      the base type for the single method argument
     */
    public SingleArgumentAnnotatedMethodPatternMatcher(Class<? extends Annotation> matchOnMethodsAnnotatedWith,
                                                       Class<ARGUMENT_COMMON_ROOT_TYPE> argumentCommonRootType) {
        this.matchOnMethodsAnnotatedWith = requireNonNull(matchOnMethodsAnnotatedWith, "You must provide an Annotation that invokable methods are annotated with");
        this.argumentCommonRootType = requireNonNull(argumentCommonRootType, "You must provide an argumentCommonRootType value");
    }

    @Override
    public boolean isInvokableMethod(Method candidateMethod) {
        requireNonNull(candidateMethod, "No candidate method supplied");
        return candidateMethod.isAnnotationPresent(matchOnMethodsAnnotatedWith) &&
                candidateMethod.getParameterCount() == 1 &&
                argumentCommonRootType.isAssignableFrom(candidateMethod.getParameterTypes()[0]);
    }

    @Override
    public Class<?> resolveInvocationArgumentTypeFromMethodDefinition(Method method) {
        requireNonNull(method, "No method supplied");
        return method.getParameterTypes()[0];
    }

    @Override
    public Class<?> resolveInvocationArgumentTypeFromObject(Object argument) {
        requireNonNull(argument, "No argument supplied");
        return argument.getClass();
    }

    @Override
    public void invokeMethod(Method methodToInvoke, Object argument, Object invokeMethodOn, Class<?> resolvedInvokeMethodWithArgumentOfType) throws Exception {
        requireNonNull(methodToInvoke, "No methodToInvoke supplied");
        requireNonNull(argument, "No argument supplied");
        requireNonNull(invokeMethodOn, "No invokeMethodOn supplied");
        requireNonNull(resolvedInvokeMethodWithArgumentOfType, "No resolvedInvokeMethodWithArgumentOfType supplied");
        methodToInvoke.invoke(invokeMethodOn, argument);
    }
}