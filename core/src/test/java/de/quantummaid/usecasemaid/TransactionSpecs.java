package de.quantummaid.usecasemaid;

import de.quantummaid.usecasemaid.usecases.Transaction;
import de.quantummaid.usecasemaid.usecases.UseCaseWithTransaction;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.injectmaid.ReusePolicy.SINGLETON;
import static de.quantummaid.usecasemaid.UseCaseMaid.aUseCaseMaid;
import static de.quantummaid.usecasemaid.usecases.Transaction.transactionOnDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

public final class TransactionSpecs {

    @Test
    public void useCaseCanHaveATransaction() {
        final Map<String, String> database = new LinkedHashMap<>();

        final UseCaseMaid useCaseMaid = aUseCaseMaid()
                .invoking("test", UseCaseWithTransaction.class)
                .withInvocationScopedDependencies(builder ->
                        builder.withCustomType(Transaction.class, () -> transactionOnDatabase(database), SINGLETON))
                .build();
        useCaseMaid.invoke("test", Map.of());

        assertThat(database, hasEntry("foo", "bar"));
        assertThat(database.size(), is(1));
    }
}
