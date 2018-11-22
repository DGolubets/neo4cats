package com.commodityvectors.neo4cats

import scala.collection.JavaConverters._

import cats.effect.IO
import fs2.Stream
import org.neo4j.driver.v1.types.TypeSystem
import org.neo4j.driver.v1.{Record, StatementResultCursor, Value}

import com.commodityvectors.neo4cats.cypher._
import com.commodityvectors.neo4cats.util.JavaConversions._

class Cursor(private val cursor: StatementResultCursor,
             typeSystem: TypeSystem) {

  private def convertValue(value: Value): CyValue = {
    if (value.`type`() == typeSystem.BOOLEAN()) {
      CyBoolean(value.asBoolean)
    } else if (value.`type`() == typeSystem.STRING()) {
      CyString(value.asString)
    } else if (value.`type`() == typeSystem.INTEGER()) {
      CyInt(value.asInt)
    } else if (value.`type`() == typeSystem.NODE()) {
      CyNode(value.asNode())
    } else if (value.`type`() == typeSystem.RELATIONSHIP()) {
      CyRelationship(value.asRelationship())
    } else if (value.`type`() == typeSystem.PATH()) {
      CyPath(value.asPath())
    } else {
      CyNull
    }
  }

  private def convertRecord(record: Record): CyRecord = {
    CyRecord(
      record
        .fields()
        .asScala
        .map(p => p.key -> p.value)
        .toMap
        .mapValues(convertValue))
  }

  def next: IO[Option[CyRecord]] = {
    IO.async { cb =>
      cursor.nextAsync().handle[Unit] { (value, error) =>
        if (error != null) cb(Left(error))
        else if (value != null) cb(Right(Some(convertRecord(value))))
        else cb(Right(None))
      }
    }
  }

  def list: IO[List[CyRecord]] = {
    IO.fromCompletionStage {
        cursor.listAsync()
      }
      .map(_.asScala.toList.map(convertRecord))
  }

  def stream: Stream[IO, CyRecord] = {
    Stream
      .eval(next)
      .repeat
      .takeWhile(_.isDefined)
      .flatMap(r => Stream.emits(r.toList))
  }
}
