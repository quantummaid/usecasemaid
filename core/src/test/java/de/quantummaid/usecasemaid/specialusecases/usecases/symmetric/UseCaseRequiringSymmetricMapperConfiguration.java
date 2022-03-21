package de.quantummaid.usecasemaid.specialusecases.usecases.symmetric;

public class UseCaseRequiringSymmetricMapperConfiguration {
    public Response execute(Request request) {
        return Response.inResponseTo(request);
    }
}