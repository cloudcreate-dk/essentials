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

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;
import static dk.cloudcreate.essentials.shared.reflection.Parameters.*;

/**
 * Caching Java Reflection helper<br>
 * Encapsulates {@link Constructors}, {@link Methods} and {@link Fields} while providing high-level method for invoking constructors, method and getting/setting fields<br>
 * <br>
 * Depending on the version of Java you're using you may need to explicitly add something like the following to your Module Config or Java arguments to ensure
 * that {@link Reflector} is allowed to perform Java reflection<br>
 * See https://docs.oracle.com/en/java/javase/17/migrate/migrating-jdk-8-later-jdk-releases.html#GUID-12F945EB-71D6-46AF-8C3D-D354FD0B1781 for more information and to understand
 * the implications of adding the <code>--add-opens</code> argument.
 *
 * @see Interfaces
 */
public class Reflector {
    /**
     * Cache of Reflect instances
     */
    private static final ConcurrentMap<Class<?>, Reflector> REFLECTOR_CACHE = new ConcurrentHashMap<>();

    /**
     * The type wrapped by this {@link Reflector} instance
     */
    public final Class<?>             type;
    /**
     * All the constructors defined in {@link #type} - see {@link Constructors#constructors(Class)}
     */
    public final List<Constructor<?>> constructors;
    /**
     * All the methods defined in {@link #type} - see {@link Methods#methods(Class)}
     */
    public final Set<Method>          methods;
    /**
     * All the fields defined in {@link #type} - see {@link Fields#fields(Class)}
     */
    public final Set<Field>           fields;

    /**
     * Create a {@link Reflector} instance for the class with <code>fullyQualifiedClassName</code>
     *
     * @param fullyQualifiedClassName the fully qualified class name for the type we want to reflect on
     * @return the {@link Reflector} instance
     */
    public static Reflector reflectOn(String fullyQualifiedClassName) {
        requireNonNull(fullyQualifiedClassName, "You must supply a fullyQualifiedClassName");
        return reflectOn(Classes.forName(fullyQualifiedClassName));
    }

    /**
     * Create a {@link Reflector} instance for the <code>type</code>
     *
     * @param type the type we want to reflect on
     * @return the {@link Reflector} instance
     */
    @SuppressWarnings("Convert2MethodRef")
    public static Reflector reflectOn(Class<?> type) {
        requireNonNull(type, "You must supply a type");
        return REFLECTOR_CACHE.computeIfAbsent(type, _type -> new Reflector(_type));
    }

    private Reflector(Class<?> type) {
        this.type = type;
        this.constructors = Constructors.constructors(type);
        this.methods = Methods.methods(type);
        this.fields = Fields.fields(type);
    }

    /**
     * Does the type have a default no-arguments constructor?
     *
     * @return true if the type has a default no-arguments constructor, otherwise false
     */
    public boolean hasDefaultConstructor() {
        return this.constructors.stream().filter(_constructor -> _constructor.getParameterCount() == 0).count() == 1;
    }

    /**
     * Perform constructor matching based on actual argument values (as opposed to {@link #hasMatchingConstructorBasedOnParameterTypes(Class[])})<br>
     * The constructor match doesn't require exact type matches only compatible type matches
     *
     * @param constructorArguments the actual argument values
     * @return true if there's a single matching constructor, otherwise false
     */
    public boolean hasMatchingConstructorBasedOnArguments(Object... constructorArguments) {
        var actualArgumentTypes = argumentTypes(constructorArguments);

        return this.constructors.stream().filter(_constructor ->
                                                         parameterTypesMatches(actualArgumentTypes, _constructor.getParameterTypes(), false)
                                                ).count() == 1;
    }

    /**
     * Perform constructor matching based on exact parameter types (as opposed to {@link #hasMatchingConstructorBasedOnArguments(Object...)})
     *
     * @param parameterTypes the parameter types
     * @return true if there's a single matching constructor
     */
    public boolean hasMatchingConstructorBasedOnParameterTypes(Class<?>... parameterTypes) {
        return this.constructors.stream().filter(_constructor ->
                                                         parameterTypesMatches(parameterTypes, _constructor.getParameterTypes(), true)
                                                ).count() == 1;
    }

    /**
     * Create a new instance of the {@link #type()} using a constructor that matches the <code>constructorArguments</code><br>
     * The constructor matching uses the {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)} without requiring an exact parameter type match
     *
     * @param constructorArguments the arguments for the constructor
     * @param <T>                  the type of the object returned
     * @return the newly created instance
     * @throws ReflectionException in case something goes wrong
     */
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Object... constructorArguments) {
        var actualArgumentTypes = argumentTypes(constructorArguments);

        var constructor = this.constructors.stream()
                                           .filter(_constructor -> parameterTypesMatches(actualArgumentTypes, _constructor.getParameterTypes(), false))
                                           .findFirst()
                                           .orElseThrow(() -> new ReflectionException(msg("Couldn't find a single constructor that matched {}", Arrays.toString(actualArgumentTypes))));

        try {
            return (T) constructor.newInstance(constructorArguments);
        } catch (Exception e) {
            throw new ReflectionException(
                    msg("Failed to create a new instance of constructor {} using arguments of type {}", constructor, actualArgumentTypes),
                    e
            );
        }
    }

    /**
     * The type wrapped by this {@link Reflector} instance
     *
     * @return The type wrapped by this {@link Reflector} instance
     */
    public Class<?> type() {
        return type;
    }

    /**
     * Does the {@link #type} wrapped by this {@link Reflector} instance have a method that matches on:
     * <ul>
     *     <li>Method Name</li>
     *     <li>Being an Instance or a Static method</li>
     *     <li>Method parameters that match the <code>argumentTypes</code><br>
     *     The parameters matching uses the {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)} without requiring an exact parameter type match</li>
     * </ul>
     *
     * @param methodName    the name of the method
     * @param staticMethod  true if the matching method MUST be a static method, otherwise it will only search instance methods
     * @param argumentTypes the argument types that the method must support
     * @return true if the {@link #type} has a matching method, otherwise false
     * @see #findMatchingMethod(String, boolean, Class[])
     */
    public boolean hasMethod(String methodName, boolean staticMethod, Class<?>... argumentTypes) {
        return findMatchingMethod(methodName, staticMethod, argumentTypes).isPresent();
    }

    /**
     * Find the first method in the {@link #type} wrapped by this {@link Reflector} instance that matches on:
     * <ul>
     *     <li>Method Name</li>
     *     <li>Being an Instance or a Static method</li>
     *     <li>Method parameters that match the <code>argumentTypes</code><br>
     *     The parameters matching uses the {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)} without requiring an exact parameter type match</li>
     * </ul>
     *
     * @param methodName    the name of the method
     * @param staticMethod  true if the matching method MUST be a static method, otherwise it will only search instance methods
     * @param argumentTypes the argument types that the method must support
     * @return an {@link Optional} with the matching method or {@link Optional#empty()} if no matching could be made
     * @throws TooManyMatchingMethodsFoundException in case there are more than 1 method that matches on the argument type
     */
    public Optional<Method> findMatchingMethod(String methodName, boolean staticMethod, Class<?>... argumentTypes) {
        requireNonNull(methodName, "You must supply a methodName");
        var matchingMethods = this.methods.stream().filter(method -> method.getName().equals(methodName) &&
                                          (staticMethod == Modifier.isStatic(method.getModifiers())) &&
                                          parameterTypesMatches(argumentTypes, method.getParameterTypes(), false))
                                          .collect(Collectors.toList());
        if (matchingMethods.isEmpty()) {
            return Optional.empty();
        } else if (matchingMethods.size() == 1) {
            return Optional.of(matchingMethods.get(0));
        }
        throw new TooManyMatchingMethodsFoundException(msg("Found {} {} methods within {} matching on name '{}' and argument-types {}",
                                                           matchingMethods.size(),
                                                           staticMethod ? "static" : "instance",
                                                           type.getName(),
                                                           Arrays.toString(argumentTypes)));
    }


    /**
     * Invoke a static method on the {@link #type} wrapped by this {@link Reflector} instance
     *
     * @param methodName the name of the method that will be invoked
     * @param arguments  the arguments that will be passed to the matching static method.<br>
     *                   The arguments will be converted to {@link Parameters#argumentTypes(Object...)} and the search for a matching static method
     *                   uses the {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)} without requiring an exact parameter type match
     * @param <R>        the return type
     * @return the result of invoking the static method
     * @throws NoMatchingMethodFoundException  if a matching method cannot be found
     * @throws MethodInvocationFailedException if the method invocation fails
     * @see #invokeStatic(Method, Object...)
     */
    @SuppressWarnings("unchecked")
    public <R> R invokeStatic(String methodName, Object... arguments) {
        requireNonNull(methodName, "You must supply a methodName");
        var argumentTypes = argumentTypes(arguments);
        var method = findMatchingMethod(methodName, true, argumentTypes)
                .orElseThrow(() -> new NoMatchingMethodFoundException(msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes))));
        return invokeStatic(method, arguments);
    }

    /**
     * Invoke the static on the {@link #type} wrapped by this {@link Reflector} instance
     *
     * @param method    the method that will be invoked
     * @param arguments the arguments that will be passed to the static method.
     * @param <R>       the return type
     * @return the result of invoking the static method
     * @throws MethodInvocationFailedException if the method invocation fails
     * @see #invokeStatic(Method, Object...)
     */
    @SuppressWarnings("unchecked")
    public <R> R invokeStatic(Method method, Object... arguments) {
        requireNonNull(method, "You must supply a method");
        try {
            return (R) method.invoke(type, arguments);
        } catch (Exception e) {
            throw new MethodInvocationFailedException(msg("Failed to invoke static method '{}' on type '{}' taking arguments of {}", method.getName(), type.getName(), Arrays.toString(method.getParameterTypes())), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Invoke an instance method on the <code>invokeOnObject</code> instance
     *
     * @param methodName     the name of the method that will be invoked
     * @param invokeOnObject the object instance that the matching method will be invoked on
     * @param withArguments  the arguments that will be passed to the matching instance method.<br>
     *                       The arguments will be converted to {@link Parameters#argumentTypes(Object...)} and the search for a matching method on the {@link #type} wrapped
     *                       by this {@link Reflector} instance. The parameter matching uses the {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)} without
     *                       requiring an exact parameter type match
     * @param <R>            the return type
     * @return the result of invoking the method
     * @throws NoMatchingMethodFoundException  if a matching method cannot be found
     * @throws MethodInvocationFailedException if the method invocation fails
     * @see #invoke(Method, Object, Object...)
     */
    public <R> R invoke(String methodName, Object invokeOnObject, Object... withArguments) {
        requireNonNull(methodName, "You must supply a methodName");
        requireNonNull(invokeOnObject, "You must supply an invokeOnObject");
        var argumentTypes = argumentTypes(withArguments);
        var method = findMatchingMethod(methodName, false, argumentTypes)
                .orElseThrow(() -> new NoMatchingMethodFoundException(msg("Failed to find method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes))));
        return invoke(method, invokeOnObject, withArguments);
    }

    /**
     * Invoke the instance method on the <code>invokeOnObject</code> instance
     *
     * @param method         the method that will be invoked
     * @param invokeOnObject the object instance that the matching method will be invoked on
     * @param withArguments  the arguments that will be passed to the instance method.<
     * @param <R>            the return type
     * @return the result of invoking the method
     * @throws NoMatchingMethodFoundException  if a matching method cannot be found
     * @throws MethodInvocationFailedException if the method invocation fails
     */
    @SuppressWarnings("unchecked")
    public <R> R invoke(Method method, Object invokeOnObject, Object... withArguments) {
        requireNonNull(method, "You must supply a method");
        requireNonNull(invokeOnObject, "You must supply an invokeOnObject");
        try {
            return (R) method.invoke(invokeOnObject, withArguments);
        } catch (Exception e) {
            throw new ReflectionException(msg("Failed to invoke method '{}' on '{}'", method.toGenericString(), invokeOnObject), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Find the instance {@link Field}, in the {@link #type} wrapped by this {@link Reflector} instance, that matches on the field name
     *
     * @param fieldName the name of the field we're searching for
     * @return an {@link Optional} with the matching {@link Field}, otherwise an {@link Optional#empty()} if no matching field can be found
     */
    public Optional<Field> findFieldByName(String fieldName) {
        return fields.stream()
                     .filter(_field -> _field.getName().equals(fieldName))
                     .findFirst();
    }

    /**
     * Find the static {@link Field}, in the {@link #type} wrapped by this {@link Reflector} instance, that matches on the field name
     *
     * @param fieldName the name of the field we're searching for
     * @return an {@link Optional} with the matching {@link Field}, otherwise an {@link Optional#empty()} if no matching field can be found
     */
    public Optional<Field> findStaticFieldByName(String fieldName) {
        return fields.stream()
                     .filter(_field -> _field.getName().equals(fieldName) && Modifier.isStatic(_field.getModifiers()))
                     .findFirst();
    }

    /**
     * Get the value of an instance field
     *
     * @param object    the object where the field value will get read from
     * @param fieldName the name of the field
     * @param <R>       the return type
     * @return the value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name in the {@link #type} wrapped by this {@link Reflector} instance
     * @throws GetFieldException     in case getting the field value fails
     */
    @SuppressWarnings("unchecked")
    public <R> R get(Object object, String fieldName) {
        requireNonNull(object, "You must supply an object");
        requireNonNull(fieldName, "You must supply a fieldName");
        return get(object,
                   findFieldByName(fieldName)
                           .orElseThrow(() -> new NoFieldFoundException(msg("Failed to find field '{}' in type '{}'", fieldName, type.getName())))
                  );
    }

    /**
     * Get the value of an instance field
     *
     * @param object the object where the field value will get read from
     * @param field  the field to read
     * @param <R>    the return type
     * @return the value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name
     * @throws GetFieldException     in case getting the field value fails
     */
    @SuppressWarnings("unchecked")
    public <R> R get(Object object, Field field) {
        requireNonNull(object, "You must supply an object");
        requireNonNull(field, "You must supply a field");
        try {
            return (R) field.get(object);
        } catch (Exception e) {
            throw new GetFieldException(msg("Failed to get field {}#{} inside object of type '{}'", type.getName(), field.getName(), object.getClass().getName()));
        }
    }

    /**
     * Get the value of a static field on the {@link #type} wrapped by this {@link Reflector} instance
     *
     * @param fieldName the name of the field
     * @param <R>       the return type
     * @return the value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name in the {@link #type} wrapped by this {@link Reflector} instance
     * @throws GetFieldException     in case getting the field value fails
     */
    @SuppressWarnings("unchecked")
    public <R> R getStatic(String fieldName) {
        requireNonNull(fieldName, "You must supply a fieldName");
        return getStatic(findStaticFieldByName(fieldName)
                                 .orElseThrow(() -> new NoFieldFoundException(msg("Failed to find static field '{}' in type '{}'", fieldName, type.getName())))
                        );
    }

    /**
     * Get the value of a static field on the {@link #type} wrapped by this {@link Reflector} instance
     *
     * @param field the static field to read
     * @param <R>   the return type
     * @return the value of the field
     * @throws GetFieldException in case getting the field value fails
     */
    @SuppressWarnings("unchecked")
    public <R> R getStatic(Field field) {
        requireNonNull(field, "You must supply a field");
        try {
            return (R) field.get(null);
        } catch (Exception e) {
            throw new GetFieldException(msg("Failed to get static field {}#{}", type.getName(), field.getName()));
        }
    }

    /**
     * Set the instance field to the provided <code>newFieldValue</code>
     *
     * @param object        the object where the field value will get read from
     * @param fieldName     the name of the field
     * @param newFieldValue the new value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name in the {@link #type} wrapped by this {@link Reflector} instance
     * @throws SetFieldException     in case setting the field value fails
     */
    public void set(Object object, String fieldName, Object newFieldValue) {
        requireNonNull(object, "You must supply an object");
        requireNonNull(fieldName, "You must supply a fieldName");
        set(object,
            findFieldByName(fieldName)
                    .orElseThrow(() -> new NoFieldFoundException(msg("Failed to find field '{}' in type '{}'", fieldName, type.getName()))),
            newFieldValue);
    }

    /**
     * Set the instance field to the provided <code>newFieldValue</code>
     *
     * @param object        the object where the field value will get read from
     * @param field         the field on which to set a new value
     * @param newFieldValue the new value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name in the {@link #type} wrapped by this {@link Reflector} instance
     * @throws SetFieldException     in case setting the field value fails
     */
    public void set(Object object, Field field, Object newFieldValue) {
        requireNonNull(object, "You must supply an object");
        requireNonNull(field, "You must supply a field");
        try {
            field.set(object, newFieldValue);
        } catch (Exception e) {
            throw new SetFieldException(msg("Failed to set field {}#{} inside object of type '{}'", type.getName(), field.getName(), object.getClass().getName()));
        }
    }

    /**
     * Set the static field, on the {@link #type} wrapped by this {@link Reflector} instance, to the provided <code>newFieldValue</code>
     *
     * @param fieldName     the name of the field
     * @param newFieldValue the new value of the field
     * @throws NoFieldFoundException in case we couldn't find a field with a matching name in the {@link #type} wrapped by this {@link Reflector} instance
     * @throws SetFieldException     in case setting the field value fails
     */
    public void setStatic(String fieldName, Object newFieldValue) {
        requireNonNull(fieldName, "You must supply a fieldName");
        setStatic(findStaticFieldByName(fieldName)
                          .orElseThrow(() -> new NoFieldFoundException(msg("Failed to find static field '{}' in type '{}'", fieldName, type.getName()))),
                  newFieldValue);
    }

    /**
     * Set the static field, on the {@link #type} wrapped by this {@link Reflector} instance, to the provided <code>newFieldValue</code>
     *
     * @param field         the field on which to set a new value
     * @param newFieldValue the new value of the field
     * @throws SetFieldException in case setting the field value fails
     */
    public void setStatic(Field field, Object newFieldValue) {
        requireNonNull(field, "You must supply a field");
        try {
            field.set(null, newFieldValue);
        } catch (Exception e) {
            throw new SetFieldException(msg("Failed to set static field {}#{}", type.getName(), field.getName()));
        }
    }

    /**
     * Find the {@link Field} in the {@link #type} wrapped by this {@link Reflector} instance, which has the specified annotation
     *
     * @param annotation the annotation
     * @return an {@link Optional} with the matching {@link Field}, otherwise an {@link Optional#empty()} if no matching field can be found
     * @throws TooManyMatchingFieldsFoundException in case we found more than one field with the specified annotation
     */
    public Optional<Field> findFieldByAnnotation(Class<? extends Annotation> annotation) {
        requireNonNull(annotation, "You must supply an annotation");
        var matchingFields = fields.stream()
                                   .filter(field -> field.isAnnotationPresent(annotation))
                                   .collect(Collectors.toList());
        if (matchingFields.isEmpty()) {
            return Optional.empty();
        } else if (matchingFields.size() == 1) {
            return Optional.of(matchingFields
                                       .get(0));
        }
        throw new TooManyMatchingFieldsFoundException(msg("Found {} fields within {} matching on annotation {}",
                                                          matchingFields.size(),
                                                          type.getName(),
                                                          annotation.getName()));
    }
}
