package org.tmt.sample.core;

import esw.http.template.wiring.JCswServices;
import org.tmt.sample.core.models.SampleResponse;

import java.util.concurrent.CompletableFuture;

public class JSampleImpl {
    JCswServices jCswServices;

    public JSampleImpl(JCswServices jCswServices) {
        this.jCswServices = jCswServices;
    }

    public CompletableFuture<SampleResponse> sayBye() {
        return CompletableFuture.completedFuture(new SampleResponse("Bye!!!"));
    }

}
