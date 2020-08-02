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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.usecasemaid.UseCaseMaidException.useCaseMaidException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseResult {
    private final boolean noReturnValue;
    private final Object returnValue;
    private final Throwable exception;

    public static UseCaseResult successfulVoid() {
        return new UseCaseResult(true, null, null);
    }

    public static UseCaseResult successfulReturnValue(final Object returnValue) {
        return new UseCaseResult(false, returnValue, null);
    }

    public static UseCaseResult error(final Throwable exception) {
        return new UseCaseResult(true, null, exception);
    }

    public boolean hasReturnValue() {
        return !noReturnValue;
    }

    public boolean wasSuccessful() {
        return exception == null;
    }

    public Object returnValue() {
        if (exception != null) {
            throw useCaseMaidException("cannot provide a return value because the use case " +
                    "invocation threw an exception", exception);
        }
        if (noReturnValue) {
            throw useCaseMaidException("cannot provide a return value because the use case method was void");
        }
        return returnValue;
    }

    public Throwable exception() {
        return exception;
    }
}
