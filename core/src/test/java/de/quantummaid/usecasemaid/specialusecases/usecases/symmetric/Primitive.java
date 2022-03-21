package de.quantummaid.usecasemaid.specialusecases.usecases.symmetric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Primitive {
    private final String mappingValue;

    public static Primitive deserializeFromString(String unsafe) {
        return new Primitive(unsafe);
    }

    static String serializeToString(Primitive safe) {
        return safe.mappingValue;
    }
}
