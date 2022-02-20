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
 * The ElseIf/Else part of an {@link IfExpression}
 *
 * @param <RETURN_TYPE> the return type from the {@link IfThenElseLogic}'s return value supplier lambda or the provided fixed value
 */
public final class ElseIfExpression<RETURN_TYPE> extends IfThenElseLogic<RETURN_TYPE> {
    ElseIfExpression(IfExpression<RETURN_TYPE> If) {
        super(If);
    }

    ElseIfExpression(ElseIfExpression<RETURN_TYPE> elseIfParent) {
        super(elseIfParent);
    }

    public ElseIfExpression<RETURN_TYPE> ElseIf(boolean elseIfPredicate,
                                                Supplier<RETURN_TYPE> elseIfReturnValueSupplier) {
        set(() -> elseIfPredicate,
            elseIfReturnValueSupplier);
        return new ElseIfExpression<>(this);
    }

    public ElseIfExpression<RETURN_TYPE> ElseIf(IfPredicate elseIfPredicate,
                                                Supplier<RETURN_TYPE> elseIfReturnValueSupplier) {
        set(elseIfPredicate,
            elseIfReturnValueSupplier);
        return new ElseIfExpression<>(this);
    }

    public ElseIfExpression<RETURN_TYPE> ElseIf(boolean elseIfPredicate,
                                                RETURN_TYPE elseIfFixedValue) {
        set(() -> elseIfPredicate,
            () -> elseIfFixedValue);
        return new ElseIfExpression<>(this);
    }

    public ElseIfExpression<RETURN_TYPE> ElseIf(IfPredicate elseIfPredicate,
                                                RETURN_TYPE elseIfFixedValue) {
        set(elseIfPredicate,
            () -> elseIfFixedValue);
        return new ElseIfExpression<>(this);
    }

    public RETURN_TYPE Else(RETURN_TYPE elseFixedValue) {
        return Else(() -> elseFixedValue);
    }

    public RETURN_TYPE Else(Supplier<RETURN_TYPE> elseReturnValueSupplier) {
        requireNonNull(elseReturnValueSupplier, "You must provide an Else return value Supplier");

        set(() -> true,
            elseReturnValueSupplier);
        var currentIfThenElse = getIfExpression();
        while (currentIfThenElse != null) {
            if (currentIfThenElse.evaluate()) {
                return currentIfThenElse.resolveReturnValue();
            }
            currentIfThenElse = currentIfThenElse.getChild();
        }

        // Should never happen
        throw new IllegalStateException("Else's evaluate didn't return true");
    }
}
