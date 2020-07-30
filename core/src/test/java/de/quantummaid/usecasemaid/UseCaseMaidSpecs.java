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

import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.driver.UseCaseExecution;
import de.quantummaid.usecasemaid.sideeffects.SideEffectInstance;
import de.quantummaid.usecasemaid.usecases.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.sideeffects.SideEffectInstance.sideEffectInstance;
import static de.quantummaid.usecasemaid.usecases.MySideEffect.mySideEffect;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class UseCaseMaidSpecs {

    @Test
    public void useCaseWithoutParametersCanBeInvoked() {
        UseCaseWithoutParameters.INVOCATION_COUNT = 0;
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking("test", UseCaseWithoutParameters.class)
                .build();
        useCaseMaid.invoke("test", Map.of());
        assertThat(UseCaseWithoutParameters.INVOCATION_COUNT, is(1));
    }

    @Test
    public void useCaseWithParametersCanBeInvoked() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking("test", UseCaseWithParameters.class)
                .build();
        useCaseMaid.invoke("test", Map.of(
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
                .invoking("test", UseCaseWithSideEffects.class)
                .withSideEffects(MySideEffect.class, sideEffect -> {
                    final String value = sideEffect.getValue();
                    executedSideEffects.add(value);
                })
                .build();
        useCaseMaid.invoke("test", Map.of());
        assertThat(executedSideEffects, contains("the correct side effect"));
    }

    @Test
    public void businessLogicExecutionCanBeControlledByDriver() {
        UseCaseWithoutParameters.INVOCATION_COUNT = 0;
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking("test", UseCaseWithoutParameters.class)
                .withExecutionDriver(new ExecutionDriver() {
                    @Override
                    public List<SideEffectInstance<?>> bypassUseCaseExecution(final InvocationId invocationId,
                                                                              final UseCaseExecution useCaseExecution) {
                        return emptyList();
                    }
                })
                .build();
        useCaseMaid.invoke("test", Map.of());
        assertThat(UseCaseWithoutParameters.INVOCATION_COUNT, is(0));
    }

    @Test
    public void sideEffectsCanBeProvidedByDriver() {
        final List<String> executedSideEffects = new ArrayList<>();
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking("test", UseCaseWithSideEffects.class)
                .withSideEffects(MySideEffect.class, sideEffect -> {
                    final String value = sideEffect.getValue();
                    executedSideEffects.add(value);
                })
                .withExecutionDriver(new ExecutionDriver() {
                    @Override
                    public List<SideEffectInstance<?>> bypassUseCaseExecution(final InvocationId invocationId,
                                                                              final UseCaseExecution useCaseExecution) {
                        return List.of(
                                sideEffectInstance(mySideEffect("the overwritten side effect"))
                        );
                    }
                })
                .build();
        useCaseMaid.invoke("test", Map.of());
        assertThat(executedSideEffects, contains("the overwritten side effect"));
    }
}
