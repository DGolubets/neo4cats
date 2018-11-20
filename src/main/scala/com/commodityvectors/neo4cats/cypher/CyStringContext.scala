package com.commodityvectors.neo4cats.cypher

/**
  * Cypher query interpolator.
  *
  * @param sc string context
  */
class CyStringContext(val sc: StringContext) extends AnyVal {

  def cy(args: CyParameter*): CyStatement = {
    val query = sc.parts.tail.zipWithIndex.foldLeft(sc.parts.head) {
      case (acc, (next, i)) => acc + s"$$$i" + next
    }
    val params = args.zipWithIndex.map {
      case (p, i) => s"$i" -> p
    }.toMap

    CyStatement(query.mkString, params)
  }
}
