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

package de.quantummaid.usecasemaid.givenwhenthen;

import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.usecasemaid.UseCaseMaid;
import de.quantummaid.usecasemaid.UseCaseResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class When {
    private final TestData testData;

    static When when(final TestData testData) {
        return new When(testData);
    }

    public Then useCaseMaidIsInitialized() {
        return Then.then(testData);
    }

    public Then useCaseIsInvoked(final Class<?> useCase) {
        return useCaseIsInvoked(useCase, Map.of());
    }

    public Then useCaseIsInvoked(final GenericType<?> useCase) {
        return useCaseIsInvoked(useCase, Map.of());
    }

    public Then useCaseIsInvoked(final Class<?> useCase,
                                 final Map<String, Object> body) {
        final GenericType<?> genericType = GenericType.genericType(useCase);
        return useCaseIsInvoked(genericType, body);
    }

    public Then useCaseIsInvoked(final GenericType<?> useCase,
                                 final Map<String, Object> body) {
        final UseCaseMaid useCaseMaid = testData.getUseCaseMaid();
        final UseCaseResult result = useCaseMaid.invoke(useCase, body);
        testData.setReturnValue(result);
        return Then.then(testData);
    }
}
