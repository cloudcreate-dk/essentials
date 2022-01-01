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

import dk.cloudcreate.essentials.shared.FailFast;

import java.math.*;

/**
 * Base class for all {@link SingleValueType}'s that encapsulate a {@link BigDecimal}.<br>
 * Example concrete implementation of the {@link BigDecimalType}:
 * <pre>{@code
 * public class Amount extends BigDecimalType<Amount> {
 *     public Amount(BigDecimal value) {
 *         super(value);
 *     }
 *
 *     public static Amount of(BigDecimal value) {
 *         return new Amount(value);
 *     }
 *
 *     public static Amount ofNullable(BigDecimal value) {
 *         return value != null ? new Amount(value) : null;
 *     }
 * }
 * }</pre>
 *
 * @param <CONCRETE_TYPE> The concrete {@link BigDecimalType} implementation
 */
@SuppressWarnings("unchecked")
public abstract class BigDecimalType<CONCRETE_TYPE extends BigDecimalType<CONCRETE_TYPE>> extends NumberType<BigDecimal, CONCRETE_TYPE> {

    public BigDecimalType(BigDecimal value) {
        super(value);
    }

    public CONCRETE_TYPE add(CONCRETE_TYPE augend, MathContext mc) {
        FailFast.requireNonNull(augend, "augend is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.add(augend.value, mc), this.getClass());
    }

    public CONCRETE_TYPE subtract(CONCRETE_TYPE subtrahend, MathContext mc) {
        FailFast.requireNonNull(subtrahend, "subtrahend is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.subtract(subtrahend.value, mc), this.getClass());
    }

    public CONCRETE_TYPE multiply(CONCRETE_TYPE multiplicand, MathContext mc) {
        FailFast.requireNonNull(multiplicand, "multiplicand is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.multiply(multiplicand.value, mc), this.getClass());
    }

    public CONCRETE_TYPE divide(CONCRETE_TYPE divisor, RoundingMode roundingMode) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divide(divisor.value, roundingMode), this.getClass());
    }

    public CONCRETE_TYPE divide(CONCRETE_TYPE divisor, MathContext mc) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divide(divisor.value, mc), this.getClass());
    }

    public CONCRETE_TYPE divideToIntegralValue(CONCRETE_TYPE divisor) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divideToIntegralValue(divisor.value), this.getClass());
    }

    public CONCRETE_TYPE divideToIntegralValue(CONCRETE_TYPE divisor, MathContext mc) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divideToIntegralValue(divisor.value, mc), this.getClass());
    }

    public CONCRETE_TYPE remainder(CONCRETE_TYPE divisor, MathContext mc) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.remainder(divisor.value, mc), this.getClass());
    }

    public CONCRETE_TYPE sqrt(MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.sqrt(mc), this.getClass());
    }

    public CONCRETE_TYPE pow(int n, MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.pow(n, mc), this.getClass());
    }

    public CONCRETE_TYPE abs(MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.abs(mc), this.getClass());
    }

    public CONCRETE_TYPE negate(MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.negate(mc), this.getClass());
    }

    public CONCRETE_TYPE plus(MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.plus(mc), this.getClass());
    }

    public BigInteger unscaledValue() {
        return value.unscaledValue();
    }

    public CONCRETE_TYPE setScale(int newScale, RoundingMode roundingMode) {
        return (CONCRETE_TYPE) SingleValueType.from(value.setScale(newScale, roundingMode), this.getClass());
    }

    public CONCRETE_TYPE setScale(int newScale) {
        return (CONCRETE_TYPE) SingleValueType.from(value.setScale(newScale), this.getClass());
    }

    public CONCRETE_TYPE movePointLeft(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.movePointLeft(n), this.getClass());
    }

    public CONCRETE_TYPE movePointRight(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.movePointRight(n), this.getClass());
    }

    public CONCRETE_TYPE scaleByPowerOfTen(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.scaleByPowerOfTen(n), this.getClass());
    }

    public CONCRETE_TYPE stripTrailingZeros() {
        return (CONCRETE_TYPE) SingleValueType.from(value.stripTrailingZeros(), this.getClass());
    }

    public CONCRETE_TYPE min(BigDecimal val) {
        return (CONCRETE_TYPE) SingleValueType.from(value.min(val), this.getClass());
    }

    public CONCRETE_TYPE max(BigDecimal val) {
        return (CONCRETE_TYPE) SingleValueType.from(value.max(val), this.getClass());
    }

    public String toEngineeringString() {
        return value.toEngineeringString();
    }

    public String toPlainString() {
        return value.toPlainString();
    }

    public BigInteger toBigInteger() {
        return value.toBigInteger();
    }

    public BigInteger toBigIntegerExact() {
        return value.toBigIntegerExact();
    }

    public long longValueExact() {
        return value.longValueExact();
    }

    public int intValueExact() {
        return value.intValueExact();
    }

    public short shortValueExact() {
        return value.shortValueExact();
    }

    public byte byteValueExact() {
        return value.byteValueExact();
    }

    public CONCRETE_TYPE ulp() {
        return (CONCRETE_TYPE) SingleValueType.from(value.ulp(), this.getClass());
    }

    public CONCRETE_TYPE add(CONCRETE_TYPE augend) {
        FailFast.requireNonNull(augend, "augend is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.add(augend.value), this.getClass());
    }

    public CONCRETE_TYPE subtract(CONCRETE_TYPE subtrahend) {
        FailFast.requireNonNull(subtrahend, "subtrahend is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.subtract(subtrahend.value), this.getClass());
    }

    public CONCRETE_TYPE multiply(CONCRETE_TYPE multiplicand) {
        FailFast.requireNonNull(multiplicand, "multiplicand is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.multiply(multiplicand.value), this.getClass());
    }

    public CONCRETE_TYPE divide(CONCRETE_TYPE divisor, int scale, RoundingMode roundingMode) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divide(divisor.value, scale, roundingMode), this.getClass());
    }

    public CONCRETE_TYPE divide(CONCRETE_TYPE divisor) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divide(divisor.value), this.getClass());
    }

    public CONCRETE_TYPE remainder(CONCRETE_TYPE divisor) {
        FailFast.requireNonNull(divisor, "divisor is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.remainder(divisor.value), this.getClass());
    }

    public CONCRETE_TYPE pow(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.pow(n), this.getClass());
    }

    public CONCRETE_TYPE abs() {
        return (CONCRETE_TYPE) SingleValueType.from(value.abs(), this.getClass());
    }

    public CONCRETE_TYPE negate() {
        return (CONCRETE_TYPE) SingleValueType.from(value.negate(), this.getClass());
    }

    public CONCRETE_TYPE plus() {
        return (CONCRETE_TYPE) SingleValueType.from(value.plus(), this.getClass());
    }

    public int signum() {
        return value.signum();
    }

    public int scale() {
        return value.scale();
    }

    public int precision() {
        return value.precision();
    }

    public CONCRETE_TYPE round(MathContext mc) {
        return (CONCRETE_TYPE) SingleValueType.from(value.round(mc), this.getClass());
    }

    @Override
    public int compareTo(CONCRETE_TYPE o) {
        return value.compareTo(o.value());
    }
}
