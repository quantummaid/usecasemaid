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

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;

import java.util.Map;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType.deserializationOnlyType;
import static de.quantummaid.reflectmaid.GenericType.fromResolvedType;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.uniqueVirtualTypeIdentifier;
import static de.quantummaid.usecasemaid.serializing.VirtualDeserializer.virtualDeserializerFor;
import static java.lang.String.format;

public final class UseCaseClassScanner {

    private UseCaseClassScanner() {
    }

    public static void addMethod(final UseCaseMethod method,
                                 final MapMaidBuilder builder,
                                 final Map<UseCaseMethod, TypeIdentifier> virtualTypeIdentifiers) {
        final Map<String, ResolvedType> parameters = method.parameters();
        parameters.values().stream()
                .map(GenericType::fromResolvedType)
                .forEach(type -> builder.withType(
                        type, deserialization(), format("because parameter type of method %s", method.describe())));

        method.returnType().ifPresent(type -> {
            final GenericType<?> genericType = fromResolvedType(type);
            builder.withType(
                    genericType,
                    serialization(),
                    format("because return type of method %s", method.describe()));
        });

        final TypeIdentifier virtualTypeIdentifier = uniqueVirtualTypeIdentifier();
        final TypeDeserializer deserializer = virtualDeserializerFor(method.describe(), parameters);
        final DeserializationOnlyType<?> virtualType = deserializationOnlyType(virtualTypeIdentifier, deserializer);
        builder.deserializing(virtualType);
        virtualTypeIdentifiers.put(method, virtualTypeIdentifier);
    }
}
