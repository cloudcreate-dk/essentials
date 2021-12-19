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


import dk.cloudcreate.essentials.shared.FailFast;

import java.util.*;

public final class Interfaces {
    /**
     * Returns a set of all interfaces implemented by type supplied<br>
     *
     * @param type find all interfaces this type implements/extends
     * @return all interfaces implemented by the <code>type</code>
     */
    public static Set<Class<?>> interfaces(Class<?> type) {
        FailFast.requireNonNull(type, "You must supply a type");
        Set<Class<?>> interfaces = new HashSet<>();

        while (type != null) {
            for (Class<?> _interface : type.getInterfaces()) {
                interfaces.add(_interface);
                interfaces.addAll(interfaces(_interface));
            }
            type = type.getSuperclass();
        }

        return interfaces;
    }
}
