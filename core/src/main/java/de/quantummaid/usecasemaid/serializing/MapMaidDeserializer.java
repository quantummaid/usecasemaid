package de.quantummaid.usecasemaid.serializing;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapMaidDeserializer {
    private final TypeIdentifier typeIdentifier;

    public static MapMaidDeserializer mapMaidDeserializer(final TypeIdentifier typeIdentifier) {
        return new MapMaidDeserializer(typeIdentifier);
    }

    public Map<String, Object> deserializeParameters(final Map<String, Object> input,
                                                     final MapMaid mapMaid) {
        return mapMaid.deserializer().deserializeFromUniversalObject(
                input,
                this.typeIdentifier,
                injector -> {}
        );
    }
}

