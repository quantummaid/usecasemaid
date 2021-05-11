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

import de.quantummaid.usecasemaid.usecases.logging.Id;
import de.quantummaid.usecasemaid.usecases.logging.Logger;
import de.quantummaid.usecasemaid.usecases.logging.LoggingUseCase;
import de.quantummaid.usecasemaid.usecases.logging.Request;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public final class RequestAsInjectionSpecs {

    @Test
    public void useCaseCanDependOnRequest() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(LoggingUseCase.class)
                .withInvocationScopedDependencies(builder -> builder
                        .withCustomType(Id.class, Invocation.class, invocation -> {
                            final Map<String, Object> parameters = invocation.parameters();
                            final Request request = (Request) parameters.get("request");
                            return request.id;
                        }))
                .build();
        assertThat(Logger.LOG_MESSAGES, empty());
        useCaseMaid.invoke(LoggingUseCase.class, Map.of("request", Map.of(
                "id", "asdf",
                "field0", "x",
                "field1", "y"
                )
        ));
        assertThat(Logger.LOG_MESSAGES, contains("asdf: foo"));
    }
}
