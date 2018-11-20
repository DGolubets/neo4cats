package com.commodityvectors

import scala.language.implicitConversions

import com.commodityvectors.neo4cats.cypher.CyStringContext

package object neo4cats {
  implicit def cypherStringContext(sc: StringContext): CyStringContext =
    new CyStringContext(sc)
}
