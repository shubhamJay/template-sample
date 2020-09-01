package sample.core;

import sample.CswWiring;
import sample.core.models.JServiceModel;
import sample.http.SampleResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JSampleImpl {
    private final CswWiring cswWiring;

    public JSampleImpl(CswWiring cswWiring) {
        this.cswWiring = cswWiring;
    }

    public SampleResponse sayHello() {
        return new SampleResponse("Hello!!!");
    }

    public CompletableFuture<SampleResponse> securedSayHello() {
        return CompletableFuture.completedFuture(new SampleResponse("Secured Hello!!!"));
    }

    public CompletableFuture<JServiceModel> getLocations() {
        return CompletableFuture.completedFuture(new JServiceModel(List.of(), new SampleResponse("foo")));
    }
}
