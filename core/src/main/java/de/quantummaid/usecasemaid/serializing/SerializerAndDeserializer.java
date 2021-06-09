/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.usecasemaid.serializing;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializerAndDeserializer {
    private final MapMaid mapMaid;

    public static SerializerAndDeserializer serializationAndDeserialization(final MapMaid mapMaid) {
        return new SerializerAndDeserializer(mapMaid);
    }

    public Map<String, Object> deserializeParameters(final Map<String, Object> input,
                                                     final UseCaseMethod useCaseMethod,
                                                     final InjectorLambda injector) {
        final Map<String, Object> parameters = new LinkedHashMap<>();
        useCaseMethod.parameters().forEach((name, type) -> {
            final Object serialized = input.get(name);
            final TypeIdentifier targetType = typeIdentifierFor(type);
            final Object deserialized = mapMaid.deserializeFromUniversalObject(
                    serialized,
                    targetType,
                    injector
            );
            parameters.put(name, deserialized);
        });
        return parameters;
    }

    public Object serializeReturnValue(final Object returnValue, final ResolvedType type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return mapMaid.serializeToUniversalObject(returnValue, typeIdentifier);
    }

    public MapMaid mapMaid() {
        return mapMaid;
    }
}
