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

package de.quantummaid.usecasemaid.usecasemethod;

import java.lang.reflect.Method;
import java.util.Map;

public final class MethodInvocationException extends RuntimeException {
    private MethodInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static MethodInvocationException methodInvocationException(final Class<?> useCaseClass,
                                                                      final Object useCase,
                                                                      final Method useCaseMethod,
                                                                      final Map<String, Object> parameters,
                                                                      final Exception cause) {
        final String message = String.format("Could not call method '%s' of class '%s' with arg '%s' on object '%s'",
                useCaseMethod,
                useCaseClass,
                parameters,
                useCase);
        return new MethodInvocationException(message, cause);
    }
}
