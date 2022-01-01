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

import dk.cloudcreate.essentials.shared.FailFast;

import java.io.Serializable;
import java.util.*;

/**
 * Base interface for all {@link Tuple}'s.<br>
 * A {@link Tuple} is an immutable object that can contain the following (supported) number of elements:
 * <table>
 *     <tr><td>Number of element in Tuple</td><td>Concrete {@link Tuple} type</td><td>Factory method</td></tr>
 *     <tr><td>0</td><td>{@link Empty}</td><td>{@link Tuple#empty()}</td></tr>
 *     <tr><td>1</td><td>{@link Single}</td><td>{@link Tuple#of(Object)}</td></tr>
 *     <tr><td>2</td><td>{@link Pair}</td><td>{@link Tuple#of(Object, Object)}</td></tr>
 *     <tr><td>3</td><td>{@link Triple}</td><td>{@link Tuple#of(Object, Object, Object)}</td></tr>
 * </table>
 * <br>
 * <b>Note</b>: {@link Tuple} (and its subclasses) supports {@link Object#equals(Object)} comparison using subclasses for the different subclasses
 *
 * @param <CONCRETE_TUPLE> the concrete {@link Tuple} sub-type
 */
public interface Tuple<CONCRETE_TUPLE extends Tuple<CONCRETE_TUPLE>> extends Serializable {
    /**
     * Number of arguments/elements in the Tuple
     *
     * @return Number of arguments/elements in the Tuple
     */
    int arity();

    /**
     * Convert the Tuple to a list
     *
     * @return list of all Tuple elements
     */
    List<?> toList();

    /**
     * Create/Get and {@link Empty} {@link Tuple}
     *
     * @return an {@link Empty} {@link Tuple}
     */
    static Empty empty() {
        return Empty.instance();
    }

    /**
     * Create/Get and {@link Empty} {@link Tuple}
     *
     * @return an {@link Empty} {@link Tuple}
     */
    static Empty of() {
        return Empty.instance();
    }

    /**
     * Create a new {@link Tuple} with 1 element
     *
     * @param t1   the first element
     * @param <T1> the first element type
     * @return the newly created {@link Single} tuple
     */
    static <T1> Single<T1> of(T1 t1) {
        return new Single<>(t1);
    }

    /**
     * Create a new {@link Tuple} with 2 elements
     *
     * @param t1   the first element
     * @param t2   the second element
     * @param <T1> the first element type
     * @param <T2> the second element type
     * @return the newly created {@link Pair} tuple
     */
    static <T1,
            T2> Pair<T1, T2> of(T1 t1, T2 t2) {
        return new Pair<>(t1, t2);
    }

    /**
     * Create a new {@link Tuple} with 2 elements from a {@link Map} {@link java.util.Map.Entry}
     *
     * @param entry the map entry
     * @param <T1>  the first element type
     * @param <T2>  the second element type
     * @return the newly created {@link Pair} tuple
     */
    static <T1,
            T2> Pair<T1, T2> fromEntry(Map.Entry<? extends T1, ? extends T2> entry) {
        FailFast.requireNonNull(entry, "You must supply an entry value");
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    /**
     * Create a new {@link Tuple} with 3 elements
     *
     * @param t1   the first element
     * @param t2   the second element
     * @param t3   the third element
     * @param <T1> the first element type
     * @param <T2> the second element type
     * @param <T3> the third element type
     * @return the newly created {@link Triple} tuple
     */
    static <T1,
            T2,
            T3> Triple<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Triple<>(t1, t2, t3);
    }
}
