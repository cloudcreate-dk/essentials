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

import dk.cloudcreate.essentials.shared.FailFast;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Utility class for working with {@link Methods}/{@link Method} <b>parameters</b> and {@link Constructors}/{@link Constructor}
 * declaration <b>parameters</b> or method/constructor call <b>arguments</b>
 */
public class Parameters {
    /**
     * Class used to represent the unknown type for a <code>null</code> parameter to {@link #argumentTypes(Object...)}
     */
    public static final class NULL_ARGUMENT_TYPE {
    }

    /**
     * Check if an array of actual-parameter-types MATCH an array of declared-parameter-types while taking
     * <code>null</code> actual parameter types and {@link BoxedTypes#boxedType} into
     * consideration (in case <code>exactMatch</code> is false)
     *
     * @param actualParameterTypes   the actual parameter types (deduced from actual parameter values)
     * @param declaredParameterTypes the declared parameter types defined on the {@link Method} or
     *                               {@link Constructor}
     * @param exactMatch             Must the match be exact (each parameter type MUST match precisely)<br>
     *                               or do we allow testing using {@link BoxedTypes#boxedType(Class)},
     *                               <code>null</code> actualParameterTypes
     *                               and <code>isAssignableFrom</code> checks to bypass individual type matches
     * @return true if the parameter types match or not
     */
    public static boolean parameterTypesMatches(Class<?>[] actualParameterTypes, Class<?>[] declaredParameterTypes, boolean exactMatch) {
        FailFast.requireNonNull(actualParameterTypes, "No actualParameterTypes supplied");
        FailFast.requireNonNull(declaredParameterTypes, "No declaredParameterTypes supplied");

        if (actualParameterTypes.length != declaredParameterTypes.length) {
            return false;
        }

        if (exactMatch) {
            return Arrays.deepEquals(actualParameterTypes, declaredParameterTypes);
        } else {
            for (int index = 0; index < actualParameterTypes.length; index++) {
                Class<?> actualParameterType = BoxedTypes.boxedType(actualParameterTypes[index]);
                if (actualParameterType != NULL_ARGUMENT_TYPE.class) {
                    Class<?> declaredParameterType = BoxedTypes.boxedType(declaredParameterTypes[index]);
                    if (!declaredParameterType.isAssignableFrom(actualParameterType)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }


    /**
     * Convert method/constructor call arguments to their corresponding types.<br>
     * <code>null</code> values are represented using the {@link NULL_ARGUMENT_TYPE} type
     *
     * @param arguments arguments
     * @return the argument types
     */
    public static Class<?>[] argumentTypes(Object... arguments) {
        if (arguments == null) {
            return new Class[0];
        }

        var parameterTypes = new Class<?>[arguments.length];
        for (var index = 0; index < arguments.length; index++) {
            parameterTypes[index] = arguments[index] != null ? arguments[index].getClass() : NULL_ARGUMENT_TYPE.class;
        }

        return parameterTypes;
    }
}
