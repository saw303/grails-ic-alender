import ch.silviowangler.groovy.util.builder.ICalendarBuilder

/**
 * @author Silvio Wangler (silvio.wangler@gmail.com)
 */
class ICalendarGrailsPlugin {
  // the plugin version
  def version = "0.2"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.1.1 > *"
  // the other plugins this plugin depends on
  def dependsOn = [:]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp",
          "grails-app/controllers/TestController.groovy"
  ]

  def author = "Silvio Wangler"
  def authorEmail = "silvio.wangler@gmail.com"
  def title = "This plugin contains a builder to easily convert your event into the iCalendar format"
  def description = '''
	'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/ICalendar+Plugin"

  def doWithSpring = {
    // do nothing yet
  }

  def doWithApplicationContext = {applicationContext ->
    // TODO Implement post initialization spring config (optional)
  }

  def doWithWebDescriptor = {xml ->
    // TODO Implement additions to web.xml (optional)
  }

  def doWithDynamicMethods = {ctx ->

    // hooking into render method
    for (Object controllerClass: application.controllerClasses) {
      println "Modifying render method on controller ${controllerClass.class.name}"
      replaceRenderMethod(controllerClass)
    }
  }

  def onChange = {event ->
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
    if (application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
      def clazz = application.getControllerClass(event.source?.name)
      replaceRenderMethod(clazz)
    }
  }

  def onConfigChange = {event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }

  /**
   * This implementation is based on Marc Palmers feed plugin. It hooks into the render method
   * of a Grails controller class and adds an alternative behaviour for the mime type
   * 'text/calendar' used by the iCalendar plugin.
   */
  private void replaceRenderMethod(controllerClass) {
    def oldRender = controllerClass.metaClass.pickMethod("render", [Map, Closure] as Class[])

    controllerClass.metaClass.render = {Map params, Closure closure ->

      if (params.contentType?.toLowerCase() == 'text/calendar') {

        println '--------> my mode'

        def builder = new ICalendarBuilder()
        builder.build(closure)
        response.contentType = 'text/calendar'
        response.characterEncoding = "UTF-8"
        builder.toString()

      } else {
        // Defer to original render method
        println '---->><<<  original mode'
        oldRender.invoke(delegate, [params, closure] as Object[])
      }
    }
  }
}
