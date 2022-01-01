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

import java.util.*;

import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

/**
 * Collection of {@link Objects#requireNonNull(Object)} replacement methods
 */
public final class FailFast {
    /**
     * Assert that the <code>objectThatMustBeAnInstanceOf</code> is an instance of <code>mustBeAnInstanceOf</code> parameter, if not then {@link IllegalArgumentException} is thrown
     *
     * @param objectThatMustBeAnInstanceOf the object that must be an instance of <code>mustBeAnInstanceOf</code> parameter
     * @param mustBeAnInstanceOf           the type that the <code>objectThatMustBeAnInstanceOf</code> parameter must be an instance of
     * @param <T>                          the type of class that the <code>objectThatMustBeAnInstanceOf</code> must be an instance of
     * @return the "objectThatMustBeAnInstanceOf" IF it is of the right type
     */
    public static <T> T requireMustBeInstanceOf(Object objectThatMustBeAnInstanceOf, Class<T> mustBeAnInstanceOf) {
        return requireMustBeInstanceOf(objectThatMustBeAnInstanceOf, mustBeAnInstanceOf, null);
    }

    /**
     * Assert that the <code>objectThatMustBeAnInstanceOf</code> is an instance of <code>mustBeAnInstanceOf</code> parameter, if not then {@link IllegalArgumentException} is thrown
     *
     * @param objectThatMustBeAnInstanceOf the object that must be an instance of <code>mustBeAnInstanceOf</code> parameter
     * @param mustBeAnInstanceOf           the type that the <code>objectThatMustBeAnInstanceOf</code> parameter must be an instance of
     * @param message                      the optional message that will become the message of the {@link IllegalArgumentException} in case the <code>objectThatMustBeAnInstanceOf</code> is NOT an instance of <code>mustBeAnInstanceOf</code>
     * @param <T>                          the type of class that the <code>objectThatMustBeAnInstanceOf</code> must be an instance of
     * @return the "objectThatMustBeAnInstanceOf" IF it is of the right type
     */
    @SuppressWarnings("unchecked")
    public static <T> T requireMustBeInstanceOf(Object objectThatMustBeAnInstanceOf, Class<?> mustBeAnInstanceOf, String message) {
        if (objectThatMustBeAnInstanceOf == null) {
            throw new IllegalArgumentException(message != null ? message : "Object was null and therefore not an instance of " + mustBeAnInstanceOf.getName());
        }
        if (mustBeAnInstanceOf == null) {
            throw new IllegalArgumentException(message != null ? message : "Cannot verify instanceOf since the mustBeAnInstanceOf parameter is null");
        }
        if (!mustBeAnInstanceOf.isAssignableFrom(objectThatMustBeAnInstanceOf.getClass())) {
            throw new IllegalArgumentException(message != null ? message : msg("Expected {} to be an instance of {}", objectThatMustBeAnInstanceOf.getClass().getName(), mustBeAnInstanceOf.getName()));
        }
        return (T) objectThatMustBeAnInstanceOf;
    }

    /**
     * Assert that the <code>objectThatMayNotBeNull</code> is NOT null. If it's null then an {@link IllegalArgumentException} is thrown
     *
     * @param objectThatMayNotBeNull the object that must NOT be null
     * @param message                the optional message that will become the message of the {@link IllegalArgumentException} in case the <code>objectThatMayNotBeNull</code> is NULL
     * @param <T>                    the type of the <code>objectThatMayNotBeNull</code>
     * @return the "objectThatMayNotBeNull" IF it's not null
     */
    public static <T> T requireNonNull(T objectThatMayNotBeNull, String message) {
        if (objectThatMayNotBeNull == null) {
            throw new IllegalArgumentException(message != null ? message : "Object was null, we expected it to be non-null");
        }
        return objectThatMayNotBeNull;
    }

    /**
     * Assert that the <code>characterStreamThatMustNotBeEmptyOrNull</code> is NOT null AND NOT empty/blank, otherwise a  {@link IllegalArgumentException} is thrown
     *
     * @param characterStreamThatMustNotBeEmptyOrNull the object that must NOT be null or empty
     * @param message                                 the optional message that will become the message of the {@link IllegalArgumentException} in case the <code>characterStreamThatMustNotBeEmptyOrNull</code> is NULL
     * @param <T>                                     the type of the <code>characterStreamThatMustNotBeEmptyOrNull</code>
     * @return the "characterStreamThatMustNotBeEmptyOrNull" IF it's not null
     */
    public static <T extends CharSequence> T requireNonBlank(T characterStreamThatMustNotBeEmptyOrNull, String message) {
        if (characterStreamThatMustNotBeEmptyOrNull == null) {
            throw new IllegalArgumentException(message != null ? message : "Character sequence was null, we expected it to be non-null and not empty");
        }
        if (characterStreamThatMustNotBeEmptyOrNull.length() == 0) {
            throw new IllegalArgumentException(message != null ? message : "Character sequence was empty, we expected it to be non empty");
        }
        return characterStreamThatMustNotBeEmptyOrNull;
    }

    /**
     * Assert that the <code>objectThatMayNotBeNull</code> is NOT null. If it's null then an {@link IllegalArgumentException} is thrown<br>
     * Short handle for calling {@code requireNonNull(someObjectThatMustNotBeNull, m("Message with {} placeholder", someMessageObject))} - (see {@link MessageFormatter#msg(String, Object...)})
     *
     * @param objectThatMayNotBeNull the object that must NOT be null
     * @param message                the optional message that will become the message of the {@link IllegalArgumentException} in case the <code>objectThatMayNotBeNull</code> is NULL
     * @param messageArguments       any placeholders for the message (see {@link MessageFormatter#msg(String, Object...)})
     * @param <T>                    the type of the <code>objectThatMayNotBeNull</code>
     * @return the "objectThatMayNotBeNull" IF it's not null
     */
    public static <T> T requireNonNull(T objectThatMayNotBeNull, String message, Object... messageArguments) {
        if (objectThatMayNotBeNull == null) {
            throw new IllegalArgumentException(message != null ? msg(message, messageArguments) : "Object was null, we expected it to be non-null");
        }
        return objectThatMayNotBeNull;
    }

    /**
     * Assert that the <code>objectThatMayNotBeNull</code> is NOT null. If it's null then an {@link IllegalArgumentException} is thrown
     *
     * @param objectThatMayNotBeNull the object that must NOT be null
     * @param <T>                    the type of the <code>objectThatMayNotBeNull</code>
     * @return the "objectThatMayNotBeNull" IF it's not null
     */
    public static <T> T requireNonNull(T objectThatMayNotBeNull) {
        return requireNonNull(objectThatMayNotBeNull, null);
    }

    /**
     * Assert that the boolean value <code>mustBeTrue</code> is <code>true</code>. If not then an {@link IllegalArgumentException} is thrown
     *
     * @param mustBeTrue boolean value that we will assert MUST be TRUE
     * @param message    the NON optional message that will become the message of the {@link IllegalArgumentException} in case the <code>mustBeTrue</code> is FALSE
     */
    public static void requireTrue(boolean mustBeTrue, String message) {
        requireNonNull(message, "You must provide a NON NULL message argument to requireTrue(boolean, String)");
        if (!mustBeTrue) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the boolean value <code>mustBeTrue</code> is <code>true</code>. If not then an {@link IllegalArgumentException} is thrown
     *
     * @param mustBeFalse boolean value that we will assert MUST be FALSE
     * @param message     the NON optional message that will become the message of the {@link IllegalArgumentException} in case the <code>mustBeFalse</code> is TRUE
     */
    public static void requireFalse(boolean mustBeFalse, String message) {
        requireNonNull(message, "You must provide a NON NULL message argument to requireFalse(boolean, String)");
        if (mustBeFalse) {
            throw new IllegalArgumentException(message);
        }
    }

    public static Object[] requireNonEmpty(Object[] items, String message) {
        if (items == null || items.length == 0) {
            throw new IllegalArgumentException(message);
        }
        return items;
    }

    public static <T> List<T> requireNonEmpty(List<T> items, String message) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException(message);
        }
        return items;
    }

    public static <T> Set<T> requireNonEmpty(Set<T> items, String message) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException(message);
        }
        return items;
    }

    public static <K, V> Map<K, V> requireNonEmpty(Map<K, V> items, String message) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException(message);
        }
        return items;
    }
}
