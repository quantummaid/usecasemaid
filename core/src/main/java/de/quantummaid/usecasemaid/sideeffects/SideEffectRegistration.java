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

import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.usecasemaid.sideeffects.collector.ListSideEffectsCollector;
import de.quantummaid.usecasemaid.sideeffects.collector.SideEffectsCollector;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.usecasemaid.sideeffects.collector.ListSideEffectsCollector.sideEffectsCollector;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SideEffectRegistration {
    private final GenericType<?> sideEffectType;
    private final SideEffectsCollector<?, ?> sideEffectsCollector;
    private final SideEffectExecutor<Object> sideEffectExecutor;

    @SuppressWarnings("unchecked")
    public static SideEffectRegistration sideEffectRegistration(final GenericType<?> sideEffectType,
                                                                final SideEffectExecutor<?> sideEffectExecutor) {
        final ListSideEffectsCollector<?> sideEffectsCollector = sideEffectsCollector(sideEffectType);
        return new SideEffectRegistration(sideEffectType, sideEffectsCollector, (SideEffectExecutor<Object>) sideEffectExecutor);
    }

    public GenericType<?> type() {
        return sideEffectType;
    }

    public SideEffectsCollector<?, ?> collector() {
        return sideEffectsCollector;
    }

    public SideEffectExecutor<Object> executor() {
        return sideEffectExecutor;
    }
}
