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

/**
 * An if <b>expression</b> is an {@link IfExpression#If(boolean, Object)} and {@link ElseIfExpression#Else(Object)},
 * with multiple optional intermediate {@link ElseIfExpression#ElseIf(boolean, Object)}'s, which
 * <b>returns a value of the evaluation of the if expression</b>:<br>
 * <pre>{@code
 * import static dk.cloudcreate.essentials.shared.logic.IfExpression.If;
 *
 * int value = getValue();
 * String description = If(value < 0, "Negative number").
 *                      Else("Zero or Positive number");
 * }</pre>
 * <p>
 * This is similar to the Java ternary operation:
 * <pre>{@code String description = value < 0 ? "Negative number" : "Zero or Positive Number";}</pre>
 * with the different that the <u>If expression</u> also can contain <b>Else-If</b> combinations:
 * <pre>{@code
 * import static dk.cloudcreate.essentials.shared.logic.IfExpression.If;
 *
 * int value = getValue();
 * String description = If(value < 0, "Negative number").
 *                      ElseIf(value == 0, "Zero").
 *                      Else("Positive number");
 * }</pre>
 * In these examples we've used simple predicate expressions and fixed return values<br>
 * However the <u>If expression</u> also supports using Lambda expressions, both for the {@link IfPredicate} and for the return value.<br>
 * Using this has the advantage of only evaluating the {@link IfPredicate} and the return value {@link Supplier} IF necessary, which for calculations, DB lookups or other IO intensive operation
 * will yield a much better result:
 *
 * <pre>{@code
 * import static dk.cloudcreate.essentials.shared.logic.IfExpression.If;
 *
 * OrderId orderId = ...;
 * Amount orderAmount = ...;
 *
 * var orderProcessingResult = If(() -> orderAmountExceedsAccountThreshold(orderAmount),
 *                                () -> cancelOrder(orderId)).
 *                             Else(() -> acceptOrder(orderId));
 * }</pre>
 *
 * @param <RETURN_TYPE> the return type from the {@link IfThenElseLogic}'s return value supplier lambda or the provided fixed value
 */
public final class IfExpression<RETURN_TYPE> extends IfThenElseLogic<RETURN_TYPE> {

    IfExpression(IfPredicate ifPredicate, Supplier<RETURN_TYPE> ifReturnValueSupplier) {
        super(ifPredicate,
              ifReturnValueSupplier);
    }

    public static <RETURN_TYPE> ElseIfExpression<RETURN_TYPE> If(boolean ifPredicate, Supplier<RETURN_TYPE> ifReturnValueSupplier) {
        return new ElseIfExpression<>(new IfExpression<>(() -> ifPredicate, ifReturnValueSupplier));
    }

    public static <RETURN_TYPE> ElseIfExpression<RETURN_TYPE> If(IfPredicate ifPredicate, Supplier<RETURN_TYPE> ifReturnValueSupplier) {
        return new ElseIfExpression<>(new IfExpression<>(ifPredicate, ifReturnValueSupplier));
    }

    public static <RETURN_TYPE> ElseIfExpression<RETURN_TYPE> If(boolean ifPredicate, RETURN_TYPE ifFixedValue) {
        return new ElseIfExpression<>(new IfExpression<>(() -> ifPredicate, () -> ifFixedValue));
    }

    public static <RETURN_TYPE> ElseIfExpression<RETURN_TYPE> If(IfPredicate ifPredicate, RETURN_TYPE ifFixedValue) {
        return new ElseIfExpression<>(new IfExpression<>(ifPredicate, () -> ifFixedValue));
    }
}
