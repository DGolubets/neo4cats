package com.commodityvectors.neo4cats.util

import java.util.concurrent.CompletionStage

import cats.effect.IO

object JavaConversions {

  implicit class CompletionStageOps[T](val stage: CompletionStage[T])
      extends AnyVal {
    def toIO: IO[T] = {
      IO.async { cb =>
        stage.handle[Unit] { (value, error) =>
          if (error != null) cb(Left(error))
          else cb(Right(value))
        }
      }
    }
  }
}
