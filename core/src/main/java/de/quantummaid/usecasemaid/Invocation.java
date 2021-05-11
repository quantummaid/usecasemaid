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

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Invocation {
    private final InvocationId invocationId;
    private final ResolvedType useCase;
    private final Map<String, Object> parameters;
    private final Object additionalData;

    public static Invocation invocation(final InvocationId invocationId,
                                        final ResolvedType useCase,
                                        final Map<String, Object> parameters,
                                        final Object additionalData) {
        return new Invocation(invocationId, useCase, parameters, additionalData);
    }

    public InvocationId id() {
        return invocationId;
    }

    public Map<String, Object> parameters() {
        return parameters;
    }

    public Object additionalData() {
        return additionalData;
    }

    public ResolvedType useCase() {
        return useCase;
    }
}
