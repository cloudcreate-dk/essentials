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

package dk.cloudcreate.essentials.shared.functional.tuple;

import java.util.*;
import java.util.function.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Represents a {@link Tuple} with two elements.<br>
 * <b>Note</b>: {@link Pair} supports {@link #equals(Object)} comparison using subclasses, e.g.:
 * <pre>{@code
 *     public class LeftAndRightSide extends Pair<String, String> {
 *
 *         public LeftAndRightSide(String leftSide, String rightSide) {
 *             super(leftSide, rightSide);
 *         }
 *     }
 * }</pre>
 *
 * @param <T1> the first element type
 * @param <T2> the second element type
 */
public class Pair<T1, T2> implements Tuple<Pair<T1, T2>> {
    /**
     * The first element in this tuple
     */
    public final T1 _1;
    /**
     * The second element in this tuple
     */
    public final T2 _2;

    /**
     * Construct a new {@link Tuple} with 2 elements
     *
     * @param t1 the first element
     * @param t2 the second element
     */
    public Pair(T1 t1, T2 t2) {
        this._1 = t1;
        this._2 = t2;
    }

    /**
     * Create a new {@link Tuple} with 2 elements
     *
     * @param t1 the first element
     * @param t2 the second element
     */
    public static <T1, T2> Pair<T1, T2> of(T1 t1, T2 t2) {
        return new Pair<>(t1, t2);
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public List<?> toList() {
        return List.of(_1, _2);
    }

    /**
     * The first element in this tuple
     *
     * @return The first element in this tuple
     */
    public T1 _1() {
        return _1;
    }

    /**
     * The second element in this tuple
     *
     * @return The second element in this tuple
     */
    public T2 _2() {
        return _2;
    }

    /**
     * Swap the elements of this {@link Pair}
     *
     * @return A new {@link Pair} where the first element is the second element of this {@link Pair} AND
     * where the second element is the first element of this {@link Pair}
     */
    public Pair<T2, T1> swap() {
        return Tuple.of(_2, _1);
    }

    /**
     * Converts the {@link Pair} to a {@link Map} {@link java.util.Map.Entry}
     *
     * @return a {@link java.util.Map.Entry} where the <b>key</b> is the first element of this {@link Pair}
     * and the <b>value</b> is the second element of this {@link Pair}
     */
    public Map.Entry<T1, T2> toEntry() {
        return new AbstractMap.SimpleEntry<>(_1, _2);
    }

    /**
     * Maps the elements of this {@link Pair} using the mapping function
     *
     * @param mappingFunction the mapping function
     * @param <R1>            result type for first element of the {@link Pair} after applying the mapping function
     * @param <R2>            result type for second element of the {@link Pair} after applying the mapping function
     * @return a new {@link Pair} with the result of applying the mapping function to this {@link Pair}
     */
    public <R1, R2> Pair<R1, R2> map(BiFunction<? super T1, ? super T2, Pair<R1, R2>> mappingFunction) {
        requireNonNull(mappingFunction, "You must supply a mapping function");
        return mappingFunction.apply(_1, _2);
    }

    /**
     * Maps the elements of this {@link Pair} using two distinct mapping functions
     *
     * @param mappingFunction1 the mapping function for element number 1
     * @param mappingFunction2 the mapping function for element number 2
     * @param <R1>             result type for first element of the {@link Pair} after applying the mapping function
     * @param <R2>             result type for second element of the {@link Pair} after applying the mapping function
     * @return a new {@link Pair} with the result of applying the mapping function to this {@link Pair}
     */
    @SuppressWarnings("unchecked")
    public <R1, R2> Pair<R1, R2> map(Function<? super T1, ? super R1> mappingFunction1,
                                     Function<? super T2, ? super R2> mappingFunction2) {
        requireNonNull(mappingFunction1, "You must supply a mappingFunction1");
        requireNonNull(mappingFunction2, "You must supply a mappingFunction2");
        R1 r1 = (R1) mappingFunction1.apply(_1);
        R2 r2 = (R2) mappingFunction2.apply(_2);
        return Tuple.of(r1,
                        r2);
    }

    /**
     * Map the <b>first</b> element of this {@link Pair} using the mapping function
     *
     * @param mappingFunction1 the mapping function for element number 1
     * @param <R1>             result type for first element of the {@link Pair} after applying the mapping function
     * @return a new {@link Pair} with the result of applying the mapping function to the first element of this {@link Pair}
     */
    @SuppressWarnings("unchecked")
    public <R1> Pair<R1, T2> map1(Function<? super T1, ? super R1> mappingFunction1) {
        requireNonNull(mappingFunction1, "You must supply a mappingFunction1");
        R1 r1 = (R1) mappingFunction1.apply(_1);
        return Tuple.of(r1,
                        _2);
    }

    /**
     * Map the <b>second</b> element of this {@link Pair} using the mapping function
     *
     * @param mappingFunction2 the mapping function for element number 2
     * @param <R2>             result type for second element of the {@link Pair} after applying the mapping function
     * @return a new {@link Pair} with the result of applying the mapping function to the second element of this {@link Pair}
     */
    @SuppressWarnings("unchecked")
    public <R2> Pair<T1, R2> map2(Function<? super T2, ? super R2> mappingFunction2) {
        requireNonNull(mappingFunction2, "You must supply a mappingFunction2");
        R2 r2 = (R2) mappingFunction2.apply(_2);
        return Tuple.of(_1,
                        r2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        var pair = (Pair<?, ?>) o;
        return Objects.equals(_1, pair._1) && Objects.equals(_2, pair._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }
}
