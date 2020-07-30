package de.quantummaid.usecasemaid.usecases;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transaction {
    private final Map<String, String> database;
    private final Map<String, String> items;

    public static Transaction transactionOnDatabase(final Map<String, String> database) {
        return new Transaction(database, new LinkedHashMap<>());
    }

    public void add(final String key, final String value) {
        items.put(key, value);
    }

    public void commit() {
        database.putAll(items);
    }
}
