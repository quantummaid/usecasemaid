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

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.usecasemaid.specialusecases.usecases.symmetric.Primitive;
import de.quantummaid.usecasemaid.specialusecases.usecases.symmetric.UseCaseRequiringSymmetricMapperConfiguration;
import de.quantummaid.usecasemaid.usecases.UseCaseWithPseudoPrimitives;
import de.quantummaid.usecasemaid.usecases.UseCaseWithoutParameters;
import de.quantummaid.usecasemaid.usecases.domain.PseudoPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.usecases.domain.PseudoPrimitive.pseudoPrimitive;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class MapperSpecs {

    @Test
    public void mapperCanBeQueried() {
        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking(UseCaseWithoutParameters.class)
                .build();
        final MapMaid mapper = useCaseMaid.mapper();
        assertThat(mapper, is(notNullValue()));
    }

    @Test
    public void customMappingsCanBeRegistered() {
        final UseCaseMaid useCaseMaid = UseCaseMaid.aUseCaseMaid()
                .invoking(UseCaseWithPseudoPrimitives.class)
                .withMapperConfiguration(mapMaidBuilder -> mapMaidBuilder.deserializingStringBasedCustomPrimitive(
                                PseudoPrimitive.class,
                                value -> {
                                    final String[] parts = value.split(":");
                                    return pseudoPrimitive(parts[0], parts[1]);
                                }))
                .build();

        final UseCaseResult result = useCaseMaid.invoke(UseCaseWithPseudoPrimitives.class, Map.of(
                "dtoWithPseudoPrimitives", Map.of(
                        "field1", "a:b",
                        "field2", "c:d",
                        "field3", "e:f"
                )
                )
        );

        final Object returnValue = result.returnValue();
        assertThat(returnValue, is("a-b c-d e-f"));
    }

    /**
     * Getting the unhelpful:
     * <p>
     * java.lang.NullPointerException
     * at de.quantummaid.mapmaid.builder.resolving.MapMaidResolver.addSignalsOfSerializer(MapMaidResolver.java:78)
     */
    @Test
    public void complainsWhenObjectUsedInRequestAndResponseIsNotConfiguredForDuplexSerialization() {
        aUseCaseMaid().invoking(UseCaseRequiringSymmetricMapperConfiguration.class)
            // Here, notice we forget mapper.serializing... or mapper.serializingAndDeserializing...
            .withMapperConfiguration(mapper -> mapper.deserializingStringBasedCustomPrimitive(
                Primitive.class,
                Primitive::deserializeFromString
            ))
            .build();
    }
}
