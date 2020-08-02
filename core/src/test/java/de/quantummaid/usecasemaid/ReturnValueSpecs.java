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

import de.quantummaid.usecasemaid.usecases.CheckedExceptionThrowingUseCase;
import de.quantummaid.usecasemaid.usecases.RuntimeExceptionThrowingUseCase;
import de.quantummaid.usecasemaid.usecases.UseCaseWithReturnValue;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.givenwhenthen.Given.given;

public final class ReturnValueSpecs {

    @Test
    public void useCaseCanReturnValue() {
        given(
                aUseCaseMaid()
                        .invoking("test", UseCaseWithReturnValue.class)
                        .build()
        )
                .when().useCaseIsInvoked("test")
                .theReturnValueWas(
                        Map.of(
                                "field1", "a",
                                "field2", "b",
                                "field3", "c")
                );
    }

    @Test
    public void useCaseMaidReturnsCaughtRuntimeException() {
        given(
                aUseCaseMaid()
                        .invoking("test", RuntimeExceptionThrowingUseCase.class)
                        .build()
        )
                .when().useCaseIsInvoked("test")
                .theUseCaseThrewRuntimeExceptionWithMessage("from the usecase");
    }

    @Test
    public void useCaseMaidReturnsCaughtCheckedException() {
        given(
                aUseCaseMaid()
                        .invoking("test", CheckedExceptionThrowingUseCase.class)
                        .build()
        )
                .when().useCaseIsInvoked("test")
                .theUseCaseThrewCheckedExceptionWithMessage("from the usecase");
    }
}
