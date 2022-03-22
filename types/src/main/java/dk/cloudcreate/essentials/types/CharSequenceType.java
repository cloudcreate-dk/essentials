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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Base class for all {@link SingleValueType}'s that encapsulate {@link CharSequence}/{@link String}.<br>
 * <br>
 * <b>Purpose:</b><br>
 * {@link CharSequenceType}'s are typically used to create strongly typed wrappers for simple values to increase readability, code search capabilities (e.g. searching for where a given type is referenced).<br>
 * Example of candidates for concrete {@link CharSequenceType} sub-types are Primary-Key identifiers, Aggregate Identifiers, Currency symbols, etc.<br>
 * <br>
 * Example of a <b>Concrete</b> {@link CharSequenceType}'s subclass CustomerId:
 * <pre>{@code
 * public class CustomerId extends CharSequenceType<CustomerId> implements Identifier {
 *     public CustomerId(CharSequence value) {
 *         super(value);
 *     }
 *
 *     public static CustomerId of(CharSequence value) {
 *         return new CustomerId(value);
 *     }
 *
 *     public static CustomerId ofNullable(CharSequence value) {
 *         return value != null ? new CustomerId(value) : null;
 *     }
 *
 *     public static CustomerId random() {
 *         return new CustomerId(UUID.randomUUID().toString());
 *     }
 * }
 * }</pre>
 *
 * @param <CONCRETE_TYPE> The concrete {@link CharSequenceType} implementation
 */
public abstract class CharSequenceType<CONCRETE_TYPE extends CharSequenceType<CONCRETE_TYPE>> implements CharSequence, SingleValueType<CharSequence, CONCRETE_TYPE> {
    private final String value;

    public CharSequenceType(CharSequence value) {
        FailFast.requireNonNull(value, "You must provide a value");
        if (value instanceof String) {
            this.value = (String) value;
        } else {
            this.value = value.toString();
        }
    }

    /**
     * Get the value of the type instance as a {@link CharSequence} - the alternative is to call {@link #toString()}
     *
     * @return the value of the type instance (never null)
     */
    @Override
    public CharSequence value() {
        return value;
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return value.subSequence(beginIndex,
                                 endIndex);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public IntStream chars() {
        return value.chars();
    }

    @Override
    public IntStream codePoints() {
        return value.codePoints();
    }

    @Override
    public int compareTo(CONCRETE_TYPE o) {
        return value.compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(this.getClass().isAssignableFrom(o.getClass()))) {
            return false;
        }

        var that = (CharSequenceType<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }


    public boolean isEmpty() {
        return value.isEmpty();
    }

    public int codePointAt(int index) {
        return value.codePointAt(index);
    }

    public int codePointBefore(int index) {
        return value.codePointBefore(index);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        return value.codePointCount(beginIndex,
                                    endIndex);
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return value.offsetByCodePoints(index,
                                        codePointOffset);
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        value.getChars(srcBegin,
                       srcEnd,
                       dst,
                       dstBegin);
    }

    public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
        return value.getBytes(charsetName);
    }

    public byte[] getBytes(Charset charset) {
        return value.getBytes(charset);
    }

    public byte[] getBytes() {
        return value.getBytes();
    }

    public boolean contentEquals(StringBuffer sb) {
        return value.contentEquals(sb);
    }

    public boolean contentEquals(CharSequence cs) {
        return value.contentEquals(cs);
    }

    public boolean equalsIgnoreCase(String anotherString) {
        return value.equalsIgnoreCase(anotherString);
    }

    public int compareTo(String anotherString) {
        return value.compareTo(anotherString);
    }

    public int compareToIgnoreCase(String str) {
        return value.compareToIgnoreCase(str);
    }

    public boolean startsWith(String prefix, int toffset) {
        return value.startsWith(prefix,
                                toffset);
    }

    public boolean startsWith(String prefix) {
        return value.startsWith(prefix);
    }

    public boolean endsWith(String suffix) {
        return value.endsWith(suffix);
    }

    public int indexOf(int ch) {
        return value.indexOf(ch);
    }

    public int indexOf(int ch, int fromIndex) {
        return value.indexOf(ch,
                             fromIndex);
    }

    public int lastIndexOf(int ch) {
        return value.lastIndexOf(ch);
    }

    public int lastIndexOf(int ch, int fromIndex) {
        return value.lastIndexOf(ch,
                                 fromIndex);
    }

    public int indexOf(String str) {
        return value.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return value.indexOf(str,
                             fromIndex);
    }

    public int lastIndexOf(String str) {
        return value.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return value.lastIndexOf(str,
                                 fromIndex);
    }

    public boolean contains(String str) {
        return value.contains(str);
    }

    public boolean containsIgnoreCase(String str) {
        Objects.requireNonNull(str);
        return value.toLowerCase().contains(str.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public CONCRETE_TYPE substring(int beginIndex) {
        return (CONCRETE_TYPE) SingleValueType.from(value.substring(beginIndex), this.getClass());
    }

    @SuppressWarnings("unchecked")
    public CONCRETE_TYPE substring(int beginIndex, int endIndex) {
        return (CONCRETE_TYPE) SingleValueType.from(value.substring(beginIndex,
                                                                    endIndex), this.getClass());
    }

    public char[] toCharArray() {
        return value.toCharArray();
    }
}
