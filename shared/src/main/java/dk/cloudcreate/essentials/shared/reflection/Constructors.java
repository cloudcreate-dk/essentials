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
import java.util.List;
import java.util.stream.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Utility class for working with {@link Constructor}'s
 */
public final class Constructors {
    /**
     * Get all declared constructors on type
     *
     * @param type the type
     * @return the list of all declared constructors (each marked as accessible) on type
     * @see Accessibles#accessible
     */
    public static List<Constructor<?>> constructors(Class<?> type) {
        requireNonNull(type, "No type supplied");
        return Stream.of(type.getDeclaredConstructors())
                     .filter(constructor -> {
                         if (type.getPackageName().startsWith("java.")) {
                             return Modifier.isPublic(constructor.getModifiers());
                         }
                         return true;
                     })
                     .map(Accessibles::accessible)
                     .collect(Collectors.toList());
    }

}
