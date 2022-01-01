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

/**
 * Base class for all {@link SingleValueType}'s that encapsulate a {@link Byte}.<br>
 * Example concrete implementation of the {@link ByteType}:
 * <pre>{@code
 * public class Symbol extends ByteType<Symbol> {
 *     public Symbol(byte value) {
 *         super(value);
 *     }
 *
 *     public static Symbol of(byte value) {
 *         return new Symbol(value);
 *     }
 *
 *     public static Symbol ofNullable(Byte value) {
 *         return value != null ? new Symbol(value) : null;
 *     }
 * }
 * }</pre>
 *
 * @param <CONCRETE_TYPE> The concrete {@link ByteType} implementation
 */
public abstract class ByteType<CONCRETE_TYPE extends ByteType<CONCRETE_TYPE>> extends NumberType<Byte, CONCRETE_TYPE> {

    public ByteType(Byte value) {
        super(value);
    }

    @Override
    public int compareTo(CONCRETE_TYPE o) {
        return value.compareTo(o.byteValue());
    }


}
