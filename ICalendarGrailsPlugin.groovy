import ch.silviowangler.groovy.util.builder.ICalendarBuilder
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass

/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Silvio Wangler (silvio.wangler@gmail.com)
 */
class ICalendarGrailsPlugin {
    // the plugin version
    def version = '0.3.1'
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '1.1 > *'
    // the other plugins this plugin depends on
    def dependsOn = [controllers: '1.1 > *']
    def loadAfter = ['controllers']
    def observe = ['controllers']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            'grails-app/views/error.gsp',
            'grails-app/controllers/TestController.groovy'
    ]

    def author = "Silvio Wangler"
    def authorEmail = "silvio.wangler@gmail.com"
    def title = "This plugin contains a builder to easily convert your event into the iCalendar format"
    def description = '''
	'''

    // URL to the plugin's documentation
    def documentation = 'http://grails.org/ICalendar+Plugin'

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
        application.controllerClasses.each() {controllerClass ->
            replaceRenderMethod(controllerClass)
        }
    }

    def onChange = {event ->

        // only process controller classes
        if (application.isArtefactOfType(DefaultGrailsControllerClass.CONTROLLER, event.source)) {
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

        println "Modifying render method on controller '${controllerClass.name}'"

        def oldRender = controllerClass.metaClass.pickMethod("render", [Map, Closure] as Class[])

        controllerClass.metaClass.render = {Map params, Closure closure ->

            if (params.contentType?.toLowerCase() == 'text/calendar') {

                response.contentType = 'text/calendar'
                response.characterEncoding = 'UTF-8'
                response.setHeader('Cache-Control', 'no-store, no-cache, must-revalidate') //HTTP/1.1
                response.setHeader('Pragma', 'no-cache') // HTTP/1.0

                def builder = new ICalendarBuilder()
                builder.invokeMethod('translate', closure)

                render builder.toString()

            } else {
                // Defer to original render method
                oldRender.invoke(delegate, [params, closure] as Object[])
            }
        }
    }
}
