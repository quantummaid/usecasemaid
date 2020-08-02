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

import de.quantummaid.usecasemaid.UseCaseResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Then {
    private final TestData testData;

    static Then then(final TestData testData) {
        return new Then(testData);
    }

    public Then anExceptionHasBeenThrownDuringInitializationWithAMessageContaining(final String message) {
        final String actualMessage = testData.getInitializationException().getMessage();
        assertThat(actualMessage, CoreMatchers.containsString(message));
        return this;
    }

    public Then theReturnValueWas(final Object returnValue) {
        final UseCaseResult actual = testData.getReturnValue();
        assertThat(actual.wasSuccessful(), is(true));
        assertThat(actual.returnValue(), is(returnValue));
        return this;
    }

    public Then theUseCaseThrewRuntimeExceptionWithMessage(final String message) {
        return theUseCaseThrewExceptionWithMessage(RuntimeException.class, message);
    }

    public Then theUseCaseThrewCheckedExceptionWithMessage(final String message) {
        return theUseCaseThrewExceptionWithMessage(Exception.class, message);
    }

    private Then theUseCaseThrewExceptionWithMessage(final Class<? extends Throwable> exceptionType,
                                                     final String message) {
        final UseCaseResult actual = testData.getReturnValue();
        assertThat(actual.wasSuccessful(), is(false));
        final Throwable exception = actual.exception();
        assertThat(exception, instanceOf(exceptionType));
        assertThat(exception.getMessage(), is(message));
        return this;
    }
}
