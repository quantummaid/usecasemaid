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

package de.quantummaid.usecasemaid.sideeffects;

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.usecasemaid.sideeffects.collector.CollectorInstance;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static de.quantummaid.usecasemaid.sideeffects.collector.CollectorInstance.createInstance;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class SideEffectsSystem {
    private final Map<ResolvedType, SideEffectRegistration> sideEffectRegistrations;

    public static SideEffectsSystem sideEffectsSystem(final Map<ResolvedType, SideEffectRegistration> sideEffectRegistrations) {
        return new SideEffectsSystem(sideEffectRegistrations);
    }

    public void execute(final SideEffectInstance<?> sideEffect) {
        final ResolvedType type = sideEffect.type();
        final SideEffectRegistration registration = this.sideEffectRegistrations.get(type);
        registration.executor().execute(sideEffect.sideEffect());
    }

    public List<CollectorInstance<?, ?>> createCollectorInstances(final ReflectMaid reflectMaid) {
        return sideEffectRegistrations.values().stream()
                .map(sideEffectRegistration -> createInstance(reflectMaid, sideEffectRegistration))
                .collect(toList());
    }
}
