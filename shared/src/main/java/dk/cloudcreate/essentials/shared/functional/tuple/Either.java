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
 * Represents a {@link Tuple} with two potential elements, but where only one element can have a value
 * at a time<br>
 * This is used to represent a <b>choice</b> type that can have two different values, but only one value at a time.<br>
 * The value can <b>either</b> be {@link #_1()} OR {@link #_2()}<br>
 * <br>
 * Use {@link #Either(Object, Object)} or {@link #of_1(Object)}/{@link #of_2(Object)} to create
 * a new {@link Either} instance<br>
 * <br>
 * Use {@link #is_1()} or {@link #is_2()} to check which value is non-null
 * and {@link #_1()} or {@link #_2()} to get the value of the element.<br>
 * <br>
 * Conditional logic can be applied using {@link #ifIs_1(Consumer)} or {@link #ifIs_2(Consumer)}
 *
 * @param <T1> the first element type
 * @param <T2> the second element type
 */
public class Either<T1, T2> implements Tuple<Either<T1, T2>> {
    /**
     * The potential first element in this tuple
     */
    public final T1 _1;
    /**
     * The potential second element in this tuple
     */
    public final T2 _2;

    /**
     * Construct a new {@link Tuple} with 2 potential elements, but where only one of
     * them can have a non-null value and one element MUST have a non-null value.
     *
     * @param t1 the first element
     * @param t2 the second element
     * @throws IllegalArgumentException if both elements has a non-null value or if both elements are null
     */
    public Either(T1 t1, T2 t2) {
        if (t1 != null && t2 != null) {
            throw new IllegalArgumentException("Only one element can have a non-null value. Both t1 and t2 were non-null");
        }
        if (t1 == null && t2 == null) {
            throw new IllegalArgumentException("One element MUST have a non-null value. Both t1 and t2 were null");
        }
        this._1 = t1;
        this._2 = t2;
    }

    /**
     * Construct a new {@link Tuple} with {@link #_1()} element having a value
     *
     * @param t1 the {@link #_1()} element ({@link #_2()} will have value null)
     */
    public static <T1, T2> Either<T1, T2> of_1(T1 t1) {
        return new Either<>(requireNonNull(t1, "No t1 value provided"), null);
    }

    /**
     * Construct a new {@link Tuple} with {@link #_2()} element having a value
     *
     * @param t2 the {@link #_2()} element ( {@link #_1()} will have value null)
     */
    public static <T1, T2> Either<T1, T2> of_2(T2 t2) {
        return new Either<>(null, requireNonNull(t2, "No t2 value provided"));
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public List<?> toList() {
        if (_1 != null) {
            return List.of(_1);
        } else {
            return List.of(_2);
        }
    }

    /**
     * The first element in this tuple (can be null)
     *
     * @return The first element in this tuple (can be null)
     * @see #get_1()
     * @see #is_1()
     * @see #ifIs_1(Consumer)
     */
    public T1 _1() {
        return _1;
    }

    /**
     * The first element in this tuple wrapped as an {@link Optional}
     *
     * @return The first element in this tuple wrapped as an {@link Optional}
     * @see #_1()
     * @see #is_1()
     * @see #ifIs_1(Consumer)
     */
    public Optional<T1> get_1() {
        return Optional.ofNullable(_1);
    }

    /**
     * Does first element in this tuple have a non-null value
     *
     * @return true if the first element in this tuple has a non-null value
     * @see #ifIs_1(Consumer)
     * @see #get_1()
     */
    public boolean is_1() {
        return _1 != null;
    }

    /**
     * If {@link #is_1()} returns true, then provide the value of {@link #_1()}
     * to the supplied <code>consumer</code>
     *
     * @param consumer the consumer that will be provided the value of  the {@link #_1()}
     *                 if {@link #is_1()} returns true
     * @see #get_1()
     */
    public void ifIs_1(Consumer<T1> consumer) {
        requireNonNull(consumer, "No consumer provided");
        if (is_1()) {
            consumer.accept(_1);
        }
    }

    /**
     * The second element in this tuple (can be null)
     *
     * @return The second element in this tuple (can be null)
     * @see #get_2()
     * @see #is_2()
     * @see #ifIs_2(Consumer)
     */
    public T2 _2() {
        return _2;
    }

    /**
     * The second element in this tuple wrapped as an {@link Optional}
     *
     * @return The second element in this tuple wrapped as an {@link Optional}
     * @see #_2()
     * @see #is_2()
     * @see #ifIs_2(Consumer)
     */
    public Optional<T2> get_2() {
        return Optional.ofNullable(_2);
    }


    /**
     * Does second element in this tuple have a non-null value
     *
     * @return true if the second element in this tuple has a non-null value
     * @see #ifIs_2(Consumer)
     * @see #get_2()
     */
    public boolean is_2() {
        return _2 != null;
    }

    /**
     * If {@link #is_2()} returns true, then provide the value of {@link #_2()}
     * to the supplied <code>consumer</code>
     *
     * @param consumer the consumer that will be provided the value of the {@link #_2()}
     *                 if {@link #is_2()} returns true
     * @see #get_2()
     */
    public void ifIs_2(Consumer<T2> consumer) {
        requireNonNull(consumer, "No consumer provided");
        if (is_2()) {
            consumer.accept(_2);
        }
    }

    /**
     * Swap the elements of this {@link Either}
     *
     * @return A new {@link Either} where the first element is the second element of this {@link Either} AND
     * where the second element is the first element of this {@link Either}
     */
    public Either<T2, T1> swap() {
        return new Either<>(_2, _1);
    }

    /**
     * Maps the elements of this {@link Either} using the mapping function
     *
     * @param mappingFunction the mapping function
     * @param <R1>            result type for first element of the {@link Either} after applying the mapping function
     * @param <R2>            result type for second element of the {@link Either} after applying the mapping function
     * @return a new {@link Either} with the result of applying the mapping function to this {@link Either}
     */
    public <R1, R2> Either<R1, R2> map(BiFunction<? super T1, ? super T2, Either<R1, R2>> mappingFunction) {
        requireNonNull(mappingFunction, "You must supply a mapping function");
        return mappingFunction.apply(_1, _2);
    }

    /**
     * Maps the elements of this {@link Either} using two distinct mapping functions
     *
     * @param mappingFunction1 the mapping function for element number 1
     * @param mappingFunction2 the mapping function for element number 2
     * @param <R1>             result type for first element of the {@link Either} after applying the mapping function
     * @param <R2>             result type for second element of the {@link Either} after applying the mapping function
     * @return a new {@link Either} with the result of applying the mapping function to this {@link Either}
     */
    @SuppressWarnings("unchecked")
    public <R1, R2> Either<R1, R2> map(Function<? super T1, ? super R1> mappingFunction1,
                                       Function<? super T2, ? super R2> mappingFunction2) {
        requireNonNull(mappingFunction1, "You must supply a mappingFunction1");
        requireNonNull(mappingFunction2, "You must supply a mappingFunction2");
        R1 r1 = (R1) mappingFunction1.apply(_1);
        R2 r2 = (R2) mappingFunction2.apply(_2);
        return new Either<>(r1, r2);
    }

    /**
     * Map the <b>first</b> element of this {@link Either} using the mapping function
     *
     * @param mappingFunction1 the mapping function for element number 1
     * @param <R1>             result type for first element of the {@link Either} after applying the mapping function
     * @return a new {@link Either} with the result of applying the mapping function to the first element of this {@link Either}
     */
    @SuppressWarnings("unchecked")
    public <R1> Either<R1, T2> map1(Function<? super T1, ? super R1> mappingFunction1) {
        requireNonNull(mappingFunction1, "You must supply a mappingFunction1");
        R1 r1 = (R1) mappingFunction1.apply(_1);
        return new Either<>(r1, _2);
    }

    /**
     * Map the <b>second</b> element of this {@link Either} using the mapping function
     *
     * @param mappingFunction2 the mapping function for element number 2
     * @param <R2>             result type for second element of the {@link Either} after applying the mapping function
     * @return a new {@link Either} with the result of applying the mapping function to the second element of this {@link Either}
     */
    @SuppressWarnings("unchecked")
    public <R2> Either<T1, R2> map2(Function<? super T2, ? super R2> mappingFunction2) {
        requireNonNull(mappingFunction2, "You must supply a mappingFunction2");
        R2 r2 = (R2) mappingFunction2.apply(_2);
        return new Either<>(_1, r2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Either)) return false;
        var pair = (Either<?, ?>) o;
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
