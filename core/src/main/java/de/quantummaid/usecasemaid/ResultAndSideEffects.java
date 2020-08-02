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

import de.quantummaid.usecasemaid.sideeffects.SideEffectInstance;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class ResultAndSideEffects {
    private final UseCaseResult result;
    private final List<SideEffectInstance<?>> sideEffects;

    public static ResultAndSideEffects resultAndSideEffects(final UseCaseResult result,
                                                            final List<SideEffectInstance<?>> sideEffects) {
        return new ResultAndSideEffects(result, sideEffects);
    }

    public UseCaseResult result() {
        return result;
    }

    public List<SideEffectInstance<?>> sideEffects() {
        return sideEffects;
    }
}
