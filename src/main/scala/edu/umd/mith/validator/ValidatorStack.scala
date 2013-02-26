package edu.umd.mith.validator

import org.scalatra._
import org.scalatra.fileupload._
import org.scalatra.json._
import org.scalatra.scalate.ScalateSupport
import org.fusesource.scalate.{ TemplateEngine, Binding }
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import javax.servlet.http.HttpServletRequest
import javax.xml.XMLConstants
import javax.xml.validation.SchemaFactory
import scala.collection.mutable

trait ValidatorStack extends ScalatraServlet
  with ScalateSupport with FileUploadSupport with JacksonJsonSupport with CorsSupport {

  // First we have to register the Jing validator.
  System.setProperty(
    classOf[SchemaFactory].getName() + ":" + XMLConstants.RELAXNG_NS_URI,
    "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory"
  )

  // Respond to the preflight request for CORS.
  options("/*"){
    response.setHeader(
      "Access-Control-Allow-Headers",
      request.getHeader("Access-Control-Request-Headers")
    )
  }

  override def jsonpCallbackParameterNames: Iterable[String] = Some("callback")

  /* wire up the precompiled templates */
  override protected def defaultTemplatePath: List[String] = List("/templates/views")
  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      TemplateEngine.templateTypes.map("/templates/layouts/default." + _): _*)
    engine.packagePrefix = "templates"
    engine
  }
  /* end wiring up the precompiled templates */
  
  override protected def templateAttributes(implicit request: HttpServletRequest): mutable.Map[String, Any] = {
    super.templateAttributes ++ mutable.Map.empty // Add extra attributes here, they need bindings in the build file
  }
  

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
