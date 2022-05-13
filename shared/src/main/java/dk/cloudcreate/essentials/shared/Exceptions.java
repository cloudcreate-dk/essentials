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

package dk.cloudcreate.essentials.shared;

import java.io.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

public final class Exceptions {
    /**
     * Allows us to sneaky throw a checked exception without having to wrap it<br>
     * Use with caution
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {
        requireNonNull(t, "You must supply an exception");
        throw (T) t;
    }

    /**
     * Get the stacktrace of the <code>throwable</code> as a String
     * @param throwable the exception
     * @return the <code>throwable</code>'s full stacktrace
     */
    public static String getStackTrace(Throwable throwable) {
        requireNonNull(throwable, "You must specify a throwable");
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static Throwable getRootCause(Throwable exception) {
        var rootCause = exception;
        var parentCause = rootCause.getCause();
        while (parentCause != null) {
            rootCause = parentCause;
            if (parentCause == parentCause.getCause()) {
                parentCause = null;
            } else {
                parentCause = parentCause.getCause();
            }
        }
        return rootCause;
    }
}