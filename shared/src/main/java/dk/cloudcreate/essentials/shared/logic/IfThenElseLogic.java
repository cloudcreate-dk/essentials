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

import java.util.function.Supplier;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Common code for {@link IfExpression} and {@link ElseIfExpression}
 *
 * @param <RETURN_TYPE> the return type from the {@link IfThenElseLogic}'s return value supplier lambda or the provided fixed value
 */
abstract class IfThenElseLogic<RETURN_TYPE> {
    private IfThenElseLogic<RETURN_TYPE> parent;
    private IfThenElseLogic<RETURN_TYPE> child;
    private IfPredicate                  ifPredicate;
    private Supplier<RETURN_TYPE>        ifReturnValueSupplier;

    /**
     * {@link ElseIfExpression} constructor
     *
     * @param parent the parent of the ElseIf expression
     */
    IfThenElseLogic(IfThenElseLogic<RETURN_TYPE> parent) {
        this.parent = requireNonNull(parent, "You must provide a parent IfTheElseLogic instance");
        parent.child = this;
    }

    /**
     * {@link IfExpression} constructor
     *
     * @param ifPredicate           the {@link IfExpression}'s {@link IfPredicate}
     * @param ifReturnValueSupplier the {@link IfExpression}'s return value supplier
     */
    IfThenElseLogic(IfPredicate ifPredicate, Supplier<RETURN_TYPE> ifReturnValueSupplier) {
        this.ifPredicate = requireNonNull(ifPredicate, "You must provide an If predicate lambda");
        this.ifReturnValueSupplier = requireNonNull(ifReturnValueSupplier, "You must provide a return value supplier lambda");
    }

    /**
     * Set the {@link ElseIfExpression}/Else's {@link IfPredicate} and return value supplier
     *
     * @param ifPredicate           the {@link IfPredicate}, which is evaluated to resolve if the {@link IfExpression} or {@link ElseIfExpression} is true
     *                              and hence the return value from the <code>ifReturnValueSupplier</code> must be returned
     * @param ifReturnValueSupplier the return value supplier
     */
    void set(IfPredicate ifPredicate, Supplier<RETURN_TYPE> ifReturnValueSupplier) {
        this.ifPredicate = requireNonNull(ifPredicate, "You must provide an If predicate lambda");
        this.ifReturnValueSupplier = requireNonNull(ifReturnValueSupplier, "You must provide a return value supplier lambda");
    }

    /**
     * Get the {@link IfExpression}, which is the root of the {@link IfThenElseLogic} hierarchy
     *
     * @return the {@link IfExpression}
     */
    IfThenElseLogic<RETURN_TYPE> getIfExpression() {
        var currentIfTheElse = this;
        while (currentIfTheElse.parent != null) {
            currentIfTheElse = currentIfTheElse.parent;
        }
        return currentIfTheElse;
    }

    /**
     * Get the child (if any) of this {@link IfThenElseLogic} element.<br>
     * The Else element in an If/(ElseIf)/Else sequence will have a child that's null.
     *
     * @return the child (if any) of this {@link IfThenElseLogic} element
     */
    protected IfThenElseLogic<RETURN_TYPE> getChild() {
        return child;
    }

    /**
     * Evaluate the {@link IfPredicate}
     *
     * @return the result of {@link IfPredicate#test()}
     */
    protected boolean evaluate() {
        requireNonNull(ifPredicate, this.getClass().getName() + " doesn't have an if predicate");
        return ifPredicate.test();
    }

    /**
     * Resolve the Return Value
     *
     * @return the result of the return-value {@link Supplier#get()}
     */
    protected RETURN_TYPE resolveReturnValue() {
        return ifReturnValueSupplier.get();
    }
}
