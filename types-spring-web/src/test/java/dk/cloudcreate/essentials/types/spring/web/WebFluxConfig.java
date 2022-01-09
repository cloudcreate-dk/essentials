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

package dk.cloudcreate.essentials.types.spring.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cloudcreate.essentials.jackson.types.EssentialTypesJacksonModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.*;
import org.springframework.web.reactive.config.*;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(
                new Jackson2JsonEncoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonDecoder(
                new Jackson2JsonDecoder(objectMapper));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SingleValueTypeConverter());
    }
}
