log4j = {

    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    debug 'ch.silviowangler.groovy.util.builder.ICalendarBuilder', 'groovy.util', 'ICalendarGrailsPlugin'
}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"

grails.doc.title = 'Grails iCalendar Plug-in'
grails.doc.subtitle = 'The Grails way of exporting appointments to your users.'
grails.doc.authors = 'Silvio Wangler'
grails.doc.license = 'Apache 2'
grails.doc.copyright = "Copyright 2009 - ${new Date().format('yyyy')} Silvio Wangler"

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
