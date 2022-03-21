package de.quantummaid.usecasemaid.specialusecases.usecases.symmetric;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Response {
    public final Primitive p;

    public static Response inResponseTo(Request request) {
        return new Response(request.p);
    }
}
