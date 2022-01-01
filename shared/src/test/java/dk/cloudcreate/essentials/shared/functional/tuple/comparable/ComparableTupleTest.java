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

import dk.cloudcreate.essentials.shared.functional.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ComparableTupleTest {

    @Test
    void test_ComparableEmpty() {
        // Equals
        assertThat(ComparableTuple.empty()).isEqualTo(ComparableEmpty.instance());
        assertThat(ComparableTuple.empty()).isEqualTo(ComparableTuple.of());
        assertThat(ComparableTuple.empty()).isEqualTo(ComparableEmpty.INSTANCE);

        // HashCode
        assertThat(ComparableTuple.empty().hashCode()).isEqualTo(ComparableEmpty.instance().hashCode());
        assertThat(ComparableTuple.empty().hashCode()).isEqualTo(ComparableEmpty.INSTANCE.hashCode());

        // Compare To
        assertThat(ComparableTuple.empty().compareTo(ComparableEmpty.INSTANCE)).isEqualTo(0);

        // ToString
        assertThat(ComparableTuple.empty().toString()).isEqualTo("Empty");
    }

    @Test
    void test_ComparableSingle() {
        // Element accessors
        assertThat(ComparableTuple.of("Test")._1).isEqualTo("Test");
        assertThat(ComparableTuple.of("Test")._1()).isEqualTo("Test");

        // Equals
        assertThat(ComparableTuple.of("Test")).isEqualTo(new ComparableSingle<>("Test"));
        assertThat(ComparableTuple.of("Test")).isEqualTo(new Option("Test"));
        assertThat(ComparableTuple.of("Test")).isNotEqualTo(ComparableTuple.of("Test2"));

        // HashCode
        assertThat(ComparableTuple.of("Test").hashCode()).isEqualTo(new ComparableSingle<>("Test").hashCode());
        assertThat(ComparableTuple.of("Test").hashCode()).isNotEqualTo(ComparableTuple.of("Test2").hashCode());

        // Compare To
        assertThat(ComparableTuple.of("Test").compareTo(new ComparableSingle<>("Test"))).isEqualTo(0);
        assertThat(ComparableTuple.of("Test").compareTo(ComparableTuple.of("T"))).isGreaterThan(0);
        assertThat(ComparableTuple.of("Test").compareTo(ComparableTuple.of("Test2"))).isLessThan(0);

        // ToString
        assertThat(ComparableTuple.of("Test").toString()).isEqualTo("(Test)");

        // Map
        assertThat(ComparableTuple.of("Test").map(String::toUpperCase)).isEqualTo(ComparableTuple.of("TEST"));

        // Arity
        assertThat(ComparableTuple.of("Test").arity()).isEqualTo(1);

        // ToList
        assertThat(ComparableTuple.of("Test").toList()).isEqualTo(List.of("Test"));
    }

    @Test
    void test_ComparablePair() {
        // Element accessors
        assertThat(ComparableTuple.of("Hello", "World")._1).isEqualTo("Hello");
        assertThat(ComparableTuple.of("Hello", "World")._2).isEqualTo("World");
        assertThat(ComparableTuple.of("Hello", "World")._1()).isEqualTo("Hello");
        assertThat(ComparableTuple.of("Hello", "World")._2()).isEqualTo("World");

        // Equals
        assertThat(ComparableTuple.of("Hello", "World")).isEqualTo(new ComparablePair<>("Hello", "World"));
        assertThat(ComparableTuple.of("Hello", "World")).isEqualTo(new LeftAndRightSide("Hello", "World"));
        assertThat(ComparableTuple.of("Hello", "World")).isNotEqualTo(ComparableTuple.of("Hello", "world"));
        assertThat(ComparableTuple.of("Hello", "World")).isNotEqualTo(ComparableTuple.of("hello", "World"));
        assertThat(ComparableTuple.of("Hello", "World")).isNotEqualTo(ComparableTuple.of("hello"));
        assertThat(ComparableTuple.of("Hi", "World")).isNotEqualTo(ComparableTuple.of("Hi", "there", "world"));

        // HashCode
        assertThat(ComparableTuple.of("Hello", "World").hashCode()).isEqualTo(ComparableTuple.of("Hello", "World").hashCode());
        assertThat(ComparableTuple.of("Hello", "World").hashCode()).isNotEqualTo(ComparableTuple.of("Hello", "world").hashCode());
        assertThat(ComparableTuple.of("Hello", "World").hashCode()).isNotEqualTo(ComparableTuple.of("hello", "World").hashCode());

        // Compare To
        assertThat(ComparableTuple.of(1, 2).compareTo(new ComparablePair<>(1, 2))).isEqualTo(0);
        assertThat(ComparableTuple.of(1, 2).compareTo(ComparableTuple.of(2, 2))).isLessThan(0);
        assertThat(ComparableTuple.of(1, 2).compareTo(ComparableTuple.of(1, 3))).isLessThan(0);
        assertThat(ComparableTuple.of(1, 2).compareTo(ComparableTuple.of(0, 2))).isGreaterThan(0);
        assertThat(ComparableTuple.of(1, 2).compareTo(ComparableTuple.of(1, 0))).isGreaterThan(0);

        // ToString
        assertThat(ComparableTuple.of("Hello", "World").toString()).isEqualTo("(Hello, World)");

        // Map
        assertThat(ComparableTuple.of(1, 2).map((t1, t2) -> ComparableTuple.of(t1.toString(), t2.toString()))).isEqualTo(ComparableTuple.of("1", "2"));
        assertThat(ComparableTuple.of("1", "2").map(Integer::parseInt, Integer::parseInt)).isEqualTo(ComparableTuple.of(1, 2));
        assertThat(ComparableTuple.of("1", "2").map1(Integer::parseInt)).isEqualTo(ComparableTuple.of(1, "2"));
        assertThat(ComparableTuple.of("1", "2").map2(Integer::parseInt)).isEqualTo(ComparableTuple.of("1", 2));

        // Swap
        assertThat(ComparableTuple.of(1, 2).swap()).isEqualTo(ComparableTuple.of(2, 1));

        // To/From Map Entry
        assertThat(ComparableTuple.of("1", "2").toEntry()).isEqualTo(new AbstractMap.SimpleEntry<>("1", "2"));
        assertThat(ComparableTuple.fromEntry(new AbstractMap.SimpleEntry<>("1", "2"))).isEqualTo(ComparableTuple.of("1", "2"));

        // Arity
        assertThat(ComparableTuple.of("Hello", "World").arity()).isEqualTo(2);

        // ToList
        assertThat(ComparableTuple.of("Hello", "World").toList()).isEqualTo(List.of("Hello", "World"));
    }

    @Test
    void test_ComparableTriple() {
        // Element accessors
        assertThat(ComparableTuple.of("Hi", "there", "World")._1).isEqualTo("Hi");
        assertThat(ComparableTuple.of("Hi", "there", "World")._2).isEqualTo("there");
        assertThat(ComparableTuple.of("Hi", "there", "World")._3).isEqualTo("World");
        assertThat(ComparableTuple.of("Hi", "there", "World")._1()).isEqualTo("Hi");
        assertThat(ComparableTuple.of("Hi", "there", "World")._2()).isEqualTo("there");
        assertThat(ComparableTuple.of("Hi", "there", "World")._3()).isEqualTo("World");

        // Equals
        assertThat(ComparableTuple.of("Hi", "there", "World")).isEqualTo(new ComparableTriple<>("Hi", "there", "World"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isEqualTo(new LeftMiddleAndRightSide("Hi", "there", "World"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isNotEqualTo(ComparableTuple.of("hi", "there", "World"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isNotEqualTo(ComparableTuple.of("Hi", "There", "World"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isNotEqualTo(ComparableTuple.of("Hi", "there", "world"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isNotEqualTo(ComparableTuple.of("Hi"));
        assertThat(ComparableTuple.of("Hi", "there", "World")).isNotEqualTo(ComparableTuple.of("Hi", "There"));

        // HashCode
        assertThat(ComparableTuple.of("Hi", "there", "World").hashCode()).isEqualTo(ComparableTuple.of("Hi", "there", "World").hashCode());
        assertThat(ComparableTuple.of("Hi", "there", "World").hashCode()).isNotEqualTo(ComparableTuple.of("Hi", "There", "World").hashCode());
        assertThat(ComparableTuple.of("Hi", "there", "World").hashCode()).isNotEqualTo(ComparableTuple.of("Hi", "there", "world").hashCode());

        // Compare To
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(new ComparableTriple<>(1, 2, 3))).isEqualTo(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(2, 2, 3))).isLessThan(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(1, 3, 3))).isLessThan(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(1, 2, 4))).isLessThan(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(0, 2, 3))).isGreaterThan(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(1, 1, 3))).isGreaterThan(0);
        assertThat(ComparableTuple.of(1, 2, 3).compareTo(ComparableTuple.of(1, 2, 2))).isGreaterThan(0);

        // ToString
        assertThat(ComparableTuple.of("Hi", "there", "World").toString()).isEqualTo("(Hi, there, World)");

        // Map
        assertThat(ComparableTuple.of(1, 2, 3).map((t1, t2, t3) -> ComparableTuple.of(t1.toString(), t2.toString(), t3.toString()))).isEqualTo(ComparableTuple.of("1", "2", "3"));
        assertThat(ComparableTuple.of("1", "2", "3").map(Integer::parseInt, Integer::parseInt, Integer::parseInt)).isEqualTo(ComparableTuple.of(1, 2, 3));
        assertThat(ComparableTuple.of("1", "2", "3").map1(Integer::parseInt)).isEqualTo(ComparableTuple.of(1, "2", "3"));
        assertThat(ComparableTuple.of("1", "2", "3").map2(Integer::parseInt)).isEqualTo(ComparableTuple.of("1", 2, "3"));
        assertThat(ComparableTuple.of("1", "2", "3").map3(Integer::parseInt)).isEqualTo(ComparableTuple.of("1", "2", 3));

        // Arity
        assertThat(Tuple.of("Hi", "there", "World").arity()).isEqualTo(3);

        // ToList
        assertThat(Tuple.of("Hi", "there", "World").toList()).isEqualTo(List.of("Hi", "there", "World"));
    }

    private static class Option extends ComparableSingle<String> {
        /**
         * Construct a new specialized {@link Tuple} with 1 element
         *
         * @param optionalValue the first element
         */
        public Option(String optionalValue) {
            super(optionalValue);
        }
    }

    private static class LeftAndRightSide extends ComparablePair<String, String> {
        /**
         * Construct a new specialized {@link Tuple} with 2 elements
         *
         * @param leftSide  the first element
         * @param rightSide the second element
         */
        public LeftAndRightSide(String leftSide, String rightSide) {
            super(leftSide, rightSide);
        }
    }

    private static class LeftMiddleAndRightSide extends ComparableTriple<String, String, String> {
        /**
         * Construct a new specialized {@link Tuple} with 3 elements
         *
         * @param leftSide  the first element
         * @param middle    the second element
         * @param rightSide the third element
         */
        public LeftMiddleAndRightSide(String leftSide, String middle, String rightSide) {
            super(leftSide, middle, rightSide);
        }
    }
}