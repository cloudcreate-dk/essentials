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

package dk.cloudcreate.essentials.jackson.model;

import dk.cloudcreate.essentials.types.*;

import java.util.UUID;

public class ProductId extends CharSequenceType<ProductId> implements Identifier {
    public ProductId(CharSequence value) {
        super(value);
    }

    public static ProductId of(CharSequence value) {
        return new ProductId(value);
    }

    public static ProductId ofNullable(CharSequence value) {
        return value != null ? new ProductId(value) : null;
    }

    public static ProductId random() {
        // You can use any random Id generator like e.g. https://github.com/codahale/time-id
        return new ProductId(UUID.randomUUID().toString());
    }
}
