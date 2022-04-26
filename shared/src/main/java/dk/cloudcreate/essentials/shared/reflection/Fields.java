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

/**
 * Utility class for working with {@link Field}'s
 */
public class Fields {
    /**
     * Find a field by name and type
     *
     * @param fields    the set of Fields (e.g. returned by {@link #fields(Class)})
     * @param fieldName the name of the field
     * @param fieldType the type of the field
     * @return Optional with the matching field or {@link Optional#empty()}
     */
    public static Optional<Field> findField(Set<Field> fields,
                                            String fieldName,
                                            Class<?> fieldType) {
        requireNonNull(fields, "You must supply a fields set");
        requireNonBlank(fieldName, "You must supply a fieldName");
        requireNonNull(fieldType, "You must supply a fieldType");
        return fields.stream()
                     .filter(field -> field.getName().equals(fieldName))
                     .filter(field -> field.getType().equals(fieldType))
                     .findFirst();
    }

    /**
     * Get all fields in the type
     *
     * @param type the type we want to find all fields within
     * @return all fields (each marked as accessible) in the type
     */
    public static Set<Field> fields(Class<?> type) {
        requireNonNull(type, "You must supply a type");
        var fields = new HashSet<Field>();

        var currentType = type;
        while (currentType != null) {
            for (var declaredField : currentType.getDeclaredFields()) {
                if (currentType.getPackageName().startsWith("java.") && !Modifier.isPublic(declaredField.getModifiers())) {
                    // Ignore non public fields on any base java classes to avoid InaccessibleObjectException
                    continue;
                }

                if (fields.stream().noneMatch(field -> field.getName().equals(declaredField.getName()))) {
                    fields.add(Accessibles.accessible(declaredField));
                }
            }

            currentType = currentType.getSuperclass();
        }
        return fields;
    }
}
