package sample.core;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.scalatestplus.junit.JUnitSuite;
import sample.CswWiring;
import sample.http.SampleResponse;

public class JSampleImplTest extends JUnitSuite {
    CswWiring mock = Mockito.mock(CswWiring.class);
    JSampleImpl jSampleImpl = new JSampleImpl(mock);

    @Test
    public void sayHelloShouldReturnSampleResponse() {
        SampleResponse sampleResponse = new SampleResponse("Hello!!!");
        Assert.assertThat(jSampleImpl.sayHello(), CoreMatchers.is(sampleResponse));
    }

    @Test
    public void securedSayHelloShouldReturnSampleResponse() {
        SampleResponse sampleResponse = new SampleResponse("Secured Hello!!!");
        Assert.assertThat(jSampleImpl.securedSayHello(), CoreMatchers.is(sampleResponse));
    }
}
