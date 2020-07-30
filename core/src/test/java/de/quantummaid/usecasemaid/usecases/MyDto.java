package de.quantummaid.usecasemaid.usecases;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MyDto {
    public final String field1;
    public final String field2;
    public final String field3;

    public static MyDto myDto(final String field1, final String field2, final String field3) {
        return new MyDto(field1, field2, field3);
    }
}
