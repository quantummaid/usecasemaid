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
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer;
import de.quantummaid.usecasemaid.serializing.UseCaseClassScanner;
import de.quantummaid.usecasemaid.sideeffects.SideEffectExecutor;
import de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration;
import de.quantummaid.usecasemaid.sideeffects.driver.SideEffectsDriver;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.UseCaseRoute.useCaseRoute;
import static de.quantummaid.usecasemaid.UseCases.useCases;
import static de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer.serializationAndDeserialization;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration.sideEffectRegistration;
import static de.quantummaid.usecasemaid.sideeffects.driver.SimpleSideEffectsDriver.simpleSideEffectsDriver;
import static de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod.useCaseMethodOf;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseMaidBuilder {
    private final Map<String, GenericType<?>> useCases;
    private final List<SideEffectRegistration> sideEffectRegistrations;
    private SideEffectsDriver sideEffectsDriver = simpleSideEffectsDriver();

    static UseCaseMaidBuilder useCaseMaidBuilder() {
        return new UseCaseMaidBuilder(
                new LinkedHashMap<>(),
                new ArrayList<>()
        );
    }

    public UseCaseMaidBuilder invoking(final String route,
                                       final Class<?> useCase) {
        final GenericType<?> genericType = genericType(useCase);
        return invoking(route, genericType);
    }

    public UseCaseMaidBuilder invoking(final String route,
                                       final GenericType<?> useCase) {
        useCases.put(route, useCase);
        return this;
    }

    public <S> UseCaseMaidBuilder withSideEffects(final Class<S> sideEffectType,
                                                  final SideEffectExecutor<S> sideEffectExecutor) {
        final GenericType<S> genericType = genericType(sideEffectType);
        return withSideEffects(genericType, sideEffectExecutor);
    }

    public <S> UseCaseMaidBuilder withSideEffects(final GenericType<S> sideEffectType,
                                                  final SideEffectExecutor<S> sideEffectExecutor) {
        final SideEffectRegistration sideEffectRegistration = sideEffectRegistration(sideEffectType, sideEffectExecutor);
        sideEffectRegistrations.add(sideEffectRegistration);
        return this;
    }

    public UseCaseMaid build() {
        final Map<UseCaseRoute, UseCaseMethod> useCaseMethods = new LinkedHashMap<>();
        final InjectMaidBuilder injectMaidBuilder = InjectMaid.anInjectMaid();
        final MapMaidBuilder mapMaidBuilder = MapMaid.aMapMaid();
        useCases.forEach((route, type) -> {
            final ResolvedType resolvedType = type.toResolvedType();
            final UseCaseMethod useCaseMethod = useCaseMethodOf(resolvedType);
            useCaseMethods.put(useCaseRoute(route), useCaseMethod);
            UseCaseClassScanner.addMethod(useCaseMethod, mapMaidBuilder);
            injectMaidBuilder.withType(type);
        });
        sideEffectRegistrations.forEach(sideEffectRegistration -> {
            final GenericType<?> type = sideEffectRegistration.type();
            mapMaidBuilder.injecting(type);
        });
        final MapMaid mapMaid = mapMaidBuilder.build();
        final SerializerAndDeserializer serializerAndDeserializer = serializationAndDeserialization(mapMaid);
        final InjectMaid injectMaid = injectMaidBuilder.build();
        return UseCaseMaid.useCaseMaid(
                useCases(useCaseMethods),
                injectMaid,
                serializerAndDeserializer,
                sideEffectRegistrations,
                sideEffectsDriver
        );
    }
}
