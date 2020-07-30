package de.quantummaid.usecasemaid;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseRoute {
    private final String value;

    public static UseCaseRoute useCaseRoute(final String value) {
        validateNotNull(value, "value");
        return new UseCaseRoute(value);
    }
}
