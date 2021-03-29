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

import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import de.quantummaid.usecasemaid.UseCaseMaidException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static de.quantummaid.usecasemaid.UseCaseMaidException.useCaseMaidException;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S112")
public final class UseCaseMethod {
    public static final Collection<String> NOT_ALLOWED_USECASE_PUBLIC_METHODS = Set.of("equals", "hashCode", "toString", "clone", "finalize", "wait", "getClass", "notify", "notifyAll");

    private final ResolvedType useCaseClass;
    private final ResolvedMethod method;
    private final Parameters parameters;

    public static UseCaseMethod useCaseMethodOf(final ResolvedType useCase) {
        validateUseCaseClass(useCase);
        final ResolvedMethod method = locateUseCaseMethod((ClassType) useCase);
        final Parameters parameters = Parameters.parametersOf(method);
        return new UseCaseMethod(useCase, method, parameters);
    }

    public ResolvedType useCaseClass() {
        return useCaseClass;
    }

    public Map<String, ResolvedType> parameters() {
        return parameters.asMap();
    }

    public Optional<ResolvedType> returnType() {
        return method.returnType();
    }

    public String describe() {
        return method.describe();
    }

    public Optional<Object> invoke(final Object useCase,
                                   final Map<String, Object> parameters) throws Exception {
        final Object[] parameterInstances = this.parameters.toArray(parameters);
        final Method lowLevelMethod = this.method.getMethod();
        try {
            final Object returnValue = lowLevelMethod.invoke(useCase, parameterInstances);
            return ofNullable(returnValue);
        } catch (final IllegalAccessException e) {
            throw methodInvocationException(useCase.getClass(), useCase, lowLevelMethod, parameters, e);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (isDeclaredByMethod(cause, lowLevelMethod)) {
                throw (Exception) cause;
            } else {
                throw methodInvocationException(useCase.getClass(), useCase, lowLevelMethod, parameters, e);
            }
        }
    }

    private static UseCaseMaidException methodInvocationException(final Class<?> useCaseClass,
                                                                  final Object useCase,
                                                                  final Method useCaseMethod,
                                                                  final Map<String, Object> parameters,
                                                                  final Exception cause) {
        final String message = String.format("Could not call method '%s' of class '%s' with arg '%s' on object '%s'",
                useCaseMethod,
                useCaseClass,
                parameters,
                useCase);
        return useCaseMaidException(message, cause);
    }

    private boolean isDeclaredByMethod(final Throwable cause, final Method method) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        final Class<? extends Throwable> exceptionClass = cause.getClass();
        return Arrays.asList(exceptionTypes).contains(exceptionClass);
    }

    private static ResolvedMethod locateUseCaseMethod(final ClassType useCaseClass) {
        final List<ResolvedMethod> useCaseMethods = getAllPublicMethods(useCaseClass, NOT_ALLOWED_USECASE_PUBLIC_METHODS);
        if (useCaseMethods.size() == 1) {
            return useCaseMethods.get(0);
        } else {
            final String methods = useCaseMethods.stream()
                    .map(ResolvedMethod::describe)
                    .collect(joining(", ", "[", "]"));
            final String message = format("Use case classes must have exactly one public instance (non-static) method. Found the methods %s " +
                            "for class '%s'. (Note that methods that declare new type variables (\"generics\") are not taken into account)",
                    methods, useCaseClass.description());
            throw new IllegalArgumentException(message);
        }
    }

    private static List<ResolvedMethod> getAllPublicMethods(final ClassType useCaseClass, final Collection<String> excludedMethods) {
        return useCaseClass.methods().stream()
                .filter(ResolvedMethod::isPublic)
                .filter(method -> !Modifier.isStatic(method.getMethod().getModifiers()))
                .filter(method -> !isAbstract(method.getMethod().getModifiers()))
                .filter(method -> method.getMethod().getDeclaringClass().equals(useCaseClass.assignableType()))
                .filter(method -> !excludedMethods.contains(method.name()))
                .collect(toList());
    }

    private static void validateUseCaseClass(final ResolvedType useCase) {
        validateNotAnonymousClass(useCase);
        validateNotLocalClass(useCase);
        validatePublicClass(useCase);
        validateNotPrimitiveClass(useCase);
        validateNotArrayClass(useCase);
        validateNotAnnotationClass(useCase);
        validateNotEnumClass(useCase);
        validateNotInnerClass(useCase);
    }

    private static void validateNotAnonymousClass(final ResolvedType useCase) {
        if (useCase.isAnonymousClass()) {
            throw new IllegalArgumentException(format("use case must not be an anonymous class but got '%s'", useCase.description()));
        }
    }

    private static void validateNotLocalClass(final ResolvedType useCase) {
        if (useCase.isLocalClass()) {
            throw new IllegalArgumentException(format("use case must not be a local class but got '%s'", useCase.description()));
        }
    }

    private static void validatePublicClass(final ResolvedType useCase) {
        if (!useCase.isPublic()) {
            throw new IllegalArgumentException(format("use case class must be public but got '%s'", useCase.description()));
        }
    }

    private static void validateNotPrimitiveClass(final ResolvedType useCase) {
        if (useCase.assignableType().isPrimitive()) {
            throw new IllegalArgumentException(format("use case must not be a primitive but got '%s'", useCase.description()));
        }
    }

    private static void validateNotArrayClass(final ResolvedType useCase) {
        if (useCase.assignableType().isArray()) {
            throw new IllegalArgumentException(format("use case must not be an array but got '%s'", useCase.description()));
        }
    }

    private static void validateNotAnnotationClass(final ResolvedType useCase) {
        if (useCase.isAnnotation()) {
            throw new IllegalArgumentException(format("use case must not be an annotation but got '%s'", useCase.description()));
        }
    }

    private static void validateNotEnumClass(final ResolvedType useCase) {
        if (useCase.assignableType().isEnum()) {
            throw new IllegalArgumentException(format("use case must not be an enum but got '%s'", useCase.description()));
        }
    }

    private static void validateNotInnerClass(final ResolvedType useCase) {
        if (useCase.isInnerClass()) {
            throw new IllegalArgumentException(format("use case must not be an inner class but got '%s'", useCase.description()));
        }
    }
}
