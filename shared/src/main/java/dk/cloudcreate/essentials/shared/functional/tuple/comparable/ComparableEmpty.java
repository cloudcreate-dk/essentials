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

import java.util.List;

/**
 * Represents a {@link ComparableTuple} with zero elements
 */
public final class ComparableEmpty implements ComparableTuple<ComparableEmpty> {
    private static final List<Object>    EMPTY_LIST = List.of();
    /**
     * The shared single instance of the {@link ComparableEmpty} tuple
     */
    public static final  ComparableEmpty INSTANCE   = new ComparableEmpty();

    /**
     * The shared single instance of the {@link ComparableEmpty} tuple
     *
     * @return the shared single instance of the {@link ComparableEmpty} tuple
     */
    public static ComparableEmpty instance() {
        return INSTANCE;
    }

    private ComparableEmpty() {
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public List<?> toList() {
        return EMPTY_LIST;
    }

    @Override
    public int compareTo(ComparableEmpty o) {
        return 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparableEmpty)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Empty";
    }
}
