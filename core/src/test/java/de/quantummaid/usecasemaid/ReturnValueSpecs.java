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
import de.quantummaid.usecasemaid.usecases.UseCaseWithoutReturnValue;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.givenwhenthen.Given.given;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ReturnValueSpecs {

    @Test
    public void useCaseCanReturnValue() {
        given(
                aUseCaseMaid()
                        .invoking(UseCaseWithReturnValue.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseWithReturnValue.class)
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
                        .invoking(RuntimeExceptionThrowingUseCase.class)
                        .build()
        )
                .when().useCaseIsInvoked(RuntimeExceptionThrowingUseCase.class)
                .theUseCaseThrewRuntimeExceptionWithMessage("from the usecase");
    }

    @Test
    public void useCaseMaidReturnsCaughtCheckedException() {
        given(
                aUseCaseMaid()
                        .invoking(CheckedExceptionThrowingUseCase.class)
                        .build()
        )
                .when().useCaseIsInvoked(CheckedExceptionThrowingUseCase.class)
                .theUseCaseThrewCheckedExceptionWithMessage("from the usecase");
    }

    @Test
    public void queryingTheReturnValueOfVoidUseCaseThrowsException() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithoutReturnValue.class)
                .build();

        final UseCaseResult result = useCaseMaid.invoke(UseCaseWithoutReturnValue.class);
        Exception exception = null;
        try {
            result.returnValue();
        } catch (final UseCaseMaidException e) {
            exception = e;
        }
        assertThat(exception, instanceOf(UseCaseMaidException.class));
        assertThat(exception.getMessage(), is("cannot provide a return value because the use case method was void"));
    }
}
