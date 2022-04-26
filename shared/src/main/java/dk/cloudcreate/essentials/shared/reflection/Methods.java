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


import java.lang.reflect.*;
import java.util.*;

import static dk.cloudcreate.essentials.shared.FailFast.*;
import static dk.cloudcreate.essentials.shared.reflection.Parameters.parameterTypesMatches;

/**
 * Utility class for working with {@link Method}'s
 */
public final class Methods {
    /**
     * Find a matching method based on:
     * <ul>
     *     <li>method name</li>
     *     <li>return type</li>
     *     <li>exact parameter-type comparison using {@link Parameters#parameterTypesMatches(Class[], Class[], boolean)}</li>
     * </ul>
     *
     * @param methods         the set of method to search (e.g. fetched using {@link #methods(Class)})
     * @param methodName      the name of the method we're looking for
     * @param returnType      the method return type that much match
     * @param parametersTypes the method parameter types that must be matched
     * @return Optional with the exact matching method or {@link Optional#empty()}
     */
    public static Optional<Method> findMatchingMethod(Set<Method> methods,
                                                      String methodName,
                                                      Class<?> returnType,
                                                      Class<?>... parametersTypes) {
        requireNonNull(methods, "You must supply a set of Methods");
        requireNonBlank(methodName, "You must supply a methodName");
        requireNonNull(returnType, "You must supply a returnType");
        requireNonNull(parametersTypes, "You must supply parametersTypes");

        return methods.stream()
                      .filter(method -> method.getName().equals(methodName))
                      .filter(method -> method.getReturnType().equals(returnType))
                      .filter(method -> Parameters.parameterTypesMatches(method.getParameterTypes(), parametersTypes, true))
                      .findFirst();
    }

    /**
     * Get all methods on type. For overridden methods only the most specific (typically being the method overridden by a subclass) is returned
     *
     * @param type the type we want to find all methods within
     * @return the list of all methods (each marked as accessible) on this type
     * @see Accessibles#accessible
     */
    @SuppressWarnings("DuplicatedCode")
    public static Set<Method> methods(final Class<?> type) {
        requireNonNull(type, "You must supply a type");
        var methods = new HashSet<Method>();

        var currentType = type;
        while (currentType != null) {
            for (var declaredMethod : currentType.getDeclaredMethods()) {
                if (currentType.getPackageName().startsWith("java.") && !Modifier.isPublic(declaredMethod.getModifiers())) {
                    // Ignore non public method on any base java classes to avoid InaccessibleObjectException
                    continue;
                }
                boolean hasAlreadyBeenOverriddenByASubClass = false;

                for (var alreadyDiscoveredMethod : methods) {
                    if (alreadyDiscoveredMethod.getName().equals(declaredMethod.getName()) &&
                            parameterTypesMatches(declaredMethod.getParameterTypes(), alreadyDiscoveredMethod.getParameterTypes(), true)) {
                        // Overridden

                        // Check which method has the most concrete return type (e.g. in case of an overridden method with covariant return type - such as a Base class/interface method returning CharSequence
                        // but where a more concrete (sub)class overrides the methods and the return type to String instead of CharSequence)
                        int specificity = Classes.compareTypeSpecificity(declaredMethod.getReturnType(), alreadyDiscoveredMethod.getReturnType());
                        if (specificity <= 0) {
                            // The return-type of the declaredMethod is LESS or EQUALLY specific than the return-type of the alreadyDiscoveredMethod - so it's safe to ignore it
                            hasAlreadyBeenOverriddenByASubClass = true;
                        } else {
                            // The return-type of the declaredMethod is MORE specific than the return-type of the alreadyDiscoveredMethod - so we remove the alreadyDiscoveredMethod and add the declaredMethod
                            methods.remove(alreadyDiscoveredMethod);
                        }
                        break;
                    }
                }

                if (!hasAlreadyBeenOverriddenByASubClass) {
                    methods.add(Accessibles.accessible(declaredMethod));
                }
            }

            currentType = currentType.getSuperclass();
        }

        for (var _interface : Interfaces.interfaces(type)) {
            for (var declaredMethod : _interface.getDeclaredMethods()) {
                if (_interface.getPackageName().startsWith("java.") && !Modifier.isPublic(declaredMethod.getModifiers())) {
                    // Ignore non public method on any base java classes to avoid InaccessibleObjectException
                    continue;
                }
                boolean hasAlreadyBeenOverriddenByASubClass = false;
                for (var alreadyDiscoveredMethod : methods) {
                    if (alreadyDiscoveredMethod.getName().equals(declaredMethod.getName()) &&
                            parameterTypesMatches(declaredMethod.getParameterTypes(), alreadyDiscoveredMethod.getParameterTypes(), true)) {
                        hasAlreadyBeenOverriddenByASubClass = true;
                        break;
                    }
                }

                if (!hasAlreadyBeenOverriddenByASubClass) {
                    methods.add(Accessibles.accessible(declaredMethod));
                }
            }
        }

        return methods;
    }
}
