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

package dk.cloudcreate.essentials.jackson.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dk.cloudcreate.essentials.shared.FailFast;
import dk.cloudcreate.essentials.types.*;

/**
 * The Jackson module that assists Jackson with serialization and deserialization of essential types (such as {@link SingleValueType})
 */
public final class EssentialTypesJacksonModule extends SimpleModule {

    /**
     * Configure the Jackson Module
     */
    public EssentialTypesJacksonModule() {
        super("Essentials-Types");
    }

    @Override
    public void setupModule(SetupContext context) {
        addSerializer(CharSequenceType.class, new CharSequenceTypeJsonSerializer());
        addSerializer(NumberType.class, new NumberTypeJsonSerializer());
        addDeserializer(Money.class, new MoneyDeserializer());
        super.setupModule(context);
    }

    /**
     * Convenience method for creating a new {@link ObjectMapper} with our {@link EssentialTypesJacksonModule} added and opinionated defaults like explicit JSON mapping,
     * not failing on unknown properties or empty beans, propagation of transient markers, using FIELDS for serialization (i.e. disabling auto detection of getters
     * and setters and instead using field and allowing ANY visibility for fields and constructors and none for getters/setters)
     *
     * @param additionalModules Additional modules that should be added to the {@link ObjectMapper} instance created
     * @return new {@link ObjectMapper} instance configured with sensible defaults and the {@link EssentialTypesJacksonModule} added
     */
    public static ObjectMapper createObjectMapper(Module... additionalModules) {
        FailFast.requireNonNull(additionalModules, "additionalModules is null");
        return JsonMapper.builder()
                         .visibility(VisibilityChecker.Std.Std.defaultInstance()
                                                              .withGetterVisibility(Visibility.NONE)
                                                              .withSetterVisibility(Visibility.NONE)
                                                              .withFieldVisibility(Visibility.ANY)
                                                              .withCreatorVisibility(Visibility.ANY))
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
                         .addModule(new EssentialTypesJacksonModule())
                         .addModules(additionalModules)
                         .build();
    }
}
