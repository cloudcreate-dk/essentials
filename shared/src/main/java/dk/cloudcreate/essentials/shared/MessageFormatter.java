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
import java.util.stream.Collectors;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Slf4J compatible message formatter.<br>
 * <br>
 * The {@link MessageFormatter} supports formatting a message using Slf4J style message anchors:<br>
 * <pre>
 * msg("This {} a {} message {}", "is", "simple", "example");
 * </pre>
 * will return <code>"This is a simple message example"</code><br>
 * <br>
 * The {@link MessageFormatter} also support <b>named argument binding</b>:<br>
 * <pre>
 * bind("Hello {:firstName} {:lastName}.",
 *      arg("firstName", "Peter"),
 *      arg("lastName", "Hansen"))
 * </pre>
 * will return the string <code>"Hello Peter Hansen."</code>
 */
public final class MessageFormatter {
    /**
     * Format a message using Slf4J like message anchors.<br>
     * Example: <code>msg("This {} a {} message {}", "is", "simple", "example");</code> will result in this String:<br>
     * <code>"This is a simple message example"</code>
     *
     * @param message                        The message that can contain zero or more message anchors <code>{}</code> that will be replaced positionally by matching values in <code>messageAnchorPlaceHolderValues</code>
     * @param messageAnchorPlaceHolderValues the positional placeholder values that will be inserted into the <code>message</code> if it contains matching anchors
     * @return the message with any anchors replaced with <code>messageAnchorPlaceHolderValues</code>
     */
    public static String msg(String message, Object... messageAnchorPlaceHolderValues) {
        requireNonNull(message, "You must supply a message");
        requireNonNull(messageAnchorPlaceHolderValues, "You must supply a messageAnchorPlaceHolderValues");
        return String.format(message.replaceAll("\\{}", "%s"), messageAnchorPlaceHolderValues);
    }

    /**
     * Replaces placeholders of style <code>"Hello {:firstName} {:lastName}."</code> with bind of <code>firstName</code> to "Peter" and <code>lastName</code> to "Hansen" this
     * will return the string <code>"Hello Peter Hansen."</code>:
     * <pre>
     *     bind("Hello {:firstName} {:lastName}.",
     *              Map.of("firstName", "Peter",
     *                     "lastName", "Hansen"
     *              )
     *         )
     * </pre>
     *
     * @param message  the message string
     * @param bindings the map where the key will become the {@link NamedArgumentBinding#name} and the value will become the {@link NamedArgumentBinding#value}<br>
     *                 Values are converted to String's using the toString()
     * @return the message merged with the bindings
     */
    public static String bind(String message, Map<String, Object> bindings) {
        requireNonNull(message, "You must supply a message");
        requireNonNull(bindings, "You must supply bindings");
        return bind(message, bindings.entrySet().stream().map(nameValue -> new NamedArgumentBinding(nameValue.getKey(), nameValue.getValue())).collect(Collectors.toList()));
    }

    /**
     * Replaces placeholders of style <code>"Hello {:firstName} {:lastName}."</code> with bind of <code>firstName</code> to "Peter" and <code>lastName</code> to "Hansen" this
     * will return the string <code>"Hello Peter Hansen."</code>:
     * <pre>
     *     bind("Hello {:firstName} {:lastName}.",
     *          arg("firstName", "Peter"),
     *          arg("lastName", "Hansen"))
     * </pre>
     *
     * @param message  the message string
     * @param bindings the vararg list of {@link NamedArgumentBinding}'s (prefer using the static method @{@link NamedArgumentBinding#arg(String, Object)}<br>
     *                 Values are converted to String's using the toString()
     * @return the message merged with the bindings
     */
    public static String bind(String message, NamedArgumentBinding... bindings) {
        requireNonNull(message, "You must supply a message");
        requireNonNull(bindings, "You must supply bindings");
        return bind(message, Arrays.asList(bindings));
    }

    /**
     * Replaces placeholders of style <code>"Hello {:firstName} {:lastName}."</code> with bind of <code>firstName</code> to "Peter" and <code>lastName</code> to "Hansen" this
     * will return the string "Hello Peter Hansen.":
     * <pre>
     *     bind("Hello {:firstName} {:lastName}.",
     *              List.of(
     *                  arg("firstName", "Peter"),
     *                  arg("lastName", "Hansen")
     *              )
     *         )
     * </pre>
     *
     * @param message  the message string
     * @param bindings list of {@link NamedArgumentBinding}'s (use the static method @{@link NamedArgumentBinding#arg(String, Object)}<br>
     *                 Values are converted to String's using the toString()
     * @return the message merged with the bindings
     */
    public static String bind(String message, List<NamedArgumentBinding> bindings) {
        requireNonNull(message, "You must supply a message");
        requireNonNull(bindings, "You must supply bindings");
        String result = message;
        for (NamedArgumentBinding bind : bindings) {
            try {
                result = result.replaceAll("\\{:" + bind.name + "}", bind.value.toString());
            } catch (MissingFormatArgumentException e) {
                throw new IllegalArgumentException(msg("Failed to replace bind :{} with value '{}'", bind.name, bind.value, bind.value), e);
            }
        }
        return result;
    }

    /**
     * Named argument binding instance
     */
    public static class NamedArgumentBinding {
        /**
         * The name of the argument
         */
        public final String name;
        /**
         * the value that will be inserted where ever an argument with name matching {@link #name} is placed
         */
        public final Object value;

        /**
         * Create a new Named argument binding<br>
         * <pre>
         *     bind("Hello {:firstName}",
         *          new NamedArgumentBinding("firstName", "Peter"))
         * </pre>
         * will return <code>"Hello Peter"</code>
         *
         * @param name  The name of the argument
         * @param value the value that will be inserted where ever an argument with name matching {@link #name} is placed
         */
        public NamedArgumentBinding(String name, Object value) {
            this.name = requireNonNull(name, "You must supply a name");
            this.value = value;
        }

        /**
         * Create a named argument binding:
         * <pre>
         *     bind("Hello {:firstName} {:lastName}.",
         *              arg("firstName", "Peter"),
         *              arg("lastName", "Hansen"))
         * </pre>
         * will return <code>"Hello Peter Hansen."</code>
         *
         * @param name  The name of the argument
         * @param value the value that will be inserted where ever an argument with name matching {@link #name} is placed
         * @return a {@link NamedArgumentBinding} instance
         */
        public static NamedArgumentBinding arg(String name, Object value) {
            return new NamedArgumentBinding(name, value);
        }

        @Override
        public String toString() {
            return ":" + name + " -> " + value;
        }
    }
}
