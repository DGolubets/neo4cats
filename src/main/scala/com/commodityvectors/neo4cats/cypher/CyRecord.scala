package com.commodityvectors.neo4cats.cypher

case class CyRecord(entries: Map[String, CyValue]) {
  def keys: Iterable[String] = entries.keys
  def values: Iterable[CyValue] = entries.values
}
