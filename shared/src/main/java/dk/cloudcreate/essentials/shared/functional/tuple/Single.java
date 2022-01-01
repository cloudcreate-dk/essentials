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
import java.util.function.Function;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Represents a {@link Tuple} with one element.<br>
 * <b>Note</b>: {@link Single} supports {@link #equals(Object)} comparison using subclasses, e.g.:
 * <pre>{@code
 * public class Option extends Single<String> {
 *     public Option(String optionalValue) {
 *       super(optionalValue);
 *     }
 * }
 * }</pre>
 *
 * @param <T1> the first element type
 */
public class Single<T1> implements Tuple<Single<T1>> {
    /**
     * The first element in this tuple
     */
    public final T1 _1;

    /**
     * Create a new {@link Tuple} with 1 element
     *
     * @param t1 the first element
     */
    public Single(T1 t1) {
        this._1 = t1;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public List<?> toList() {
        return List.of(_1);
    }

    /**
     * Returns the first element in this tuple
     *
     * @return the first element in this tuple
     */
    public T1 _1() {
        return _1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Single)) return false;
        var single = (Single<?>) o;
        return Objects.equals(_1, single._1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1);
    }

    /**
     * Maps the element of this {@link Single} using the mapping function
     *
     * @param mappingFunction the mapping function
     * @param <R1>            result type for the mapping function
     * @return a new {@link Single} with the result of applying the mapping function to this {@link Single}
     */
    public <R1> Single<R1> map(Function<? super T1, ? extends R1> mappingFunction) {
        requireNonNull(mappingFunction, "You must supply a mapping function");
        return Tuple.of(mappingFunction.apply(_1));
    }

    @Override
    public String toString() {
        return "(" + _1 + ")";
    }
}
