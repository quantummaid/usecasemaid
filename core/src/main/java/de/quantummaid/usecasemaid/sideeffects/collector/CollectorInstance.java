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

package de.quantummaid.usecasemaid.sideeffects.collector;

import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.usecasemaid.sideeffects.SideEffectInstance;
import de.quantummaid.usecasemaid.sideeffects.SideEffectRegistration;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.usecasemaid.sideeffects.SideEffectInstance.sideEffectInstance;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class CollectorInstance<C, S> {
    private final SideEffectsCollector<S, C> collector;
    private final C collectorInstance;
    private final ResolvedType collectorType;
    private final ResolvedType sideEffectType;

    @SuppressWarnings("unchecked")
    public static CollectorInstance<?, ?> createInstance(final SideEffectRegistration registration) {
        final SideEffectsCollector<Object, Object> collector = (SideEffectsCollector<Object, Object>) registration.collector();
        final Object instance = collector.createCollectorInstance();
        return new CollectorInstance<>(
                collector,
                instance,
                collector.collectorType().toResolvedType(),
                registration.type().toResolvedType()
        );
    }

    public C collectorInstance() {
        return collectorInstance;
    }

    public ResolvedType collectorType() {
        return collectorType;
    }

    public List<SideEffectInstance<S>> collectInstances() {
        return collector.unpackSideEffects(collectorInstance).stream()
                .map(sideEffect -> sideEffectInstance(sideEffect, sideEffectType))
                .collect(toList());
    }
}
