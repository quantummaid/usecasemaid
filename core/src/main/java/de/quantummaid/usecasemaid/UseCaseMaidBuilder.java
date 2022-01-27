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
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.sideeffects.SideEffectExecutor;
import de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.injectmaid.InjectMaid.anInjectMaid;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.RoutingTarget.routingTarget;
import static de.quantummaid.usecasemaid.driver.SimpleExecutionDriver.simpleExecutionDriver;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration.sideEffectRegistration;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseMaidBuilder {
    private final ReflectMaid reflectMaid;
    private final List<RoutingTarget> useCases = new ArrayList<>();
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
        final ResolvedType resolvedType = reflectMaid.resolve(useCase);
        return invoking(routingTarget(resolvedType));
    }

    public UseCaseMaidBuilder invoking(final RoutingTarget routingTarget) {
        useCases.add(routingTarget);
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
        final InjectorConfiguration injectMaidConfiguration = buildInjectorConfiguration();
        final InjectMaidBuilder injectMaidBuilder = anInjectMaid(reflectMaid).withConfiguration(injectMaidConfiguration);
        final InjectMaid injectMaid = injectMaidBuilder.build();
        return injectMaid.getInstance(UseCaseMaid.class);
    }

    public InjectorConfiguration buildInjectorConfiguration() {
        final UseCaseMaidIC injectorConfiguration = new UseCaseMaidIC(dependencies,
                invocationScopedDependencies,
                mapperConfigurations,
                useCases,
                sideEffectRegistrations,
                executionDriver);
        return injectorConfiguration;
    }
}
