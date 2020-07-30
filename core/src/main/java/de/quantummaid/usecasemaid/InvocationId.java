package de.quantummaid.usecasemaid;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvocationId {
    private final String value;

    public static InvocationId randomInvocationId() {
        return invocationId(UUID.randomUUID().toString());
    }

    public static InvocationId invocationId(final String value) {
        validateNotNull(value, "value");
        return new InvocationId(value);
    }

    public String value() {
        return value;
    }
}
