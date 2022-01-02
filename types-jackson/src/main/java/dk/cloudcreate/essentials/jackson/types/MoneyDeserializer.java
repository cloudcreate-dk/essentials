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

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dk.cloudcreate.essentials.types.*;

import java.io.IOException;

public class MoneyDeserializer extends StdDeserializer<Money> {
    public MoneyDeserializer() {
        this(null);
    }

    public MoneyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        var    amount       = node.get("amount").decimalValue();
        String currencyCode = node.get("currency").asText();

        return new Money(Amount.of(amount),
                         CurrencyCode.of(currencyCode));
    }
}
