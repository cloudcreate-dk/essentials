/*
 * Copyright 2021 the original author or authors.
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

import dk.cloudcreate.essentials.shared.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import static dk.cloudcreate.essentials.shared.reflection.Parameters.*;

/**
 * Caching Java Reflection helper<br>
 * Encapsulates {@link Constructors}, {@link Methods} and {@link Fields} while providing high-level method for invoking constructors, method and getting/setting fields
 *
 * @see Interfaces
 */
public class Reflector {
    /**
     * Cache of Reflect instances
     */
    private static final ConcurrentMap<Class<?>, Reflector> REFLECTOR_CACHE = new ConcurrentHashMap<>();

    public final Class<?>             type;
    public final List<Constructor<?>> constructors;
    public final Set<Method>          methods;
    public final Set<Field>           fields;

    public static Reflector reflectOn(String fullyQualifiedClassName) {
        FailFast.requireNonNull(fullyQualifiedClassName, "You must supply a fullyQualifiedClassName");
        return reflectOn(Classes.forName(fullyQualifiedClassName));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static Reflector reflectOn(Class<?> type) {
        FailFast.requireNonNull(type, "You must supply a type");
        return REFLECTOR_CACHE.computeIfAbsent(type, _type -> new Reflector(_type));
    }

    private Reflector(Class<?> type) {
        this.type = type;
        this.constructors = Constructors.constructors(type);
        this.methods = Methods.methods(type);
        this.fields = Fields.fields(type);
    }

    /**
     * Match a constructor based on actual argument values (as opposed to {@link #hasMatchingConstructorBasedOnParameterTypes(Class[])})<br>
     * The constructor match doesn't require exact type matches only compatible type matches
     *
     * @param constructorArguments the actual argument values
     * @return true if there's a single matching constructor
     */
    public boolean hasMatchingConstructorBasedOnArguments(Object... constructorArguments) {
        var actualArgumentTypes = argumentTypes(constructorArguments);

        return this.constructors.stream().filter(_constructor ->
                                                         parameterTypesMatches(actualArgumentTypes, _constructor.getParameterTypes(), false)
                                                ).count() == 1;
    }

    /**
     * Match a constructor based on exact parameter types (as opposed to {@link #hasMatchingConstructorBasedOnArguments(Object...)})
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
     * Create a new instance of the {@link #type()} using a constructor matching the <code>constructorArguments</code>
     *
     * @param constructorArguments the arguments for the constructor
     * @param <T>                  the type of the object returned
     * @return the newly created instance
     * @throws ReflectionException in case something goes wrong
     */
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Object... constructorArguments) {
        Class<?>[] actualArgumentTypes = argumentTypes(constructorArguments);

        Constructor<?> constructor = this.constructors.stream()
                                                      .filter(_constructor -> parameterTypesMatches(actualArgumentTypes, _constructor.getParameterTypes(), false))
                                                      .findFirst()
                                                      .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Couldn't find a single constructor that matched {}", Arrays.toString(actualArgumentTypes))));

        try {
            return (T) constructor.newInstance(constructorArguments);
        } catch (Exception e) {
            throw new ReflectionException(
                    MessageFormatter.msg("Failed to create a new instance of constructor {} using arguments of type {}", constructor, actualArgumentTypes),
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

    public boolean hasMethod(String methodName, boolean staticMethod, Class<?>... argumentTypes) {
        return findMatchingMethod(methodName, staticMethod, argumentTypes).isPresent();
    }

    public Optional<Method> findMatchingMethod(String methodName, boolean staticMethod, Class<?>... argumentTypes) {
        FailFast.requireNonNull(methodName, "You must supply a methodName");
        return methods.stream().filter(method -> method.getName().equals(methodName) &&
                              (staticMethod == Modifier.isStatic(method.getModifiers())) &&
                              parameterTypesMatches(argumentTypes, method.getParameterTypes(), false))
                      .findFirst();
    }


    @SuppressWarnings("unchecked")
    public <T> T invokeStatic(String methodName, Object... arguments) {
        FailFast.requireNonNull(methodName, "You must supply a methodName");
        var argumentTypes = argumentTypes(arguments);
        var method = findMatchingMethod(methodName, true, argumentTypes)
                .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes))));
        return invokeStatic(method, arguments);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeStatic(Method method, Object... arguments) {
        FailFast.requireNonNull(method, "You must supply a method");
        try {
            return (T) method.invoke(type, arguments);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to invoke static method '{}' on type '{}' taking arguments of {}", method.getName(), type.getName(), Arrays.toString(method.getParameterTypes())), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    public <R> R invoke(String methodName, Object invokeOnObject, Object... withArguments) {
        FailFast.requireNonNull(methodName, "You must supply a methodName");
        FailFast.requireNonNull(invokeOnObject, "You must supply an invokeOnObject");
        var argumentTypes = argumentTypes(withArguments);
        var method        = findMatchingMethod(methodName, false, argumentTypes).orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find static method '{}' on type '{}' taking arguments of {}", methodName, type.getName(), Arrays.toString(argumentTypes))));
        return invoke(method, invokeOnObject, withArguments);
    }

    @SuppressWarnings("unchecked")
    public <R> R invoke(Method method, Object invokeOnObject, Object... withArguments) {
        FailFast.requireNonNull(method, "You must supply a method");
        FailFast.requireNonNull(invokeOnObject, "You must supply an invokeOnObject");
        try {
            return (R) method.invoke(invokeOnObject, withArguments);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to invoke '{}' on '{}'", method.toGenericString(), invokeOnObject), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Optional<Field> findFieldByName(String fieldName) {
        return fields.stream()
                     .filter(_field -> _field.getName().equals(fieldName))
                     .findFirst();
    }

    public Optional<Field> findStaticFieldByName(String fieldName) {
        return fields.stream()
                     .filter(_field -> _field.getName().equals(fieldName) && Modifier.isStatic(_field.getModifiers()))
                     .findFirst();
    }

    @SuppressWarnings("unchecked")
    public <R> R get(Object object, String fieldName) {
        FailFast.requireNonNull(object, "You must supply an object");
        FailFast.requireNonNull(fieldName, "You must supply a fieldName");
        return get(object,
                   findFieldByName(fieldName)
                           .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find field '{}' in type '{}'", fieldName, type.getName())))
                  );
    }


    @SuppressWarnings("unchecked")
    public <R> R get(Object object, Field field) {
        FailFast.requireNonNull(object, "You must supply an object");
        FailFast.requireNonNull(field, "You must supply a field");
        try {
            return (R) field.get(object);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to get field {}#{} inside object of type '{}'", type.getName(), field.getName(), object.getClass().getName()));
        }
    }

    @SuppressWarnings("unchecked")
    public <R> R getStatic(String fieldName) {
        FailFast.requireNonNull(fieldName, "You must supply a fieldName");
        return getStatic(findStaticFieldByName(fieldName)
                                 .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find static field '{}' in type '{}'", fieldName, type.getName())))
                        );
    }


    @SuppressWarnings("unchecked")
    public <R> R getStatic(Field field) {
        FailFast.requireNonNull(field, "You must supply a field");
        try {
            return (R) field.get(null);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to get static field {}#{}", type.getName(), field.getName()));
        }
    }

    public void set(Object object, String fieldName, Object newFieldValue) {
        FailFast.requireNonNull(object, "You must supply an object");
        FailFast.requireNonNull(fieldName, "You must supply a fieldName");
        set(object,
            findFieldByName(fieldName)
                    .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find field '{}' in type '{}'", fieldName, type.getName()))),
            newFieldValue);
    }

    public void set(Object object, Field field, Object newFieldValue) {
        FailFast.requireNonNull(object, "You must supply an object");
        FailFast.requireNonNull(field, "You must supply a field");
        try {
            field.set(object, newFieldValue);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to set field {}#{} inside object of type '{}'", type.getName(), field.getName(), object.getClass().getName()));
        }
    }

    public void setStatic(String fieldName, Object newFieldValue) {
        FailFast.requireNonNull(fieldName, "You must supply a fieldName");
        setStatic(findStaticFieldByName(fieldName)
                          .orElseThrow(() -> new ReflectionException(MessageFormatter.msg("Failed to find static field '{}' in type '{}'", fieldName, type.getName()))),
                  newFieldValue);
    }

    public void setStatic(Field field, Object newFieldValue) {
        FailFast.requireNonNull(field, "You must supply a field");
        try {
            field.set(null, newFieldValue);
        } catch (Exception e) {
            throw new ReflectionException(MessageFormatter.msg("Failed to set static field {}#{}", type.getName(), field.getName()));
        }
    }


    public Optional<Field> findFieldByAnnotation(Class<? extends Annotation> annotation) {
        FailFast.requireNonNull(annotation, "You must supply an annotation");
        return fields.stream()
                     .filter(field -> field.isAnnotationPresent(annotation))
                     .findFirst();
    }
}
