package com.commodityvectors.neo4cats.cypher

import org.neo4j.driver.v1.types.Node

trait CyDecoder[T] {
  def read(value: CyValue): Either[Exception, T]
}

object CyDecoder {

  case object InvalidTypeException extends Exception

  implicit object IntDecoder extends CyDecoder[Int] {
    override def read(value: CyValue): Either[Exception, Int] = value match {
      case CyInt(v) => Right(v)
      case _        => Left(InvalidTypeException)
    }
  }

  implicit object StringDecoder extends CyDecoder[String] {
    override def read(value: CyValue): Either[Exception, String] = value match {
      case CyString(v) => Right(v)
      case _           => Left(InvalidTypeException)
    }
  }

  implicit object NodeDecoder extends CyDecoder[Node] {
    override def read(value: CyValue): Either[Exception, Node] = value match {
      case CyNode(v) => Right(v)
      case _         => Left(InvalidTypeException)
    }
  }
}
