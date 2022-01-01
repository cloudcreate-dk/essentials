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

package dk.cloudcreate.essentials.shared.functional.tuple.comparable;

import dk.cloudcreate.essentials.shared.FailFast;
import dk.cloudcreate.essentials.shared.functional.tuple.Tuple;

import java.io.Serializable;
import java.util.*;

/**
 * Base interface for all {@link ComparableTuple}'s.<br>
 * A {@link ComparableTuple} is an immutable object that can contain the following (supported) number of elements:
 * <table>
 *     <tr><td>Number of element in Tuple</td><td>Concrete {@link ComparableTuple} type</td><td>Factory method</td></tr>
 *     <tr><td>0</td><td>{@link ComparableEmpty}</td><td>{@link ComparableTuple#empty()}</td></tr>
 *     <tr><td>1</td><td>{@link ComparableSingle}</td><td>{@link ComparableTuple#of(Comparable)}</td></tr>
 *     <tr><td>2</td><td>{@link ComparablePair}</td><td>{@link ComparableTuple#of(Comparable, Comparable)}</td></tr>
 *     <tr><td>3</td><td>{@link ComparableTriple}</td><td>{@link ComparableTuple#of(Comparable, Comparable, Comparable)}</td></tr>
 * </table>
 * <br>
 * <b>Note</b>: {@link ComparableTuple} (and its subclasses) supports {@link Object#equals(Object)} comparison using subclasses for the different subclasses
 *
 * @param <CONCRETE_TUPLE> the concrete {@link ComparableTuple} sub-type
 */
public interface ComparableTuple<CONCRETE_TUPLE extends ComparableTuple<CONCRETE_TUPLE>> extends Comparable<CONCRETE_TUPLE>, Serializable {
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
     * Create/Get and {@link ComparableEmpty} {@link ComparableTuple}
     *
     * @return an {@link ComparableEmpty} {@link Tuple}
     */
    static ComparableEmpty empty() {
        return ComparableEmpty.instance();
    }

    /**
     * Create/Get and {@link ComparableEmpty} {@link Tuple}
     *
     * @return an {@link ComparableEmpty} {@link Tuple}
     */
    static ComparableEmpty of() {
        return ComparableEmpty.instance();
    }

    /**
     * Create a new {@link ComparableTuple} with 1 element
     *
     * @param t1   the first element
     * @param <T1> the first element type
     * @return the newly created {@link ComparableSingle} tuple
     */
    static <T1 extends Comparable<? super T1>> ComparableSingle<T1> of(T1 t1) {
        return new ComparableSingle<>(t1);
    }

    /**
     * Create a new {@link ComparableTuple} with 2 elements
     *
     * @param t1   the first element
     * @param t2   the second element
     * @param <T1> the first element type
     * @param <T2> the second element type
     * @return the newly created {@link ComparablePair} tuple
     */
    static <T1 extends Comparable<? super T1>,
            T2 extends Comparable<? super T2>> ComparablePair<T1, T2> of(T1 t1, T2 t2) {
        return new ComparablePair<>(t1, t2);
    }

    /**
     * Create a new {@link ComparableTuple} with 2 elements from a {@link Map} {@link Map.Entry}
     *
     * @param entry the map entry
     * @param <T1>  the first element type
     * @param <T2>  the second element type
     * @return the newly created {@link ComparablePair} tuple
     */
    static <T1 extends Comparable<? super T1>,
            T2 extends Comparable<? super T2>> ComparablePair<T1, T2> fromEntry(Map.Entry<? extends T1, ? extends T2> entry) {
        FailFast.requireNonNull(entry, "You must supply an entry value");
        return new ComparablePair<>(entry.getKey(), entry.getValue());
    }

    /**
     * Create a new {@link ComparableTuple} with 3 elements
     *
     * @param t1   the first element
     * @param t2   the second element
     * @param t3   the third element
     * @param <T1> the first element type
     * @param <T2> the second element type
     * @param <T3> the third element type
     * @return the newly created {@link ComparableTriple} tuple
     */
    static <T1 extends Comparable<? super T1>,
            T2 extends Comparable<? super T2>,
            T3 extends Comparable<? super T3>> ComparableTriple<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new ComparableTriple<>(t1, t2, t3);
    }
}
