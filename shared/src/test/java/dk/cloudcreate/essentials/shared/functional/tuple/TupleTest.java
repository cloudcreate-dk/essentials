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

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class TupleTest {

    @Test
    void test_Empty() {
        // Equals
        assertThat(Tuple.empty()).isEqualTo(Empty.instance());
        assertThat(Tuple.empty()).isEqualTo(Tuple.of());
        assertThat(Tuple.empty()).isEqualTo(Empty.INSTANCE);

        // HashCode
        assertThat(Tuple.empty().hashCode()).isEqualTo(Empty.instance().hashCode());
        assertThat(Tuple.empty().hashCode()).isEqualTo(Empty.INSTANCE.hashCode());

        // ToString
        assertThat(Tuple.empty().toString()).isEqualTo("Empty");

        // Arity
        assertThat(Tuple.empty().arity()).isEqualTo(0);

        // ToList
        assertThat(Tuple.empty().toList()).isEmpty();
    }

    @Test
    void test_Single() {
        // Element accessors
        assertThat(Tuple.of("Test")._1).isEqualTo("Test");
        assertThat(Tuple.of("Test")._1()).isEqualTo("Test");

        // Equals
        assertThat(Tuple.of("Test")).isEqualTo(new Single<>("Test"));
        assertThat(Tuple.of("Test")).isEqualTo(new Option("Test"));
        assertThat(Tuple.of("Test")).isNotEqualTo(Tuple.of("Test2"));

        // HashCode
        assertThat(Tuple.of("Test").hashCode()).isEqualTo(new Single<>("Test").hashCode());
        assertThat(Tuple.of("Test").hashCode()).isNotEqualTo(Tuple.of("Test2").hashCode());

        // ToString
        assertThat(Tuple.of("Test").toString()).isEqualTo("(Test)");

        // Map
        assertThat(Tuple.of("Test").map(String::toUpperCase)).isEqualTo(Tuple.of("TEST"));

        // Arity
        assertThat(Tuple.of("Test").arity()).isEqualTo(1);

        // ToList
        assertThat(Tuple.of("Test").toList()).isEqualTo(List.of("Test"));
    }

    @Test
    void test_Pair() {
        // Element accessors
        assertThat(Tuple.of("Hello", "World")._1).isEqualTo("Hello");
        assertThat(Tuple.of("Hello", "World")._2).isEqualTo("World");
        assertThat(Tuple.of("Hello", "World")._1()).isEqualTo("Hello");
        assertThat(Tuple.of("Hello", "World")._2()).isEqualTo("World");

        // Equals
        assertThat(Tuple.of("Hello", "World")).isEqualTo(new Pair<>("Hello", "World"));
        assertThat(Tuple.of("Hello", "World")).isEqualTo(new LeftAndRightSide("Hello", "World"));
        assertThat(Tuple.of("Hello", "World")).isNotEqualTo(Tuple.of("Hello", "world"));
        assertThat(Tuple.of("Hello", "World")).isNotEqualTo(Tuple.of("hello", "World"));
        assertThat(Tuple.of("Hello", "World")).isNotEqualTo(Tuple.of("hello"));
        assertThat(Tuple.of("Hi", "World")).isNotEqualTo(Tuple.of("Hi", "there", "world"));

        // HashCode
        assertThat(Tuple.of("Hello", "World").hashCode()).isEqualTo(Tuple.of("Hello", "World").hashCode());
        assertThat(Tuple.of("Hello", "World").hashCode()).isNotEqualTo(Tuple.of("Hello", "world").hashCode());
        assertThat(Tuple.of("Hello", "World").hashCode()).isNotEqualTo(Tuple.of("hello", "World").hashCode());

        // ToString
        assertThat(Tuple.of("Hello", "World").toString()).isEqualTo("(Hello, World)");

        // Map
        assertThat(Tuple.of(1, 2).map((t1, t2) -> Tuple.of(t1.toString(), t2.toString()))).isEqualTo(Tuple.of("1", "2"));
        assertThat(Tuple.of("1", "2").map(Integer::parseInt, Integer::parseInt)).isEqualTo(Tuple.of(1, 2));
        assertThat(Tuple.of("1", "2").map1(Integer::parseInt)).isEqualTo(Tuple.of(1, "2"));
        assertThat(Tuple.of("1", "2").map2(Integer::parseInt)).isEqualTo(Tuple.of("1", 2));

        // Swap
        assertThat(Tuple.of(1, 2).swap()).isEqualTo(Tuple.of(2, 1));

        // To/From Map Entry
        assertThat(Tuple.of("1", "2").toEntry()).isEqualTo(new AbstractMap.SimpleEntry<>("1", "2"));
        assertThat(Tuple.fromEntry(new AbstractMap.SimpleEntry<>("1", "2"))).isEqualTo(Tuple.of("1", "2"));

        // Arity
        assertThat(Tuple.of("Hello", "World").arity()).isEqualTo(2);

        // ToList
        assertThat(Tuple.of("Hello", "World").toList()).isEqualTo(List.of("Hello", "World"));
    }

    @Test
    void test_Triple() {
        // Element accessors
        assertThat(Tuple.of("Hi", "there", "World")._1).isEqualTo("Hi");
        assertThat(Tuple.of("Hi", "there", "World")._2).isEqualTo("there");
        assertThat(Tuple.of("Hi", "there", "World")._3).isEqualTo("World");
        assertThat(Tuple.of("Hi", "there", "World")._1()).isEqualTo("Hi");
        assertThat(Tuple.of("Hi", "there", "World")._2()).isEqualTo("there");
        assertThat(Tuple.of("Hi", "there", "World")._3()).isEqualTo("World");

        // Equals
        assertThat(Tuple.of("Hi", "there", "World")).isEqualTo(new Triple<>("Hi", "there", "World"));
        assertThat(Tuple.of("Hi", "there", "World")).isEqualTo(new LeftMiddleAndRightSide("Hi", "there", "World"));
        assertThat(Tuple.of("Hi", "there", "World")).isNotEqualTo(Tuple.of("hi", "there", "World"));
        assertThat(Tuple.of("Hi", "there", "World")).isNotEqualTo(Tuple.of("Hi", "There", "World"));
        assertThat(Tuple.of("Hi", "there", "World")).isNotEqualTo(Tuple.of("Hi", "there", "world"));
        assertThat(Tuple.of("Hi", "there", "World")).isNotEqualTo(Tuple.of("Hi"));
        assertThat(Tuple.of("Hi", "there", "World")).isNotEqualTo(Tuple.of("Hi", "There"));

        // HashCode
        assertThat(Tuple.of("Hi", "there", "World").hashCode()).isEqualTo(Tuple.of("Hi", "there", "World").hashCode());
        assertThat(Tuple.of("Hi", "there", "World").hashCode()).isNotEqualTo(Tuple.of("Hi", "There", "World").hashCode());
        assertThat(Tuple.of("Hi", "there", "World").hashCode()).isNotEqualTo(Tuple.of("Hi", "there", "world").hashCode());

        // ToString
        assertThat(Tuple.of("Hi", "there", "World").toString()).isEqualTo("(Hi, there, World)");

        // Map
        assertThat(Tuple.of(1, 2, 3).map((t1, t2, t3) -> Tuple.of(t1.toString(), t2.toString(), t3.toString()))).isEqualTo(Tuple.of("1", "2", "3"));
        assertThat(Tuple.of("1", "2", "3").map(Integer::parseInt, Integer::parseInt, Integer::parseInt)).isEqualTo(Tuple.of(1, 2, 3));
        assertThat(Tuple.of("1", "2", "3").map1(Integer::parseInt)).isEqualTo(Tuple.of(1, "2", "3"));
        assertThat(Tuple.of("1", "2", "3").map2(Integer::parseInt)).isEqualTo(Tuple.of("1", 2, "3"));
        assertThat(Tuple.of("1", "2", "3").map3(Integer::parseInt)).isEqualTo(Tuple.of("1", "2", 3));

        // Arity
        assertThat(Tuple.of("Hi", "there", "World").arity()).isEqualTo(3);

        // ToList
        assertThat(Tuple.of("Hi", "there", "World").toList()).isEqualTo(List.of("Hi", "there", "World"));
    }

    private static class Option extends Single<String> {
        /**
         * Construct a new specialized {@link Tuple} with 1 element
         *
         * @param optionalValue the first element
         */
        public Option(String optionalValue) {
            super(optionalValue);
        }
    }

    private static class LeftAndRightSide extends Pair<String, String> {
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

    private static class LeftMiddleAndRightSide extends Triple<String, String, String> {
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