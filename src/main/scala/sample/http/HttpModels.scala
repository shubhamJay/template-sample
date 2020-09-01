package sample.http

import csw.location.api.models.Location

case class SampleResponse(msg: String)

case class ServiceModel(locs: List[Location], sampleResponse: SampleResponse)
