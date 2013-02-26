package edu.umd.mith.validator

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra._
import org.scalatra.fileupload._
import org.scalatra.scalate.ScalateSupport
import org.scalatra.json._

import java.io.{ File, InputStreamReader, StringReader }
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

  get("/validate") {
    contentType = formats("json")

    val schemaUrl = params("schema")
    val document = new StringReader(params("document"))

    val rngValidator = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI)
      .newSchema(new StreamSource(schemaUrl))
      .newValidator

    val handler = new ValidationErrorHandler
    rngValidator.setErrorHandler(handler)

    val errors = try {
      rngValidator.validate(new StreamSource(document))
      handler.getErrors
    } catch {
      case _: SAXException => handler.getErrors
    }
    
    errors
  }
  post("/validate") {
    contentType = formats("json")

    val schemaUrl = params("schema")

    val source = new StreamSource(
      params

        .get("document")
        .map(doc => new StringReader(doc))
        .getOrElse(new InputStreamReader(fileParams("document").getInputStream))
    )

    val rngValidator = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI)
      .newSchema(new StreamSource(schemaUrl))
      .newValidator

    val handler = new ValidationErrorHandler
    rngValidator.setErrorHandler(handler)

    val errors = try {
      rngValidator.validate(source)
      handler.getErrors
    } catch {
      case _: SAXException => handler.getErrors
    }
    
    errors
  }
}
