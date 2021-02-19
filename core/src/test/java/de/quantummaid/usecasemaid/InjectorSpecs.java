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
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.injectmaid.timing.InstantiationTimes;
import de.quantummaid.usecasemaid.usecases.Transaction;
import de.quantummaid.usecasemaid.usecases.UseCaseWithTransaction;
import de.quantummaid.usecasemaid.usecases.UseCaseWithoutParameters;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.usecases.Transaction.transactionOnDatabase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public final class InjectorSpecs {

    @Test
    public void injectorCanBeQueried() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithoutParameters.class)
                .build();
        final InjectMaid instantiator = useCaseMaid.instantiator();
        assertThat(instantiator, is(notNullValue()));
    }

    @Test
    public void instantiationTimesCanBeQueriedFromInjector() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .withDependencies(builder -> builder.withCustomType(String.class, () -> {
                    try {
                        Thread.sleep(100);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "foo";
                }, ReusePolicy.EAGER_SINGLETON))
                .invoking(UseCaseWithoutParameters.class)
                .build();
        final InjectMaid instantiator = useCaseMaid.instantiator();
        final InstantiationTimes instantiationTimes = instantiator.instantiationTimes();
        final InstantiationTime instantiationTime = instantiationTimes.initializationTimeFor(String.class);
        assertThat(instantiationTime.timeInMilliseconds(), is(greaterThanOrEqualTo(100L)));
    }

    @Test
    public void instantiationTimesAreReturnedPerUseCase() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .withInvocationScopedDependencies(builder ->
                        builder.withCustomType(Transaction.class, () -> {
                            try {
                                Thread.sleep(100);
                            } catch (final InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return transactionOnDatabase(new HashMap<>());
                        }))
                .invoking(UseCaseWithTransaction.class)
                .build();

        final UseCaseResult useCaseResult = useCaseMaid.invoke(UseCaseWithTransaction.class, Map.of());
        final InstantiationTime instantiationTime = useCaseResult.instantiationTime();
        assertThat(instantiationTime.timeInMilliseconds(), is(greaterThanOrEqualTo(100L)));
    }
}
