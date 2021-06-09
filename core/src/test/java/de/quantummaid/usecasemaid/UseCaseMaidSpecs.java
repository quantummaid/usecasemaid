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

import de.quantummaid.injectmaid.api.Injector;
import de.quantummaid.injectmaid.timing.InstantiationTime;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.driver.UseCaseExecution;
import de.quantummaid.usecasemaid.usecases.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.quantummaid.injectmaid.timing.InstantiationTime.instantiationTime;
import static de.quantummaid.reflectmaid.ReflectMaid.aReflectMaid;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.usecasemaid.ResultAndSideEffects.resultAndSideEffects;
import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.UseCaseResult.successfulVoid;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectInstance.sideEffectInstance;
import static de.quantummaid.usecasemaid.usecases.MySideEffect.mySideEffect;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class UseCaseMaidSpecs {
    private static final InstantiationTime INSTANTIATION_TIME =
            instantiationTime(typeIdentifierFor(aReflectMaid().resolve(String.class)), 0L);

    @Test
    public void useCaseWithoutParametersCanBeInvoked() {
        UseCaseWithoutParameters.INVOCATION_COUNT = 0;

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithoutParameters.class)
                .build();

        useCaseMaid.invoke(UseCaseWithoutParameters.class);

        assertThat(UseCaseWithoutParameters.INVOCATION_COUNT, is(1));
    }

    @Test
    public void useCaseWithParametersCanBeInvoked() {

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithParameters.class)
                .build();

        useCaseMaid.invoke(UseCaseWithParameters.class, Map.of(
                "myDto", Map.of(
                        "field1", "a",
                        "field2", "b",
                        "field3", "c")
                )
        );

        assertThat(UseCaseWithParameters.LAST_PARAMETER, is(MyDto.myDto("a", "b", "c")));
    }

    @Test
    public void useCaseCanHaveSideEffects() {
        final List<String> executedSideEffects = new ArrayList<>();
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithSideEffects.class)
                .withSideEffects(MySideEffect.class, sideEffect -> {
                    final String value = sideEffect.getValue();
                    executedSideEffects.add(value);
                })
                .build();
        useCaseMaid.invoke(UseCaseWithSideEffects.class, Map.of());
        assertThat(executedSideEffects, contains("the correct side effect"));
    }

    @Test
    public void businessLogicExecutionCanBeControlledByDriver() {
        UseCaseWithoutParameters.INVOCATION_COUNT = 0;
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithoutParameters.class)
                .withExecutionDriver(new ExecutionDriver() {
                    @Override
                    public ResultAndSideEffects executeUseCase(final Invocation invocationId,
                                                               final Injector injector,
                                                               final UseCaseExecution useCaseExecution) {
                        return resultAndSideEffects(successfulVoid(INSTANTIATION_TIME),
                                emptyList());
                    }
                })
                .build();
        useCaseMaid.invoke(UseCaseWithoutParameters.class, Map.of());
        assertThat(UseCaseWithoutParameters.INVOCATION_COUNT, is(0));
    }

    @Test
    public void sideEffectsCanBeProvidedByDriver() {
        final ReflectMaid reflectMaid = ReflectMaid.aReflectMaid();
        final List<String> executedSideEffects = new ArrayList<>();
        final UseCaseMaid useCaseMaid = aUseCaseMaid(reflectMaid)
                .invoking(UseCaseWithSideEffects.class)
                .withSideEffects(MySideEffect.class, sideEffect -> {
                    final String value = sideEffect.getValue();
                    executedSideEffects.add(value);
                })
                .withExecutionDriver(new ExecutionDriver() {
                    @Override
                    public ResultAndSideEffects executeUseCase(final Invocation invocationId,
                                                               final Injector injector,
                                                               final UseCaseExecution useCaseExecution) {
                        return resultAndSideEffects(successfulVoid(INSTANTIATION_TIME), List.of(
                                sideEffectInstance(reflectMaid, mySideEffect("the overwritten side effect"))
                        ));
                    }
                })
                .build();
        useCaseMaid.invoke(UseCaseWithSideEffects.class, Map.of());
        assertThat(executedSideEffects, contains("the overwritten side effect"));
    }

    @Test
    public void useCaseWithSameDtoInRequestAndResponse() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithSameDtoInRequestAndResponse.class)
                .build();

        final UseCaseResult response = useCaseMaid.invoke(UseCaseWithSameDtoInRequestAndResponse.class, Map.of(
                "id", "abc",
                "dto", Map.of(
                        "field1", "a",
                        "field2", "b",
                        "field3", "c"
                )
        ));
        assertThat(response.wasSuccessful(), is(true));
        assertThat(response.returnValue(), is(Map.of(
                "id", "abc",
                "value", Map.of(
                        "field1", "a",
                        "field2", "b",
                        "field3", "c"
                )
        )));
    }

}
