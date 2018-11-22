package com.commodityvectors.neo4cats

import java.io.Closeable

import scala.collection.JavaConverters._

import cats.effect.IO
import fs2.Stream
import org.neo4j.driver.v1
import org.neo4j.driver.v1._

import com.commodityvectors.neo4cats.cypher._
import com.commodityvectors.neo4cats.util.JavaConversions._

class Session(private val session: v1.Session) extends Closeable {

  def run(statement: CyStatement): IO[Cursor] = {
    IO.fromCompletionStage[v1.StatementResultCursor] {
        session
          .readTransactionAsync((tx: Transaction) => {
            tx.runAsync(statement.query,
                        statement.parameters
                          .mapValues(_.rawValue.asInstanceOf[AnyRef])
                          .asJava)
          })
      }
      .map(c => new Cursor(c, session.typeSystem()))
  }

  def list(statement: CyStatement): IO[List[CyRecord]] = {
    run(statement).flatMap(_.list)
  }

  def stream(statement: CyStatement): Stream[IO, CyRecord] = {
    Stream.eval(run(statement)).flatMap(_.stream)
  }

  def close(): Unit = {
    session.close()
  }

  def shutdown(): IO[Unit] = {
    IO.fromCompletionStage {
        session.closeAsync()
      }
      .map(_ => ())
  }
}
