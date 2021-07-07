package com.gopewpew

import cats.effect.IO
import fs2.io.file.Files
import fs2.text

import java.nio.file.Path

object ReadFile extends (Path => fs2.Stream[IO, String]) {
  private val CHUNK_SIZE: Int = 4096

  def apply(path: Path): fs2.Stream[IO, String] =
    Files[IO].readAll(path, CHUNK_SIZE)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(_.nonEmpty)
}
