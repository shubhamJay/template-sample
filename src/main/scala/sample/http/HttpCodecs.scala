package sample.http

import csw.location.api.codec.LocationCodecs
import io.bullet.borer.Codec
import io.bullet.borer.compat.AkkaHttpCompat
import io.bullet.borer.derivation.MapBasedCodecs.deriveCodec
import sample.core.models.{Person, SampleResponse}

object HttpCodecs extends HttpCodecs

trait HttpCodecs extends AkkaHttpCompat with LocationCodecs{
  implicit lazy val sampleResponseCodec: Codec[SampleResponse] = deriveCodec
  implicit lazy val personCodec: Codec[Person] = deriveCodec
}
