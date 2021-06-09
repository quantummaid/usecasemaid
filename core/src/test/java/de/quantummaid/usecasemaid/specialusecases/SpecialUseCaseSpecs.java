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

package de.quantummaid.usecasemaid.specialusecases;

import de.quantummaid.usecasemaid.specialusecases.usecases.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.givenwhenthen.Given.given;

public final class SpecialUseCaseSpecs {

    @Test
    public void useCaseWithTwoMethods() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithTwoMethods.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("Use case classes must have exactly one public instance (non-static) method.");
    }

    @Test
    public void useCaseWithoutPublicMethods() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithoutPublicMethods.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("Use case classes must have exactly one public instance (non-static) method.");
    }

    @Test
    public void useCaseWithAdditionalPackagePrivateMethods() {
        given(
                aUseCaseMaid()
                        .invoking(UseCaseWithAdditionalPackagePrivateMethods.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseWithAdditionalPackagePrivateMethods.class)
                .theReturnValueWas("method2");
    }

    @Test
    public void packagePrivateUseCaseWithPublicMethod() {
        given(
                () -> aUseCaseMaid()
                        .invoking(PackagePrivateUseCaseWithPublicMethod.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case class must be public but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.PackagePrivateUseCaseWithPublicMethod'");
    }

    @Test
    public void packagePrivateUseCaseWithPackagePrivateMethod() {
        given(
                () -> aUseCaseMaid()
                        .invoking(PackagePrivateUseCaseWithPackagePrivateMethod.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case class must be public but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.PackagePrivateUseCaseWithPackagePrivateMethod'");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsDirectReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithClassScopeTypeVariableAsDirectReturnType.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "type 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithClassScopeTypeVariableAsDirectReturnType' " +
                        "contains the following type variables that need to be filled in in order to create a GenericType object: [T]");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsDirectReturnTypeWithGenericType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(genericType(UseCaseWithClassScopeTypeVariableAsDirectReturnType.class, String.class))
                        .build()
        )
                .when().useCaseIsInvoked(genericType(UseCaseWithClassScopeTypeVariableAsDirectReturnType.class, String.class))
                .theReturnValueWas("foo");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsIndirectReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithClassScopeTypeVariableAsIndirectReturnType.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "type 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithClassScopeTypeVariableAsIndirectReturnType' " +
                        "contains the following type variables that need to be filled in in order to create a GenericType object: [T]");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsIndirectReturnTypeWithGenericType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(genericType(UseCaseWithClassScopeTypeVariableAsIndirectReturnType.class, String.class))
                        .build()
        )
                .when().useCaseIsInvoked(genericType(UseCaseWithClassScopeTypeVariableAsIndirectReturnType.class, String.class))
                .theReturnValueWas(List.of("a", "b", "c"));
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsDirectParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithClassScopeTypeVariableAsDirectParameter.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("type 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithClassScopeTypeVariableAsDirectParameter' " +
                        "contains the following type variables that need to be filled in in order to create a GenericType object: [T]");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsDirectParameterRegisteredAsGenericType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(genericType(UseCaseWithClassScopeTypeVariableAsDirectParameter.class, String.class))
                        .build()
        )
                .when().useCaseIsInvoked(genericType(UseCaseWithClassScopeTypeVariableAsDirectParameter.class, String.class), Map.of("t", "foo"))
                .theReturnValueWas("foo");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsIndirectParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithClassScopeTypeVariableAsIndirectParameter.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "type 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithClassScopeTypeVariableAsIndirectParameter' " +
                        "contains the following type variables that need to be filled in in order to create a GenericType object: [T]");
    }

    @Test
    public void useCaseWithClassScopeTypeVariableAsIndirectParameterWithGenericType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(genericType(UseCaseWithClassScopeTypeVariableAsIndirectParameter.class, String.class))
                        .build()
        )
                .when().useCaseIsInvoked(genericType(UseCaseWithClassScopeTypeVariableAsIndirectParameter.class, String.class), Map.of("list", List.of("a", "b", "c")))
                .theReturnValueWas("{a, b, c}");
    }

    @Test
    public void useCaseWithMethodScopeTypeVariableAsDirectReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithMethodScopeTypeVariableAsDirectReturnType.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("Use case classes must have exactly one public instance (non-static) method. " +
                        "Found the methods [] for class 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithMethodScopeTypeVariableAsDirectReturnType'. " +
                        "(Note that methods that declare new type variables (\"generics\") are not taken into account)");
    }

    @Test
    public void useCaseWithMethodScopeTypeVariableAsIndirectReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithMethodScopeTypeVariableAsIndirectReturnType.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "Use case classes must have exactly one public instance (non-static) method. " +
                        "Found the methods [] for class 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithMethodScopeTypeVariableAsIndirectReturnType'. " +
                        "(Note that methods that declare new type variables (\"generics\") are not taken into account)");
    }

    @Test
    public void useCaseWithMethodScopeTypeVariableAsDirectParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithMethodScopeTypeVariableAsDirectParameter.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("Use case classes must have exactly one public instance (non-static) method. " +
                        "Found the methods [] for class 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithMethodScopeTypeVariableAsDirectParameter'. " +
                        "(Note that methods that declare new type variables (\"generics\") are not taken into account)");
    }

    @Test
    public void useCaseWithMethodScopeTypeVariableAsIndirectParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithMethodScopeTypeVariableAsIndirectParameter.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "Use case classes must have exactly one public instance (non-static) method. " +
                        "Found the methods [] for class 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithMethodScopeTypeVariableAsIndirectParameter'. " +
                        "(Note that methods that declare new type variables (\"generics\") are not taken into account)");
    }

    @Test
    public void useCaseWithWildcardInReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithWildcardInReturnType.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "The following classes could not be detected properly:\n" +
                        "\n" +
                        "?: unable to detect serialization-only:\n" +
                        "no serialization-only detected:\n" +
                        "[type '?' is not supported because it contains wildcard generics (\"?\")]\n" +
                        "\n" +
                        "?:\n" +
                        "Mode: serialization-only\n" +
                        "How it is serialized:\n" +
                        "\tNo serializer available\n" +
                        "Why it needs to be serializable:\n" +
                        "\t- java.util.List<?> -> because return type of method 'List<?> method()' [public java.util.List<? super java.lang.String> " +
                        "de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithWildcardInReturnType.method()]");
    }

    @Test
    public void useCaseWithGenericsInReturnType() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithGenericsInReturnType.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseWithGenericsInReturnType.class)
                .theReturnValueWas(List.of("a", "b", "c"));
    }

    @Test
    public void useCaseWithGenericsInParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithGenericsInParameter.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseWithGenericsInParameter.class, Map.of("parameter", List.of("a", "b", "c")))
                .theReturnValueWas("{a, b, c}");
    }

    @Test
    public void useCaseWithWildcardInParameter() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseWithWildcardInParameter.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("" +
                        "The following classes could not be detected properly:\n" +
                        "\n" +
                        "?: unable to detect deserialization-only:\n" +
                        "no deserialization-only detected:\n" +
                        "[type '?' is not supported because it contains wildcard generics (\"?\")]\n" +
                        "\n" +
                        "?:\n" +
                        "Mode: deserialization-only\n" +
                        "How it is deserialized:\n" +
                        "\tNo deserializer available\n" +
                        "Why it needs to be deserializable:\n" +
                        "\t- java.util.List<?> -> because parameter type of method 'void method(List<?> list)' [public void " +
                        "de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseWithWildcardInParameter.method(java.util.List<? super java.lang.String>)]");
    }

    @Test
    public void useCaseThatIsAnInterface() {
        given(
                aUseCaseMaid()
                        .invoking(UseCaseThatIsAnInterface.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseThatIsAnInterface.class)
                .theReturnValueWas("method");
    }

    @Test
    public void useCaseThatIsAnAbstractClass() {
        given(
                aUseCaseMaid()
                        .invoking(UseCaseThatIsAnAbstractClass.class)
                        .build()
        )
                .when().useCaseIsInvoked(UseCaseThatIsAnAbstractClass.class)
                .theReturnValueWas("method1");
    }

    @Test
    public void useCaseThatIsAnEnum() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseThatIsAnEnum.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an enum but got 'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseThatIsAnEnum'");
    }

    @Test
    public void useCaseThatIsAnAnonymousClass() {
        final Class<?> useCaseClass = new UseCaseThatIsAnInterface() {
        }.getClass();
        given(
                () -> aUseCaseMaid()
                        .invoking(useCaseClass)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an anonymous class but got 'de.quantummaid.usecasemaid.specialusecases.SpecialUseCaseSpecs$");
    }

    @Test
    public void useCaseThatIsAnInnerClass() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseThatIsAnInnerClass.NonStaticInnerClass.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an inner class but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseThatIsAnInnerClass$NonStaticInnerClass'");
    }

    @Test
    public void useCaseThatIsAStaticInnerClass() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseThatIsAnInnerClass.StaticInnerClass.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an inner class but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseThatIsAnInnerClass$StaticInnerClass'");
    }

    @Test
    public void useCaseThatIsALocalClass() {
        class UseCase {
            public String method() {
                return "method";
            }
        }

        given(
                () -> aUseCaseMaid()
                        .invoking(UseCase.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be a local class but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.SpecialUseCaseSpecs$1UseCase'");
    }

    @Test
    public void useCaseThatIsAPrimitive() {
        given(
                () -> aUseCaseMaid()
                        .invoking(int.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be a primitive but got 'int'");
    }

    @Test
    public void useCaseThatIsAnAnnotation() {
        given(
                () -> aUseCaseMaid()
                        .invoking(UseCaseThatIsAnAnnotation.class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an annotation but got " +
                        "'de.quantummaid.usecasemaid.specialusecases.usecases.UseCaseThatIsAnAnnotation'");
    }

    @Test
    public void useCaseThatIsAnArray() {
        given(
                () -> aUseCaseMaid()
                        .invoking(String[].class)
                        .build()
        )
                .when().useCaseMaidIsInitialized()
                .anExceptionHasBeenThrownDuringInitializationWithAMessageContaining("use case must not be an array but got 'java.lang.String[]'");
    }
}
