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

import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.usecasemaid.sideeffects.collector.SideEffectsCollector;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectorInstance<S, C> {
    private final C instance;
    private final SideEffectsCollector<S, C> collector;
    private final SideEffectExecutor<S> executor;

    @SuppressWarnings("unchecked")
    public static CollectorInstance<?, ?> createInstance(final SideEffectRegistration registration) {
        final SideEffectsCollector<Object, Object> collector = (SideEffectsCollector<Object, Object>) registration.collector();
        final Object instance = collector.createCollectorInstance();
        final SideEffectExecutor<Object> executor = (SideEffectExecutor<Object>) registration.executor();
        return collectorInstance(instance, collector, executor);
    }

    public static <S, C> CollectorInstance<S, C> collectorInstance(final C instance,
                                                                   final SideEffectsCollector<S, C> collector,
                                                                   final SideEffectExecutor<S> executor) {
        return new CollectorInstance<>(instance, collector, executor);
    }

    public Object instance() {
        return instance;
    }

    public ResolvedType type() {
        return collector.collectorType().toResolvedType();
    }

    public void executeAll() {
        final List<S> sideEffects = collector.unpackSideEffects(instance);
        sideEffects.forEach(executor::execute);
    }
}
