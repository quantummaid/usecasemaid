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
import de.quantummaid.injectmaid.api.InjectorConfiguration;
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer;
import de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration;
import de.quantummaid.usecasemaid.sideeffects.SideEffectsSystem;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.api.ReusePolicy.PROTOTYPE;
import static de.quantummaid.usecasemaid.UseCases.useCases;
import static de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer.serializationAndDeserialization;
import static de.quantummaid.usecasemaid.serializing.UseCaseClassScanner.addMethod;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectsSystem.sideEffectsSystem;
import static de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod.useCaseMethodOf;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class UseCaseMaidIC implements InjectorConfiguration {
    private final List<InjectorConfiguration> dependencies;
    private final List<InjectorConfiguration> invocationScopedDependencies;
    private final List<Recipe> mapperConfigurations;
    private final List<RoutingTarget> useCases;
    private final List<SideEffectRegistration> sideEffectRegistrations;
    private final ExecutionDriver executionDriver;

    private final Map<RoutingTarget, UseCaseMethod> useCaseMethods = new LinkedHashMap<>();
    private final Map<UseCaseMethod, TypeIdentifier> virtualTypeIdentifiers = new LinkedHashMap<>();
    private final Map<ResolvedType, SideEffectRegistration> sideEffectRegistrationMap = new LinkedHashMap<>();

    @Override
    public void apply(final InjectMaidBuilder builder) {
        final MapMaidBuilder mapMaidBuilder = MapMaid.aMapMaid(builder.reflectMaid());
        dependencies.forEach(builder::withConfiguration);
        builder.withScope(Invocation.class, invocationScope -> {
            invocationScope.withCustomType(InvocationId.class, Invocation.class, Invocation::id);
            invocationScopedDependencies.forEach(invocationScope::withConfiguration);
            useCases.forEach(target -> {
                final ResolvedType type = target.type();
                final UseCaseMethod useCaseMethod = useCaseMethodOf(type);
                useCaseMethods.put(target, useCaseMethod);
                addMethod(useCaseMethod, mapMaidBuilder, virtualTypeIdentifiers);
                invocationScope.withType(type, PROTOTYPE);
            });
        });

        sideEffectRegistrations.forEach(sideEffectRegistration -> {
            final GenericType<?> type = sideEffectRegistration.type();
            mapMaidBuilder.injecting(type);
            final ResolvedType resolvedType = builder.reflectMaid().resolve(type);
            sideEffectRegistrationMap.put(resolvedType, sideEffectRegistration);
        });
        final SideEffectsSystem sideEffectsSystem = sideEffectsSystem(sideEffectRegistrationMap);
        mapperConfigurations.forEach(recipe -> recipe.apply(mapMaidBuilder));
        final MapMaid mapMaid = mapMaidBuilder.build();
        final SerializerAndDeserializer serializerAndDeserializer = serializationAndDeserialization(mapMaid, virtualTypeIdentifiers);

        builder.withCustomType(
                UseCaseMaid.class,
                InjectMaid.class,
                injector ->
                        UseCaseMaid.useCaseMaid(
                                builder.reflectMaid(),
                                useCases(useCaseMethods),
                                injector,
                                serializerAndDeserializer,
                                sideEffectsSystem,
                                executionDriver
                        ),
                ReusePolicy.DEFAULT_SINGLETON);
    }
}
