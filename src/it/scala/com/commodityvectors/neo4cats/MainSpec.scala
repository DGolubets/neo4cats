package com.commodityvectors.neo4cats

import org.neo4j.driver.v1.AuthTokens
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class MainSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  val driver = Driver(
    "bolt://localhost:7687",
    AuthTokens.basic("neo4j", "neo4s")
  )

  override def afterAll(): Unit = {
    driver.close()
  }

  "Driver" should {
    "list results" in {
      driver
        .session()
        .flatMap { session =>
          val name = "Tom Hanks"

          session
            .list(cy"""
              MATCH (tom:Person {name: $name})-[:ACTED_IN]->(tomHanksMovies)
              RETURN tom,tomHanksMovies
            """)
        }
        .unsafeToFuture
        .map(_.length should be > 0)
    }

    "stream results" in {
      driver
        .session()
        .flatMap { session =>
          session
            .stream(cy"""
               MATCH (tom:Person {name: "Tom Hanks"})-[:ACTED_IN]->(tomHanksMovies)
               RETURN tom,tomHanksMovies
            """)
            .compile
            .toList
        }
        .unsafeToFuture
        .map(_.length should be > 0)
    }
  }
}
