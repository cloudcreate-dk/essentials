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

package dk.cloudcreate.essentials.shared.reflection.invocation;

/**
 * Consumer that will be called if {@link PatternMatchingMethodInvoker#invoke(Object)} or {@link PatternMatchingMethodInvoker#invoke(Object, NoMatchingMethodsHandler)} is called with an argument that doesn't match any methods
 */
@FunctionalInterface
public interface NoMatchingMethodsHandler {
    /**
     * This method will be called if {@link PatternMatchingMethodInvoker#invoke(Object)} or {@link PatternMatchingMethodInvoker#invoke(Object, NoMatchingMethodsHandler)} is called with an argument that doesn't match any methods
     *
     * @param argument the argument provided to {@link PatternMatchingMethodInvoker#invoke(Object)} or {@link PatternMatchingMethodInvoker#invoke(Object, NoMatchingMethodsHandler)} which didn't result in any matching methods
     */
    void noMatchesFor(Object argument);
}
