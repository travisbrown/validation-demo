package edu.umd.mith.validator

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra._
import org.scalatra.fileupload._
import org.scalatra.scalate.ScalateSupport
import org.scalatra.json._

import java.io.File
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

  post("/validate") {
    contentType = formats("json")

    val schemaUrl = params("schema")
    println(schemaUrl)

    val rngValidator = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI)
      .newSchema(new StreamSource(schemaUrl))
      .newValidator

    val handler = new ValidationErrorHandler
    rngValidator.setErrorHandler(handler)

    val errors = try {
      rngValidator.validate(
        new StreamSource(fileParams("document").getInputStream)
      )
      handler.getErrors
    } catch {
      case _: SAXException => handler.getErrors
    }
    
    errors
  }
}
