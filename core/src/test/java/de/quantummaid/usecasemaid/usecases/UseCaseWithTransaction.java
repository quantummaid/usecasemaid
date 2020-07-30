package de.quantummaid.usecasemaid.usecases;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseWithTransaction {
    private final Transaction transaction;

    public static UseCaseWithTransaction useCaseWithTransaction(final Transaction transaction) {
        return new UseCaseWithTransaction(transaction);
    }

    public void execute() {
        transaction.add("foo", "bar");
        transaction.commit();
    }
}
