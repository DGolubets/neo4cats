package com.commodityvectors.neo4cats

import java.io.Closeable

import cats.effect.IO
import org.neo4j.driver.v1
import org.neo4j.driver.v1.{AuthToken, GraphDatabase}

import com.commodityvectors.neo4cats.util.JavaConversions._

class Driver(uri: String, authToken: AuthToken) extends Closeable {

  private val driver: v1.Driver = GraphDatabase.driver(uri, authToken)

  def session(): Session = {
    new Session(driver.session())
  }

  def close(): Unit = {
    driver.close()
  }

  def shutdown(): IO[Unit] = {
    driver.closeAsync().toIO.map(_ => ())
  }
}

object Driver {
  def apply(uri: String, authToken: AuthToken): Driver = {
    new Driver(uri, authToken)
  }
}
