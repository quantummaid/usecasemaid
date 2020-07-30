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

package de.quantummaid.usecasemaid;

import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseRoute.useCaseRoute;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseMaid {
    private final UseCases useCases;
    private final InjectMaid instantiator;
    private final SerializerAndDeserializer serializerAndDeserializer;

    public static UseCaseMaidBuilder aUseCaseMaid() {
        return UseCaseMaidBuilder.useCaseMaidBuilder();
    }

    static UseCaseMaid useCaseMaid(final UseCases useCases,
                                   final InjectMaid instantiator,
                                   final SerializerAndDeserializer serializerAndDeserializer) {
        return new UseCaseMaid(useCases, instantiator, serializerAndDeserializer);
    }

    public void invoke(final String route,
                       final Map<String, Object> input) {
        final UseCaseMethod useCaseMethod = useCases.forRoute(useCaseRoute(route));
        final ResolvedType objectType = useCaseMethod.useCaseClass();
        final Object instance = instantiator.getInstance(objectType);

        final Map<String, Object> parameters = serializerAndDeserializer.deserializeParameters(input, useCaseMethod);

        try {
            useCaseMethod.invoke(instance, parameters);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
