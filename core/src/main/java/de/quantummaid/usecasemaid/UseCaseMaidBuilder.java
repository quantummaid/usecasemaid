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
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer;
import de.quantummaid.usecasemaid.serializing.UseCaseClassScanner;
import de.quantummaid.usecasemaid.sideeffects.SideEffectExecutor;
import de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration;
import de.quantummaid.usecasemaid.sideeffects.SideEffectsSystem;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.UseCases.useCases;
import static de.quantummaid.usecasemaid.driver.SimpleExecutionDriver.simpleExecutionDriver;
import static de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer.serializationAndDeserialization;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration.sideEffectRegistration;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectsSystem.sideEffectsSystem;
import static de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod.useCaseMethodOf;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseMaidBuilder {
    private final ReflectMaid reflectMaid;
    private final List<GenericType<?>> useCases = new ArrayList<>();
    private final List<SideEffectRegistration> sideEffectRegistrations = new ArrayList<>();
    private ExecutionDriver executionDriver = simpleExecutionDriver();
    private final List<Recipe> mapperConfigurations = new ArrayList<>();
    private final List<InjectorConfiguration> dependencies = new ArrayList<>();
    private final List<InjectorConfiguration> invocationScopedDependencies = new ArrayList<>();

    static UseCaseMaidBuilder useCaseMaidBuilder(final ReflectMaid reflectMaid) {
        return new UseCaseMaidBuilder(reflectMaid);
    }

    public UseCaseMaidBuilder invoking(final Class<?> useCase) {
        final GenericType<?> genericType = genericType(useCase);
        return invoking(genericType);
    }

    public UseCaseMaidBuilder invoking(final GenericType<?> useCase) {
        useCases.add(useCase);
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

    public UseCaseMaidBuilder withExecutionDriver(final ExecutionDriver executionDriver) {
        validateNotNull(executionDriver, "executionDriver");
        this.executionDriver = executionDriver;
        return this;
    }

    public UseCaseMaidBuilder withMapperConfiguration(final Recipe mapperConfiguration) {
        validateNotNull(mapperConfiguration, "mapperConfiguration");
        mapperConfigurations.add(mapperConfiguration);
        return this;
    }

    public UseCaseMaidBuilder withDependencies(final InjectorConfiguration module) {
        dependencies.add(module);
        return this;
    }

    public UseCaseMaidBuilder withInvocationScopedDependencies(final InjectorConfiguration module) {
        invocationScopedDependencies.add(module);
        return this;
    }

    public UseCaseMaid build() {
        final Map<ResolvedType, UseCaseMethod> useCaseMethods = new LinkedHashMap<>();
        final InjectMaidBuilder injectMaidBuilder = InjectMaid.anInjectMaid(reflectMaid);
        final MapMaidBuilder mapMaidBuilder = MapMaid.aMapMaid(reflectMaid);
        dependencies.forEach(injectMaidBuilder::withConfiguration);
        injectMaidBuilder.withScope(Invocation.class, builder -> {
            builder.withCustomType(InvocationId.class, Invocation.class, Invocation::id);
            invocationScopedDependencies.forEach(builder::withConfiguration);
            useCases.forEach(type -> {
                final ResolvedType resolvedType = reflectMaid.resolve(type);
                final UseCaseMethod useCaseMethod = useCaseMethodOf(resolvedType);
                useCaseMethods.put(resolvedType, useCaseMethod);
                UseCaseClassScanner.addMethod(useCaseMethod, mapMaidBuilder);
                builder.withType(type);
            });
        });
        final Map<ResolvedType, SideEffectRegistration> sideEffectRegistrationMap = new LinkedHashMap<>();
        sideEffectRegistrations.forEach(sideEffectRegistration -> {
            final GenericType<?> type = sideEffectRegistration.type();
            mapMaidBuilder.injecting(type);
            final ResolvedType resolvedType = reflectMaid.resolve(type);
            sideEffectRegistrationMap.put(resolvedType, sideEffectRegistration);
        });
        final SideEffectsSystem sideEffectsSystem = sideEffectsSystem(sideEffectRegistrationMap);
        mapperConfigurations.forEach(recipe -> recipe.apply(mapMaidBuilder));
        final MapMaid mapMaid = mapMaidBuilder.build();
        final SerializerAndDeserializer serializerAndDeserializer = serializationAndDeserialization(mapMaid);
        final InjectMaid injector = injectMaidBuilder.build();
        return UseCaseMaid.useCaseMaid(
                reflectMaid,
                useCases(useCaseMethods),
                injector,
                serializerAndDeserializer,
                sideEffectsSystem,
                executionDriver
        );
    }
}
