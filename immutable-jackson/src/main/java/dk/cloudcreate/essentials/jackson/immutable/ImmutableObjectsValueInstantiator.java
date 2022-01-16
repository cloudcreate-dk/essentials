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

package dk.cloudcreate.essentials.jackson.immutable;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import dk.cloudcreate.essentials.shared.reflection.Reflector;
import org.objenesis.*;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.IOException;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * {@link ValueInstantiator} for Jackson that allows for deserializing of immutable classes or other classes that don't have a suitable creator
 * (constructor, or no-arg static factory method, etc.).<br>
 * <br>
 * This is very useful for when you're using Record's (in Java 14+) or other types of immutable classes, as it allows Jackson to create an object instance
 * without requiring a matching constructing.<br>
 * Property/Field values are set directly using reflection, even if the fields themselves are final.<br>
 * <br>
 * For this to work we require that opinionated defaults, such as using FIELDS for serialization, have applied to the {@link ObjectMapper} instance.<br>
 * This can e.g. be accomplished by using the {@link EssentialsImmutableJacksonModule#createObjectMapper(Module...)} method
 * <p>
 * The object creation/instantiation logic is as follows:<br>
 * <ul>
 *  <li>First it will try using the <b>standard Jackson {@link ValueInstantiator}</b> provided via the constructor</li>
 *  <li>Only if the <b>standard Jackson {@link ValueInstantiator}</b> cannot create a new instance of the Class, then we will use {@link Objenesis} to create an instance of the Class<br>
 *  <b>BE AWARE: If the object is created/instantiated using Objenesis then <u>NO constructor will be called and NO default field values will be applied</u>!!!!</b>
 *  </li>
 * </ul>
 */
public class ImmutableObjectsValueInstantiator extends ValueInstantiator {
    private static Objenesis OBJENESIS = new ObjenesisStd();

    private final ValueInstantiator     standardJacksonValueInstantiator;
    private final ObjectInstantiator<?> objenesisInstantiator;

    public ImmutableObjectsValueInstantiator(Class<?> typeToInstantiate, ValueInstantiator defaultJacksonInstantiator) {
        requireNonNull(typeToInstantiate, "You must provide a typeToInstantiate instance");
        this.objenesisInstantiator = OBJENESIS.getInstantiatorOf(typeToInstantiate);
        this.standardJacksonValueInstantiator = requireNonNull(defaultJacksonInstantiator, "You must provide a standardJacksonValueInstantiator instance");
    }

    @Override
    public boolean canCreateUsingDefault() {
        // Either the standardJacksonValueInstantiator is capable of creating a new Object instance, otherwise we will fallback to using Objenesis
        return true;
    }

    @Override
    public Object createUsingDefault(DeserializationContext context) throws IOException {
        var isTheStandardValueInstantiatorCapableOfCreatingANewInstance = standardJacksonValueInstantiator.canCreateUsingDefault();
        if (isTheStandardValueInstantiatorCapableOfCreatingANewInstance) {
            return standardJacksonValueInstantiator.createUsingDefault(context);
        }
        return objenesisInstantiator.newInstance();
    }

    @Override
    public Class<?> getValueClass() {
        return standardJacksonValueInstantiator.getValueClass();
    }

    @Override
    public String getValueTypeDesc() {
        return standardJacksonValueInstantiator.getValueTypeDesc();
    }

    @Override
    public boolean canInstantiate() {
        return standardJacksonValueInstantiator.canInstantiate();
    }

    @Override
    public boolean canCreateFromString() {
        return standardJacksonValueInstantiator.canCreateFromString();
    }

    @Override
    public boolean canCreateFromInt() {
        return standardJacksonValueInstantiator.canCreateFromInt();
    }

    @Override
    public boolean canCreateFromLong() {
        return standardJacksonValueInstantiator.canCreateFromLong();
    }

    @Override
    public boolean canCreateFromDouble() {
        return standardJacksonValueInstantiator.canCreateFromDouble();
    }

    @Override
    public boolean canCreateFromBoolean() {
        return standardJacksonValueInstantiator.canCreateFromBoolean();
    }

    @Override
    public boolean canCreateUsingDelegate() {
        return standardJacksonValueInstantiator.canCreateUsingDelegate();
    }

    @Override
    public boolean canCreateUsingArrayDelegate() {
        return standardJacksonValueInstantiator.canCreateUsingArrayDelegate();
    }

    @Override
    public boolean canCreateFromObjectWith() {
        return standardJacksonValueInstantiator.canCreateFromObjectWith();
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return standardJacksonValueInstantiator.getFromObjectArguments(config);
    }

    @Override
    public JavaType getDelegateType(DeserializationConfig config) {
        return standardJacksonValueInstantiator.getDelegateType(config);
    }

    @Override
    public JavaType getArrayDelegateType(DeserializationConfig config) {
        return standardJacksonValueInstantiator.getArrayDelegateType(config);
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
        return standardJacksonValueInstantiator.createFromObjectWith(ctxt, args);
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
        return standardJacksonValueInstantiator.createFromObjectWith(ctxt, props, buffer);
    }

    @Override
    public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        return standardJacksonValueInstantiator.createUsingDelegate(ctxt, delegate);
    }

    @Override
    public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        return standardJacksonValueInstantiator.createUsingArrayDelegate(ctxt, delegate);
    }

    @Override
    public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
        return standardJacksonValueInstantiator.createFromString(ctxt, value);
    }

    @Override
    public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
        return standardJacksonValueInstantiator.createFromInt(ctxt, value);
    }

    @Override
    public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
        return standardJacksonValueInstantiator.createFromLong(ctxt, value);
    }

    @Override
    public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
        return standardJacksonValueInstantiator.createFromDouble(ctxt, value);
    }

    @Override
    public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
        return standardJacksonValueInstantiator.createFromBoolean(ctxt, value);
    }

    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return standardJacksonValueInstantiator.getDefaultCreator();
    }

    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return standardJacksonValueInstantiator.getDelegateCreator();
    }

    @Override
    public AnnotatedWithParams getArrayDelegateCreator() {
        return standardJacksonValueInstantiator.getArrayDelegateCreator();
    }

    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return standardJacksonValueInstantiator.getWithArgsCreator();
    }

    @Override
    protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value) throws IOException {
        return Reflector.reflectOn(standardJacksonValueInstantiator.getClass()).invoke("_createFromStringFallbacks", standardJacksonValueInstantiator, ctxt, value);
    }
}
