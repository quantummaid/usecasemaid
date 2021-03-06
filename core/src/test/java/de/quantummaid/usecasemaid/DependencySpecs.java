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
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.usecasemaid.driver.ExecutionDriver;
import de.quantummaid.usecasemaid.driver.UseCaseExecution;
import de.quantummaid.usecasemaid.usecases.Transaction;
import de.quantummaid.usecasemaid.usecases.UseCaseWithTransaction;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.injectmaid.api.customtype.api.CustomType.customType;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.usecases.Transaction.transactionOnDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

public final class DependencySpecs {

    @Test
    public void useCaseCanHaveATransaction() {
        final Map<String, String> database = new LinkedHashMap<>();

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithTransaction.class)
                .withInvocationScopedDependencies(builder ->
                        builder.withCustomType(Transaction.class, () -> transactionOnDatabase(database), ReusePolicy.DEFAULT_SINGLETON))
                .build();
        useCaseMaid.invoke(UseCaseWithTransaction.class, Map.of());

        assertThat(database, hasEntry("foo", "bar"));
        assertThat(database.size(), is(1));
    }

    @Test
    public void executionDriverCanAccessTransaction() {
        final Map<String, String> database = new LinkedHashMap<>();

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithTransaction.class)
                .withInvocationScopedDependencies(builder ->
                        builder.withCustomType(Transaction.class, () -> transactionOnDatabase(database), ReusePolicy.DEFAULT_SINGLETON)
                )
                .withExecutionDriver(new ExecutionDriver() {
                    @Override
                    public ResultAndSideEffects executeUseCase(final Invocation invocation,
                                                               final Injector injector,
                                                               final UseCaseExecution useCaseExecution) {
                        final Injector scopedInjector = injector.enterScope(Invocation.class, invocation);
                        final Transaction transaction = scopedInjector.getInstance(Transaction.class);
                        transaction.add("a", "b");
                        return useCaseExecution.executeUseCase(scopedInjector);
                    }
                })
                .build();
        useCaseMaid.invoke(UseCaseWithTransaction.class, Map.of());

        assertThat(database, hasEntry("foo", "bar"));
        assertThat(database, hasEntry("a", "b"));
        assertThat(database.size(), is(2));
    }

    @Test
    public void useCaseCanHaveAGlobalDependency() {
        final Map<String, String> database = new LinkedHashMap<>();

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithTransaction.class)
                .withDependencies(builder -> builder
                        .withCustomType(
                                customType(genericType(Map.class, String.class, String.class))
                                        .usingFactory(() -> database)))
                .withInvocationScopedDependencies(builder -> builder.withType(Transaction.class, ReusePolicy.DEFAULT_SINGLETON))
                .build();
        useCaseMaid.invoke(UseCaseWithTransaction.class, Map.of());

        assertThat(database, hasEntry("foo", "bar"));
        assertThat(database.size(), is(1));
    }
}
