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

import java.math.BigInteger;

/**
 * Base class for all {@link SingleValueType}'s that encapsulate a {@link BigInteger}.<br>
 * Example concrete implementation of the {@link BigDecimalType}:
 * <pre>{@code
 * public class MyBigInteger extends BigIntegerType<MyBigInteger> {
 *     public MyBigInteger(BigInteger value) {
 *         super(value);
 *     }
 *
 *     public static MyBigInteger of(BigInteger value) {
 *         return new MyBigInteger(value);
 *     }
 *
 *     public static MyBigInteger ofNullable(BigInteger value) {
 *         return value != null ? new MyBigInteger(value) : null;
 *     }
 * }
 * }</pre>
 *
 * @param <CONCRETE_TYPE> The concrete {@link BigIntegerType} implementation
 */
@SuppressWarnings("unchecked")
public abstract class BigIntegerType<CONCRETE_TYPE extends BigIntegerType<CONCRETE_TYPE>> extends NumberType<BigInteger, CONCRETE_TYPE> {

    public BigIntegerType(BigInteger value) {
        super(value);
    }

    @Override
    public int compareTo(CONCRETE_TYPE o) {
        return value.compareTo(o.value());
    }

    public CONCRETE_TYPE nextProbablePrime() {
        return (CONCRETE_TYPE) SingleValueType.from(value.nextProbablePrime(), this.getClass());
    }

    public CONCRETE_TYPE add(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.add(val.value), this.getClass());
    }

    public CONCRETE_TYPE subtract(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.subtract(val.value), this.getClass());
    }

    public CONCRETE_TYPE multiply(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.multiply(val.value), this.getClass());
    }

    public CONCRETE_TYPE divide(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.divide(val.value), this.getClass());
    }

    public CONCRETE_TYPE remainder(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.remainder(val.value), this.getClass());
    }

    public CONCRETE_TYPE pow(int exponent) {
        return (CONCRETE_TYPE) SingleValueType.from(value.pow(exponent), this.getClass());
    }

    public CONCRETE_TYPE sqrt() {
        return (CONCRETE_TYPE) SingleValueType.from(value.sqrt(), this.getClass());
    }


    public CONCRETE_TYPE gcd(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.gcd(val.value), this.getClass());
    }

    public CONCRETE_TYPE abs() {
        return (CONCRETE_TYPE) SingleValueType.from(value.abs(), this.getClass());
    }

    public CONCRETE_TYPE negate() {
        return (CONCRETE_TYPE) SingleValueType.from(value.negate(), this.getClass());
    }

    public int signum() {
        return value.signum();
    }

    public CONCRETE_TYPE mod(CONCRETE_TYPE m) {
        FailFast.requireNonNull(m, "m is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.mod(m.value), this.getClass());
    }

    public CONCRETE_TYPE modPow(CONCRETE_TYPE exponent, CONCRETE_TYPE m) {
        FailFast.requireNonNull(exponent, "exponent is null");
        FailFast.requireNonNull(m, "m is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.modPow(exponent.value, m.value), this.getClass());
    }

    public CONCRETE_TYPE modInverse(CONCRETE_TYPE m) {
        FailFast.requireNonNull(m, "m is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.modInverse(m.value), this.getClass());
    }

    public CONCRETE_TYPE shiftLeft(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.shiftLeft(n), this.getClass());
    }

    public CONCRETE_TYPE shiftRight(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.shiftRight(n), this.getClass());
    }

    public CONCRETE_TYPE and(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.and(val.value), this.getClass());
    }

    public CONCRETE_TYPE or(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.or(val.value), this.getClass());
    }

    public CONCRETE_TYPE xor(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.xor(val.value), this.getClass());
    }

    public CONCRETE_TYPE not() {
        return (CONCRETE_TYPE) SingleValueType.from(value.not(), this.getClass());
    }

    public CONCRETE_TYPE andNot(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.andNot(val.value), this.getClass());
    }

    public boolean testBit(int n) {
        return value.testBit(n);
    }

    public CONCRETE_TYPE setBit(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.setBit(n), this.getClass());
    }

    public CONCRETE_TYPE clearBit(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.clearBit(n), this.getClass());
    }

    public CONCRETE_TYPE flipBit(int n) {
        return (CONCRETE_TYPE) SingleValueType.from(value.flipBit(n), this.getClass());
    }

    public int getLowestSetBit() {
        return value.getLowestSetBit();
    }

    public int bitLength() {
        return value.bitLength();
    }

    public int bitCount() {
        return value.bitCount();
    }

    public boolean isProbablePrime(int certainty) {
        return value.isProbablePrime(certainty);
    }

    public CONCRETE_TYPE min(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.min(val.value), this.getClass());
    }

    public CONCRETE_TYPE max(CONCRETE_TYPE val) {
        FailFast.requireNonNull(val, "val is null");
        return (CONCRETE_TYPE) SingleValueType.from(value.max(val.value), this.getClass());
    }

    public String toString(int radix) {
        return value.toString(radix);
    }

    public byte[] toByteArray() {
        return value.toByteArray();
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
}
