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

package dk.cloudcreate.essentials.types.springdata.mongo;

import dk.cloudcreate.essentials.shared.reflection.Reflector;
import dk.cloudcreate.essentials.types.SingleValueType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

/**
 * This {@link BeforeConvertCallback} will investigate if an Entity (of any type)
 * contains a field annotated with {@link Id} of type {@link SingleValueType}.<br>
 * If this field has value <code>null</code>, then we will call the <b>static</b>
 * <code>random()</code> method on the concrete {@link SingleValueType}.
 */
public class SingleValueTypeRandomIdGenerator implements BeforeConvertCallback<Object> {
    @Override
    public Object onBeforeConvert(Object entity, String collection) {
        var reflector  = Reflector.reflectOn(entity.getClass());
        var hasIdField = reflector.findFieldByAnnotation(Id.class);
        if (hasIdField.isPresent()) {
            var idField     = hasIdField.get();
            var idFieldType = idField.getType();
            var idValue     = reflector.get(entity, idField);
            if (idValue == null && SingleValueType.class.isAssignableFrom(idFieldType)) {
                // Generate a new id value
                var newIdValue = Reflector.reflectOn(idFieldType).invokeStatic("random");
                reflector.set(entity,
                              idField,
                              newIdValue);
            }
        }
        return entity;
    }
}
