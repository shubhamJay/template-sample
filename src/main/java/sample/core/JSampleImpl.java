package sample.core;

import esw.http.template.wiring.JCswContext;
import sample.core.models.SampleResponse;

import java.util.concurrent.CompletableFuture;

public class JSampleImpl {
    JCswContext jCswContext;

    public JSampleImpl(JCswContext jCswContext) {
        this.jCswContext = jCswContext;
    }

    public CompletableFuture<SampleResponse> sayBye() {
        return CompletableFuture.completedFuture(new SampleResponse("Bye!!!"));
    }


}
