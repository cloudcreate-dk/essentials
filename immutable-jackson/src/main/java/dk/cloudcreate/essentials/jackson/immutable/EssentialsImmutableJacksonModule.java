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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dk.cloudcreate.essentials.shared.FailFast;
import org.objenesis.Objenesis;

/**
 * The Jackson module that assists Jackson support for deserializing immutable classes or other classes that don't have a suitable creator (constructor, or no-arg static factory method, etc.).<br>
 * <br>
 * This is very useful for when you're using Record's (in Java 14+) or other types supporting immutable objects, as it allows Jackson to create an object instance
 * without requiring a matching constructing.<br>
 * Property/Field values are set directly using reflection, even if the fields themselves are final.<br>
 * <br>
 * For this to work we require that opinionated defaults, such as <b>using FIELDS for serialization</b>, have been applied to the {@link ObjectMapper} instance.<br>
 * This can e.g. be accomplished by using the {@link EssentialsImmutableJacksonModule#createObjectMapper(Module...)} method
 * <p>
 * The object creation/instantiation logic is as follows:<br>
 * <ul>
 *  <li>First it will try using the <b>standard Jackson {@link ValueInstantiator}</b></li>
 *  <li>IF the <b>standard Jackson {@link ValueInstantiator}</b> cannot create a new instance of the Class, then we will use {@link Objenesis} to create an instance of the Class<br>
 *  <b>BE AWARE: If the object is created/instantiated using {@link Objenesis} then <u>NO constructor will be called and NO fields will have their default values set by Java</u>!!!!</b>
 *  </li>
 * </ul>
 */
public final class EssentialsImmutableJacksonModule extends SimpleModule {

    /**
     * Configure the Jackson Module
     */
    public EssentialsImmutableJacksonModule() {
        super("Essentials-Immutable");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
                builder.setValueInstantiator(new ImmutableObjectsValueInstantiator(beanDesc.getBeanClass(), builder.getValueInstantiator()));
                return builder;
            }
        });
        super.setupModule(context);
    }


    /**
     * Convenience method for creating a new {@link ObjectMapper} with our {@link EssentialsImmutableJacksonModule} added and with opinionated defaults like explicit JSON mapping,
     * not failing on unknown properties or empty beans, propagation of transient markers, using FIELDS for serialization (i.e. disabling auto detection of getters
     * and setters and instead using field and allowing ANY visibility for fields and constructors and none for getters/setters)
     *
     * @param additionalModules Additional modules that should be added to the {@link ObjectMapper} instance created
     * @return new {@link ObjectMapper} instance configured with sensible defaults and the {@link EssentialsImmutableJacksonModule} added
     */
    public static ObjectMapper createObjectMapper(Module... additionalModules) {
        FailFast.requireNonNull(additionalModules, "additionalModules is null");
        return JsonMapper.builder()
                         .visibility(VisibilityChecker.Std.Std.defaultInstance()
                                                              .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                                                              .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                                              .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                                              .withCreatorVisibility(JsonAutoDetect.Visibility.ANY))
                         .disable(MapperFeature.AUTO_DETECT_GETTERS)
                         .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
                         .disable(MapperFeature.AUTO_DETECT_SETTERS)
                         .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                         .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                         .enable(MapperFeature.AUTO_DETECT_CREATORS)
                         .enable(MapperFeature.AUTO_DETECT_FIELDS)
                         .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                         .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                         .addModule(new EssentialsImmutableJacksonModule())
                         .addModules(additionalModules)
                         .build();
    }
}
