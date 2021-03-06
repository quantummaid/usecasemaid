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
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.TimedInstantiation;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.serializing.SerializerAndDeserializer;
import de.quantummaid.usecasemaid.sideeffects.SideEffectInstance;
import de.quantummaid.usecasemaid.sideeffects.SideEffectsSystem;
import de.quantummaid.usecasemaid.sideeffects.collector.CollectorInstance;
import de.quantummaid.usecasemaid.usecasemethod.UseCaseMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.reflectmaid.GenericType.fromResolvedType;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.ReflectMaid.aReflectMaid;
import static de.quantummaid.usecasemaid.Invocation.invocation;
import static de.quantummaid.usecasemaid.InvocationId.randomInvocationId;
import static de.quantummaid.usecasemaid.RoutingTarget.routingTarget;
import static de.quantummaid.usecasemaid.UseCaseResult.error;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1181")
public final class UseCaseMaid {
    private final ReflectMaid reflectMaid;
    private final UseCases useCases;
    private final InjectMaid instantiator;
    private final SerializerAndDeserializer serializerAndDeserializer;
    private final SideEffectsSystem sideEffectsSystem;
    private final ExecutionDriver executionDriver;

    public static UseCaseMaidBuilder aUseCaseMaid() {
        final ReflectMaid reflectMaid = aReflectMaid();
        return aUseCaseMaid(reflectMaid);
    }

    public static UseCaseMaidBuilder aUseCaseMaid(final ReflectMaid reflectMaid) {
        return UseCaseMaidBuilder.useCaseMaidBuilder(reflectMaid);
    }

    static UseCaseMaid useCaseMaid(final ReflectMaid reflectMaid,
                                   final UseCases useCases,
                                   final InjectMaid instantiator,
                                   final SerializerAndDeserializer serializerAndDeserializer,
                                   final SideEffectsSystem sideEffectsSystem,
                                   final ExecutionDriver executionDriver) {
        return new UseCaseMaid(
                reflectMaid,
                useCases,
                instantiator,
                serializerAndDeserializer,
                sideEffectsSystem,
                executionDriver
        );
    }

    public UseCaseResult invoke(final Class<?> useCase) {
        final GenericType<?> genericType = genericType(useCase);
        return invoke(genericType);
    }

    public UseCaseResult invoke(final GenericType<?> useCase) {
        return invoke(useCase, Map.of());
    }

    public UseCaseResult invoke(final Class<?> useCase,
                                final Map<String, Object> input) {
        final GenericType<?> genericType = genericType(useCase);
        return invoke(genericType, input);
    }

    public UseCaseResult invoke(final GenericType<?> useCase,
                                final Map<String, Object> input) {
        final InvocationId invocationId = randomInvocationId();
        return invoke(useCase, input, invocationId);
    }

    public UseCaseResult invoke(final Class<?> useCase,
                                final Map<String, Object> input,
                                final Object additionalData) {
        final GenericType<?> genericType = genericType(useCase);
        return invoke(genericType, input, additionalData);
    }

    public UseCaseResult invoke(final GenericType<?> useCase,
                                final Map<String, Object> input,
                                final Object additionalData) {
        final InvocationId invocationId = randomInvocationId();
        return invoke(useCase, input, invocationId, additionalData);
    }

    public UseCaseResult invoke(final Class<?> useCase,
                                final Map<String, Object> input,
                                final InvocationId invocationId) {
        final GenericType<?> genericType = genericType(useCase);
        return invoke(genericType, input, invocationId);
    }

    public UseCaseResult invoke(final GenericType<?> useCase,
                                final Map<String, Object> input,
                                final InvocationId invocationId) {
        return invoke(useCase, input, invocationId, null);
    }

    public UseCaseResult invoke(final Class<?> useCase,
                                final Map<String, Object> input,
                                final InvocationId invocationId,
                                final Object additionalData) {
        final GenericType<?> genericType = genericType(useCase);
        return invoke(genericType, input, invocationId, additionalData);
    }

    public UseCaseResult invoke(final GenericType<?> useCase,
                                final Map<String, Object> input,
                                final InvocationId invocationId,
                                final Object additionalData) {
        final ResolvedType resolvedType = reflectMaid.resolve(useCase);
        final RoutingTarget routingTarget = routingTarget(resolvedType);
        return invoke(routingTarget, input, invocationId, additionalData);
    }

    public UseCaseResult invoke(final RoutingTarget routingTarget,
                                final Map<String, Object> input,
                                final InvocationId invocationId,
                                final Object additionalData) {
        return invoke(routingTarget, input, invocationId, additionalData, injector -> {
        });
    }

    public UseCaseResult invoke(final RoutingTarget routingTarget,
                                final Map<String, Object> input,
                                final InvocationId invocationId,
                                final Object additionalData,
                                final InjectorLambda injectorLambda) {
        final UseCaseMethod useCaseMethod = useCases.forRoutingTarget(routingTarget);
        final List<CollectorInstance<?, ?>> collectorInstances = sideEffectsSystem.createCollectorInstances(reflectMaid);
        final Map<String, Object> parameters = serializerAndDeserializer
                .deserializeParameters(input, useCaseMethod, injector -> {
                    injectorLambda.setupInjector(injector);
                    collectorInstances.forEach(instance ->
                            injector.put(instance.collectorType(), instance.collectorInstance()));
                });
        final Invocation invocation = invocation(invocationId, useCaseMethod.useCaseClass(), parameters, additionalData);
        final ResultAndSideEffects resultAndSideEffects = executionDriver.executeUseCase(invocation, instantiator, scopedInjector -> {
            final GenericType<Object> objectType = fromResolvedType(useCaseMethod.useCaseClass());
            final TimedInstantiation<Object> instanceWithInitializationTime = scopedInjector.getInstanceWithInitializationTime(objectType);

            final UseCaseResult result = invokeMethod(useCaseMethod, instanceWithInitializationTime, parameters);
            final List<SideEffectInstance<?>> collectedSideEffects = collectorInstances.stream()
                    .map(CollectorInstance::collectInstances)
                    .flatMap(Collection::stream)
                    .collect(toList());
            return ResultAndSideEffects.resultAndSideEffects(result, collectedSideEffects);
        });

        final List<SideEffectInstance<?>> sideEffects = resultAndSideEffects.sideEffects();
        executionDriver.executeSideEffects(invocation, sideEffects, instantiator, sideEffectsSystem);

        return resultAndSideEffects.result();
    }

    public InjectMaid instantiator() {
        return instantiator;
    }

    public MapMaid mapper() {
        return serializerAndDeserializer.mapMaid();
    }

    private UseCaseResult invokeMethod(final UseCaseMethod useCaseMethod,
                                       final TimedInstantiation<Object> timedUseCase,
                                       final Map<String, Object> parameters) {
        final Object useCase = timedUseCase.instance();
        final InstantiationTime instantiationTime = timedUseCase.instantiationTime();
        final Optional<Object> invocationResult;
        try {
            invocationResult = useCaseMethod.invoke(useCase, parameters);
        } catch (final Throwable e) {
            return error(e, instantiationTime);
        }
        return invocationResult
                .map(returnValue -> {
                    final ResolvedType returnType = useCaseMethod.returnType().orElseThrow();
                    return serializerAndDeserializer.serializeReturnValue(returnValue, returnType);
                })
                .map(returnValue -> UseCaseResult.successfulReturnValue(returnValue, instantiationTime))
                .orElseGet(() -> UseCaseResult.successfulVoid(instantiationTime));
    }

    public Collection<String> topLevelParameterNamesFor(final RoutingTarget routingTarget) {
        final UseCaseMethod useCaseMethod = useCases.forRoutingTarget(routingTarget);
        return useCaseMethod.parameters().keySet();
    }

    public void runStartupChecks() {
        useCases.all().forEach(useCaseMethod -> {
            final ResolvedType useCaseClass = useCaseMethod.useCaseClass();
            final Invocation invocation = invocation(randomInvocationId(), useCaseMethod.useCaseClass(), Map.of(), null);
            instantiator.enterScope(invocation).getInstance(useCaseClass);
        });
    }
}
