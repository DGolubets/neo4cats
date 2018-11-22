package com.commodityvectors.neo4cats.cypher

import org.neo4j.driver.v1.{Value, Values}
import org.neo4j.driver.v1.types.{Node, Path, Relationship}

/**
  * Cypher value.
  */
sealed trait CyValue {
  final def as[T](implicit decoder: CyDecoder[T]): Either[Exception, T] =
    decoder.read(this)

  private[neo4cats] def rawValue: Value
}

object CyValue {
  def apply(value: Value): CyValue = {
    value.asObject match {
      case null                 => CyNull
      case v: String            => CyString(v)
      case v: java.lang.Integer => CyInt(v)
      case v: java.lang.Double  => CyFloat(v)
      case v: Node              => CyNode(v)
      case v: Relationship      => CyRelationship(v)
      case v: Path              => CyPath(v)
    }
  }
}

/**
  * Cypher parameter.
  */
sealed trait CyParameter extends CyValue

object CyParameter {

  implicit def apply(value: String): CyParameter = CyString(value)

  implicit def apply(value: Int): CyParameter = CyInt(value)

  implicit def apply(value: Double): CyParameter = CyFloat(value)

  implicit def apply(value: Boolean): CyParameter = CyBoolean(value)

  implicit def apply[T](value: Option[T])(
      implicit encoder: T => CyParameter): CyParameter = {
    value.map(encoder).getOrElse(CyNull)
  }
}

/**
  * Property types
  *
  * ✓ Can be returned from Cypher queries
  * ✓ Can be used as parameters
  * ✓ Can be stored as properties
  * ✓ Can be constructed with Cypher literals
  */
sealed trait CyProperty extends CyParameter

/**
  * Structural types
  *
  * ✓ Can be returned from Cypher queries
  * ❏ Cannot be used as parameters
  * ❏ Cannot be stored as properties
  * ❏ Cannot be constructed with Cypher literals
  */
sealed trait CyStructural extends CyValue

/**
  * Composite types
  *
  * ✓ Can be returned from Cypher queries
  * ✓ Can be used as parameters
  * ❏ Cannot be stored as properties
  * ✓ Can be constructed with Cypher literals
  */
sealed trait CyComposite extends CyParameter

case object CyNull extends CyProperty {
  override def rawValue: Value = Values.NULL
}

case class CyString(value: String) extends CyProperty {
  override def rawValue: Value = Values.value(value)
}

case class CyFloat(value: Double) extends CyProperty {
  override def rawValue: Value = Values.value(value)
}

case class CyInt(value: Int) extends CyProperty {
  override def rawValue: Value = Values.value(value)
}

case class CyBoolean(value: Boolean) extends CyProperty {
  override def rawValue: Value = Values.value(value)
}

case class CyList(value: Seq[CyValue]) extends CyComposite {
  override def rawValue: Value = Values.value(value)
}

case class CyMap(value: Map[String, CyValue]) extends CyComposite {
  override def rawValue: Value = Values.value(value)
}

case class CyNode(value: Node) extends CyStructural {
  override def rawValue: Value = Values.value(value)
}

case class CyRelationship(value: Relationship) extends CyStructural {
  override def rawValue: Value = Values.value(value)
}

case class CyPath(value: Path) extends CyStructural {
  override def rawValue: Value = Values.value(value)
}
