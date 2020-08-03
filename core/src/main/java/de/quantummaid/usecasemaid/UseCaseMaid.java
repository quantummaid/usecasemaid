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
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
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

import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.InvocationId.randomInvocationId;
import static de.quantummaid.usecasemaid.UseCaseResult.error;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1181")
public final class UseCaseMaid {
    private final UseCases useCases;
    private final InjectMaid instantiator;
    private final SerializerAndDeserializer serializerAndDeserializer;
    private final SideEffectsSystem sideEffectsSystem;
    private final ExecutionDriver executionDriver;

    public static UseCaseMaidBuilder aUseCaseMaid() {
        return UseCaseMaidBuilder.useCaseMaidBuilder();
    }

    static UseCaseMaid useCaseMaid(final UseCases useCases,
                                   final InjectMaid instantiator,
                                   final SerializerAndDeserializer serializerAndDeserializer,
                                   final SideEffectsSystem sideEffectsSystem,
                                   final ExecutionDriver executionDriver) {
        return new UseCaseMaid(useCases,
                instantiator,
                serializerAndDeserializer,
                sideEffectsSystem,
                executionDriver);
    }

    public UseCaseResult invoke(final Class<?> useCase,
                                final Map<String, Object> input) {
        final InvocationId invocationId = randomInvocationId();
        return invoke(useCase, input, invocationId);
    }

    public UseCaseResult invoke(final GenericType<?> useCase,
                                final Map<String, Object> input) {
        final InvocationId invocationId = randomInvocationId();
        return invoke(useCase, input, invocationId);
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
        final UseCaseMethod useCaseMethod = useCases.forUseCase(useCase);
        final ResultAndSideEffects resultAndSideEffects = executionDriver.executeUseCase(invocationId, instantiator, scopedInjector -> {
            final List<CollectorInstance<?, ?>> collectorInstances = sideEffectsSystem.createCollectorInstances();
            final ResolvedType objectType = useCaseMethod.useCaseClass();
            final Object useCaseInstance = scopedInjector.getInstance(objectType);
            final Map<String, Object> parameters = serializerAndDeserializer
                    .deserializeParameters(input, useCaseMethod, injector ->
                            collectorInstances.forEach(instance ->
                                    injector.put(instance.collectorType(), instance.collectorInstance())));
            final UseCaseResult result = invokeMethod(useCaseMethod, useCaseInstance, parameters);
            final List<SideEffectInstance<?>> collectedSideEffects = collectorInstances.stream()
                    .map(CollectorInstance::collectInstances)
                    .flatMap(Collection::stream)
                    .collect(toList());
            return ResultAndSideEffects.resultAndSideEffects(result, collectedSideEffects);
        });

        final List<SideEffectInstance<?>> sideEffects = resultAndSideEffects.sideEffects();
        executionDriver.executeSideEffects(invocationId, sideEffects, instantiator, sideEffectsSystem);

        return resultAndSideEffects.result();
    }

    private UseCaseResult invokeMethod(final UseCaseMethod useCaseMethod,
                                       final Object useCase,
                                       final Map<String, Object> parameters) {
        final Optional<Object> invocationResult;
        try {
            invocationResult = useCaseMethod.invoke(useCase, parameters);
        } catch (final Throwable e) {
            return error(e);
        }
        return invocationResult
                .map(returnValue -> {
                    final ResolvedType returnType = useCaseMethod.returnType().orElseThrow();
                    return serializerAndDeserializer.serializeReturnValue(returnValue, returnType);
                })
                .map(UseCaseResult::successfulReturnValue)
                .orElseGet(UseCaseResult::successfulVoid);
    }
}
