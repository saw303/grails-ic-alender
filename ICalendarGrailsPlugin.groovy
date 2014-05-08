import ch.silviowangler.groovy.util.builder.ICalendarBuilder

import static org.codehaus.groovy.grails.commons.ControllerArtefactHandler.TYPE

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
    def version = "0.4.0" // added by set-version

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '2.0.0 > *'
    // the other plugins this plugin depends on
    def dependsOn = [controllers: '2.0.0 > *']
    def loadAfter = ['controllers']
    def observe = ['controllers']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            'grails-app/views/error.gsp',
            'grails-app/controllers/TestController.groovy'
    ]

    def author = "Silvio Wangler"
    def authorEmail = "silvio.wangler@gmail.com"
    def title = "iCalendar Plug-in"
    def description = '''
        This plugin contains a builder to easily convert your event into the iCalendar format.
        The plugin hooks replaces each render method that uses the contentType 'text/calendar'.
	'''

    // URL to the plugin's documentation
    def documentation = 'http://github.com/saw303/grails-ic-alender'

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [name: "Silvio Wangler Software Development", url: "http://www.silviowangler.ch/"]

    // Any additional developers beyond the author specified above.
    def developers = [[name: "Silvio Wangler", email: "silvio.wangler@gmail.com"]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: "Github", url: "https://github.com/saw303/grails-ic-alender/issues"]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/saw303/grails-ic-alender"]

    def doWithSpring = {
        // do nothing yet
    }

    def doWithApplicationContext = { applicationContext ->
        // Implement post initialization spring config (optional)
    }

    def doWithWebDescriptor = { xml ->
        // Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->

        // hooking into render method
        application.controllerClasses.each() { controllerClass ->
            replaceRenderMethod(controllerClass)
        }
    }

    def onChange = { event ->

        // only process controller classes
        if (application.isArtefactOfType(TYPE, event.source)) {
            def clazz = application.getControllerClass(event.source?.name)
            replaceRenderMethod(clazz)
        }
    }

    def onConfigChange = { event ->
        // The event is the same as for 'onChange'.
    }

    /**
     * This implementation is based on Marc Palmers feeds plugin. It hooks into the render method
     * of a Grails controller class and adds an alternative behaviour for the mime type
     * 'text/calendar' used by the iCalendar plugin.
     */
    private void replaceRenderMethod(controllerClass) {

        println "Modifying render method on controller '${controllerClass.name}'"

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
}
