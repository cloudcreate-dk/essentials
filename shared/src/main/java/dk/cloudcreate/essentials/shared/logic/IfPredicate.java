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

package dk.cloudcreate.essentials.shared.logic;

/**
 * An {@link IfPredicate} is evaluated to resolve if an {@link IfExpression} or {@link ElseIfExpression} element is true
 * and hence the return value from the <code>ifReturnValueSupplier</code> must be returned
 */
@FunctionalInterface
public interface IfPredicate {
    /**
     * Evaluate the if predicate
     * @return the result of the if predicate evaluation. If it returns true, then the given {@link IfExpression} or {@link ElseIfExpression}
     * will have the supplied <code>ifReturnValueSupplier</code> called and its returned value returned from the {@link IfExpression}
     */
    boolean test();
}
