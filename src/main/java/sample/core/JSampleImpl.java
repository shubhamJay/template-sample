package sample.core;

import sample.core.models.SampleResponse;

import java.util.concurrent.CompletableFuture;

public class JSampleImpl {
    public CompletableFuture<SampleResponse> sayBye() {
        return CompletableFuture.completedFuture(new SampleResponse("Bye!!!"));
    }


}
