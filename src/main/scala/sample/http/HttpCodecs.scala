package sample.http

import io.bullet.borer.Codec
import io.bullet.borer.compat.AkkaHttpCompat
import io.bullet.borer.derivation.MapBasedCodecs.deriveCodec
import sample.core.models.SampleResponse

object HttpCodecs extends HttpCodecs

trait HttpCodecs extends AkkaHttpCompat {
  implicit lazy val sampleResponseCodec: Codec[SampleResponse] = deriveCodec
}
