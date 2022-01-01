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

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static java.lang.reflect.Modifier.isPublic;

/**
 * Utility class for working with {@link AccessibleObject}/{@link Member}'s
 */
public final class Accessibles {
    /**
     * Set the {@link AccessibleObject} as accessible
     *
     * @param object the object that we want to ensure is accessible
     * @param <T>    the type of accessible object
     * @return the object marked as accessible
     * @throws ReflectionException in case we couldn't mark the <code>object</code> as accessible
     */
    public static <T extends AccessibleObject> T accessible(T object) {
        requireNonNull(object, "No object supplied");
        if (!isAccessible(object)) {
            object.setAccessible(true);
        }

        return object;
    }

    @SuppressWarnings("deprecation")
    public static boolean isAccessible(AccessibleObject object) {
        requireNonNull(object, "No object supplied");
        if (object instanceof Member) {
            var member = (Member) object;

            if (isPublic(member.getModifiers()) && isPublic(member.getDeclaringClass().getModifiers())) {
                return true;
            }
        }
        return object.isAccessible();
    }
}
