
log4j = {

    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    debug 'ch.silviowangler.groovy.util.builder.ICalendarBuilder', 'groovy.util'
}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
