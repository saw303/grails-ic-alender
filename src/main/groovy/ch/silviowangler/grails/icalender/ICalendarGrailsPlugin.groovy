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
            'grails-app/controllers/TestController.groovy'
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

}
