package edu.umd.mith.validator

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra._
import org.scalatra.fileupload._
import org.scalatra.scalate.ScalateSupport
import org.scalatra.json._

import java.io.{ File, InputStreamReader, Reader, StringReader }
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import org.xml.sax.SAXException

class Validator extends ValidatorStack {
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/") {
    <html>
      <body>
        <h1>Validator</h1>
        <p>A simple Scalatra XML validation service.</p>
      </body>
    </html>
  }

  def schemaSource = params.get("schema").map(new StreamSource(_)).getOrElse(
    halt(404, "Not Found")
  )

  def documentParam = params.get("document").map(new StringReader(_))
  def documentFileParam = fileParams.get("document").map(
    document => new InputStreamReader(document.getInputStream)
  )

  def documentSource(reader: Option[Reader]) =
    reader.map(new StreamSource(_)).getOrElse(
      halt(400, "Bad Request")
    )

  def createValidator(schema: StreamSource) =
    try SchemaFactory
      .newInstance(XMLConstants.RELAXNG_NS_URI)
      .newSchema(schema)
      .newValidator
    catch {
      case _: SAXException => halt(400, "Bad Request")
    }

  get("/validate") {
    contentType = formats("json")

    val document = documentSource(documentParam)
    val validator = createValidator(schemaSource)

    val handler = new ValidationErrorHandler
    validator.setErrorHandler(handler)

    try {
      validator.validate(document)
      handler.getErrors
    } catch {
      case _: SAXException => handler.getErrors
    }
  }

  post("/validate") {
    contentType = formats("json")

    val document = documentSource(documentParam orElse documentFileParam)
    val validator = createValidator(schemaSource)

    val handler = new ValidationErrorHandler
    validator.setErrorHandler(handler)

    try {
      validator.validate(document)
      handler.getErrors
    } catch {
      case _: SAXException => handler.getErrors
    }
  }
}

