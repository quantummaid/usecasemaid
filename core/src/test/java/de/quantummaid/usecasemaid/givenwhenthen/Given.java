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

import de.quantummaid.usecasemaid.UseCaseMaid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Given {
    private final Supplier<UseCaseMaid> useCaseMaidSupplier;

    public static Given given(final Supplier<UseCaseMaid> useCaseMaid) {
        return new Given(useCaseMaid);
    }

    public static Given given(final UseCaseMaid useCaseMaid) {
        return given(() -> useCaseMaid);
    }

    public When when() {
        final TestData testData = new TestData();
        try {
            final UseCaseMaid useCaseMaid = useCaseMaidSupplier.get();
            testData.setUseCaseMaid(useCaseMaid);
        } catch (final Exception e) {
            testData.setInitializationException(e);
        }
        return When.when(testData);
    }
}
