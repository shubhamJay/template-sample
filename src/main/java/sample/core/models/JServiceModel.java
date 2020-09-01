package sample.core.models;

import csw.location.api.models.Location;
import sample.http.SampleResponse;

import java.util.List;

public class JServiceModel {
    List<Location> locs;
    SampleResponse sampleResponse;

    public JServiceModel(List<Location> locs, SampleResponse sampleResponse) {
        this.locs = locs;
        this.sampleResponse = sampleResponse;
    }

    public List<Location> getLocs() {
        return locs;
    }

    public SampleResponse getSampleResponse() {
        return sampleResponse;
    }
}
