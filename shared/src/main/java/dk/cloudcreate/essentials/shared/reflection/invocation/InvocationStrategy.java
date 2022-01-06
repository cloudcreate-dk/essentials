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
 * Determines which among as set of matching methods are invoked
 */
public enum InvocationStrategy {
    /**
     * If multiple methods match, then the method with the most specific type (in a type hierarchy) will be chosen as
     * only a SINGLE method will be invoked.<p>
     * Example: Say we have a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
     * Let's say the <code>argument</code> object is an instance of <code>OrderAccepted</code> and we're matching against the following methods
     * <pre>
     *      {@literal @EventHandler}
     *       private void method1(OrderEvent t) { ... }
     *
     *      {@literal @EventHandler}
     *       private void method2(OrderShipped t) { ... }
     *
     *      {@literal @EventHandler}
     *       private void method3(OrderAccepted t) { ... }
     *     </pre>
     * <p>
     * In this case both <code>method1</code> and <code>method3</code> match on type of the argument instance (<code>OrderAccepted</code>), because <code>OrderEvent</code> (of <code>method1</code>) match by hierarchy,
     * and <code>OrderAccepted</code> (of <code>method3</code>) match directly by type.<br>
     * Using <b>{@link InvocationStrategy#InvokeMostSpecificTypeMatched}</b> then only <code>method3</code> will be called, since <code>OrderAccepted</code> is a more specific type than the more generic type <code>OrderEvent</code>
     */
    InvokeMostSpecificTypeMatched,
    /**
     * If multiple methods match, then all matching methods will be invoked in sequence (notice: the order of the methods called is not deterministic)<p>
     * Example: Say we have a single class we have placed a set methods that can handle <code>OrderEvent</code>'s, such as <code>OrderCreated, OrderShipped, OrderAccepted</code>, etc.<br>
     * Let's say the <code>argument</code> object is an instance of <code>OrderAccepted</code> and we're matching against the following methods
     * <pre>
     *      {@literal @EventHandler}
     *       private void method1(OrderEvent t) { ... }
     *
     *      {@literal @EventHandler}
     *       private void method2(OrderShipped t) { ... }
     *
     *      {@literal @EventHandler}
     *       private void method3(OrderAccepted t) { ... }
     *     </pre>
     * <p>
     * In this case both <code>method1</code> and <code>method3</code> match on type of the argument instance (<code>OrderAccepted</code>), because <code>OrderEvent</code> (of <code>method1</code>) match by hierarchy,
     * and <code>OrderAccepted</code> (of <code>method3</code>) match directly by type.<br>
     * Using {@link InvocationStrategy#InvokeAllMatches}</b> then BOTH <code>method1</code> and <code>method3</code> will be called (notice: the order in which the methods will be called is not deterministic)
     */
    InvokeAllMatches
}
