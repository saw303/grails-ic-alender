package ch.silviowangler.grails.icalender

import grails.plugins.Plugin
import org.grails.core.artefact.ControllerArtefactHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ICalendarGrailsPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(getClass())

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            'grails-app/views/error.gsp',
            'grails-app/controllers/TestController.groovy',
            'grails-app/controllers/ExcludedTestController.groovy'
    ]

    def author = "Silvio Wangler"
    def authorEmail = "silvio.wangler@gmail.com"
    def title = "iCalendar Plug-in"
    def description = '''
        This plugin contains a builder to easily convert your event into the iCalendar format.
        The plugin hooks replaces each render method that uses the contentType 'text/calendar'.
	'''


    def dependsOn = [controllers: '3.0.0 > *']
    def loadAfter = ['controllers']
    def observe = ['controllers']

    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = 'http://github.com/saw303/grails-ic-alender'

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    def organization = [name: "Silvio Wangler Software Development", url: "http://www.silviowangler.ch/"]

    // Any additional developers beyond the author specified above.
    def developers = [[name: "Silvio Wangler", email: "silvio.wangler@gmail.com"]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: "Github", url: "https://github.com/saw303/grails-ic-alender/issues"]


    void doWithDynamicMethods() {
        // hooking into render method
        for (controllerClass in grailsApplication.controllerClasses) {
            replaceRenderMethod(controllerClass, grailsApplication)
        }
    }

    void onChange(Map<String, Object> event) {
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.

        // only process controller classes
        if (grailsApplication.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
            replaceRenderMethod(grailsApplication.getControllerClass(event.source?.name), grailsApplication)
        }
    }

    private void replaceRenderMethod(controllerClass, application) {
        if(controllerClass.logicalPropertyName in getExcludedControllerNames(application)){
            return
        }

        log.info("Modifying render method on controller '${controllerClass.name}'")

        def oldRender = controllerClass.metaClass.pickMethod("render", [Map, Closure] as Class[])

        controllerClass.metaClass.render = { Map params, Closure closure ->

            final String MIME_TYPE_TEXT_CALENDAR = 'text/calendar'

            if (MIME_TYPE_TEXT_CALENDAR.equalsIgnoreCase(params.contentType)) {

                def builder = new ICalendarBuilder()
                builder.invokeMethod('translate', closure)

                if (params.filename) {
                    response.setHeader 'Content-Disposition', "inline; filename=\"${params.filename}\""
                }

                response.contentType = MIME_TYPE_TEXT_CALENDAR
                response.characterEncoding = params.characterEncoding ?: 'UTF-8'
                response.outputStream << builder.toString()
                response.outputStream.flush()

            } else {
                // Defer to original render method
                oldRender.invoke(delegate, [params, closure] as Object[])
            }
        }
    }

    private static getExcludedControllerNames(def application) {
        application.config.grails.plugins.ical.controllers.exclude
    }
}
