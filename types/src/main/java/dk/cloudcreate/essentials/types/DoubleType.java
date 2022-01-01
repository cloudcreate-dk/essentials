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

package dk.cloudcreate.essentials.types;

/**
 * Base class for all {@link SingleValueType}'s that encapsulate a {@link Double}.<br>
 * Example concrete implementation of the {@link DoubleType}:
 * <pre>{@code
 * public class Amount extends DoubleType<Amount> {
 *     public Amount(double value) {
 *         super(value);
 *     }
 *
 *     public static Amount of(double value) {
 *         return new Amount(value);
 *     }
 *
 *     public static Amount ofNullable(Double value) {
 *         return value != null ? new Amount(value) : null;
 *     }
 * }
 * }</pre>
 *
 * @param <CONCRETE_TYPE> The concrete {@link DoubleType} implementation
 */
public abstract class DoubleType<CONCRETE_TYPE extends DoubleType<CONCRETE_TYPE>> extends NumberType<Double, CONCRETE_TYPE> {

    public DoubleType(Double value) {
        super(value);
    }

    @Override
    public int compareTo(CONCRETE_TYPE o) {
        return value.compareTo(o.doubleValue());
    }

    public boolean isNaN() {
        return value.isNaN();
    }

    public boolean isInfinite() {
        return value.isInfinite();
    }
}
