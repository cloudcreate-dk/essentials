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

import dk.cloudcreate.essentials.shared.*;

import java.util.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Utility class for working with {@link Class}'s
 */
public final class Classes {
    /**
     * Load a class based on a fully qualified class name
     *
     * @param fullyQualifiedClassName the Fully Qualified Class Name as a
     * @return The class loaded
     * @throws ReflectionException in case loading the class failed
     */
    public static Class<?> forName(String fullyQualifiedClassName) throws ReflectionException {
        requireNonNull(fullyQualifiedClassName, "You must supply a fullyQualifiedClassName");
        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (Exception e) {
            throw new LoadingClassFailedException(MessageFormatter.msg("Failed to load class based on name '{}'", fullyQualifiedClassName), e);
        }
    }

    /**
     * Load a class based on a fully qualified class name and using a specific {@link ClassLoader}
     *
     * @param fullyQualifiedClassName the Fully Qualified Class Name as a string
     * @param classLoader             the class loader to use
     * @return The class loaded
     * @throws ReflectionException in case loading the class failed
     */
    public static Class<?> forName(String fullyQualifiedClassName, ClassLoader classLoader) throws ReflectionException {
        requireNonNull(fullyQualifiedClassName, "You must supply a fullyQualifiedClassName");
        requireNonNull(classLoader, "You must supply a classLoader");
        try {
            return Class.forName(fullyQualifiedClassName, true, classLoader);
        } catch (Exception e) {
            throw new LoadingClassFailedException(MessageFormatter.msg("Failed to load class '{}'", fullyQualifiedClassName), e);
        }
    }

    /**
     * Compares <code>leftSide</code> with the <code>rightSide</code> for inheritance order.<br>
     * It returns:
     * <ul>
     *     <li>negative integer - if the leftSide is less specific then the rightSide<br>
     *     Example: If leftSide is a CharSequence and rightSide is a String (a specialization of CharSequence).</li>
     *     <li>zero - if the leftSide is equally specific to rightSide (i.e. leftSide and rightSide are the same class)<br>
     *     Example: If leftSide is a CharSequence and rightSide is a CharSequence</li>
     *     <li>negative integer - if the leftSide is more specific then the rightSide<br>
     *     Example: If leftSide is a String (a subclass of CharSequence) and rightSide is a CharSequence.</li>
     * </ul>
     *
     * @param leftSide  the left side type
     * @param rightSide the right side type
     * @return the inheritance comparison between the leftSide and rightSide
     * @throws IllegalArgumentException in case leftSide and rightSide aren't either the same class or in a inheritance hierarchy with each other
     */
    public static int compareTypeSpecificity(Class<?> leftSide, Class<?> rightSide) {
        requireNonNull(leftSide, "No leftSide supplied");
        requireNonNull(rightSide, "No rightSide supplied");

        if (leftSide == rightSide) {
            return 0;
        }
        if (leftSide.isAssignableFrom(rightSide)) {
            // left side is the most generic type
            return -1;
        } else if (rightSide.isAssignableFrom(leftSide)) {
            // left side is the most specific type
            return 1;
        }
        throw new IllegalArgumentException(MessageFormatter.msg("leftSide {} and rightSide {} aren't in the same type hierarchy", leftSide.getName(), rightSide.getName()));
    }

    /**
     * Get all superclasses of a type.
     *
     * @param type The type to get all super classes for
     * @return All superclasses of <code>type</code>
     */
    public static List<Class<?>> superClasses(Class<?> type) {
        requireNonNull(type, "No type supplied");
        var superClasses = new ArrayList<Class<?>>();
        while (type != null && !type.equals(Object.class)) {
            type = type.getSuperclass();
            if (type != null) {
                superClasses.add(type);
            }
        }
        return superClasses;
    }
}
