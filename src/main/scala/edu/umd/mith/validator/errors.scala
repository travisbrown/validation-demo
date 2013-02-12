package edu.umd.mith.validator

import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import scala.collection.mutable.Buffer

sealed trait XmlError {
  def uri: String
  def message: String
  def location: String
}

trait SaxError extends XmlError {
  def line: Int
  def column: Int
  def location = "line %d; column %d".format(this.line, this.column)
}

case class FatalError(
  uri: String, message: String, line: Int, column: Int
) extends SaxError

case class NonFatalError(
  uri: String, message: String, line: Int, column: Int
) extends SaxError

case class Warning(
  uri: String, message: String, line: Int, column: Int
) extends SaxError

class ValidationErrorHandler extends ErrorHandler {
  private val errors = Buffer.empty[XmlError]

  def getErrors: Seq[XmlError] = this.errors

  def error(e: SAXParseException) {
    this.errors += NonFatalError(
      e.getSystemId, e.getMessage, e.getLineNumber, e.getColumnNumber
    )
  }

  def fatalError(e: SAXParseException) { println("FATAL!")
    this.errors += FatalError(
      e.getSystemId, e.getMessage, e.getLineNumber, e.getColumnNumber
    )
  }

  def warning(e: SAXParseException) {
    this.errors += Warning(
      e.getSystemId, e.getMessage, e.getLineNumber, e.getColumnNumber
    )
  }
}

