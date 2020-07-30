package de.quantummaid.usecasemaid.usecases;

public final class UseCaseWithParameters {
    public static MyDto LAST_PARAMETER = null;

    public void execute(final MyDto myDto) {
        LAST_PARAMETER = myDto;
    }
}
