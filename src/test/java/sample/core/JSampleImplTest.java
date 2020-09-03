package sample.core;

import esw.http.template.wiring.JCswContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.scalatestplus.junit.JUnitSuite;
import sample.core.models.SampleResponse;

import java.util.concurrent.ExecutionException;

public class JSampleImplTest extends JUnitSuite {

    @Test
    public void shouldCallBye() throws ExecutionException, InterruptedException {
        JCswContext mock = Mockito.mock(JCswContext.class);
        JSampleImpl jSample = new JSampleImpl(mock);
        SampleResponse sampleResponse = new SampleResponse("Bye!!!");
        Assert.assertThat(jSample.sayBye().get(), CoreMatchers.is(sampleResponse));
    }
}