package com.commodityvectors.neo4cats.cypher

case class CyStatement(query: String, parameters: Map[String, CyParameter])
