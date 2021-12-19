/*
 * Copyright 2021 the original author or authors.
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

/**
 * Represents a Range which uses Long's to define from and to (both inclusive) in the Range.
 */
public final class LongRange {
    public final long fromInclusive;
    public final Long toInclusive;

    private LongRange(long fromInclusive, long toInclusive) {
        this.fromInclusive = fromInclusive;
        this.toInclusive = toInclusive;
    }

    private LongRange(long fromInclusive) {
        this.fromInclusive = fromInclusive;
        toInclusive = null;
    }

    /**
     * Create a <b>closed</b> range that covers <code>fromInclusive</code> until <code>toInclusive</code> (both included)
     *
     * @param fromInclusive the first value included in the range
     * @param toInclusive   the last value included in the range
     * @return a <b>closed</b> range that covers <code>fromInclusive</code> until <code>toInclusive</code> (both included)
     */
    public static LongRange between(long fromInclusive, long toInclusive) {
        return new LongRange(fromInclusive, toInclusive);
    }

    /**
     * Create a <b>open</b> range that covers all values from and including the <code>fromInclusive</code> and all numbers larger than this
     *
     * @param fromInclusive the first value included in the range
     * @return
     */
    public static LongRange from(long fromInclusive) {
        return new LongRange(fromInclusive);
    }

    /**
     * Create a range that only covers a single number
     *
     * @param fromAndToInclusive the only number covered by the {@link LongRange} returned
     */
    public static LongRange only(long fromAndToInclusive) {
        return new LongRange(fromAndToInclusive, fromAndToInclusive);
    }

    /**
     * Create a <b>closed</b> range that covers all values from and including the <code>fromInclusive</code> and all numbers
     * larger than <code>fromInclusive</code> and until and inclusive <code>fromInclusive+rangeLength</code>
     *
     * @param fromInclusive the first value included in the range
     * @param rangeLength   the length of the range (will result in a range from (and inclusive) <code>fromInclusive</code> and until and inclusive <code>fromInclusive+rangeLength</code>)
     * @return
     */
    public static LongRange from(long fromInclusive, long rangeLength) {
        return LongRange.between(fromInclusive, fromInclusive + rangeLength);
    }

    /**
     * Does the Range defined a {@link #toInclusive} value
     */
    public boolean isClosedRange() {
        return toInclusive != null;
    }

    /**
     * Is the Range without a {@link #toInclusive} value
     */
    public boolean isOpenRange() {
        return toInclusive == null;
    }

    /**
     * Does the range cover a given value
     *
     * @param value the value to test against the range
     * @return true if the range covers the <code>value</code>, otherwise false
     */
    public boolean covers(long value) {
        if (value < fromInclusive) return false;
        if (toInclusive != null) {
            return value <= toInclusive;
        } else {
            return true;
        }
    }


}
