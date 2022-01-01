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

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

public final class BoxedTypes {
    public static boolean isPrimitiveType(Class<?> type) {
        requireNonNull(type, "You must supply a type");
        return type.isPrimitive();
    }

    public static boolean isBoxedType(Class<?> type) {
        requireNonNull(type, "You must supply a type");
        return Boolean.class == type ||
                Integer.class == type ||
                Long.class == type ||
                Short.class == type ||
                Byte.class == type ||
                Double.class == type ||
                Float.class == type ||
                Character.class == type ||
                Void.class == type;
    }

    public static Class<?> boxedType(Class<?> type) {
        if (type == null) {
            return null;
        }

        if (!type.isPrimitive()) {
            return type;
        }

        if (boolean.class == type) {
            return Boolean.class;
        }
        if (int.class == type) {
            return Integer.class;
        }
        if (long.class == type) {
            return Long.class;
        }
        if (short.class == type) {
            return Short.class;
        }
        if (byte.class == type) {
            return Byte.class;
        }
        if (double.class == type) {
            return Double.class;
        }
        if (float.class == type) {
            return Float.class;
        }
        if (char.class == type) {
            return Character.class;
        }
        if (void.class == type) {
            return Void.class;
        }
        throw new RuntimeException(MessageFormatter.msg("Unsupported boxed type '{}'", type.getName()));
    }
}
