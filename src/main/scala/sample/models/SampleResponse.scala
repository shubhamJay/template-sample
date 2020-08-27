package sample.models

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs.deriveCodec

case class SampleResponse(msg: String)

object SampleResponse {
  implicit val sampleResponseCodec: Codec[SampleResponse] = deriveCodec
}
