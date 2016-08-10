package ch.silviowangler.grails.icalender

import grails.web.api.WebAttributes
import groovy.transform.CompileStatic

/**
 * @author Silvio Wangler
 */
@CompileStatic
trait CalendarExporter implements WebAttributes {


    void renderCalendar(Closure closure) {
        render(null, closure)
    }

    void renderCalendar(String filename, Closure closure) {
        render(filename, closure)
    }

    private render(String filename, Closure closure) {
        def response = webRequest.currentResponse

        def builder = new ICalendarBuilder()
        builder.invokeMethod('translate', closure)

        if (filename) {
            response.setHeader 'Content-Disposition', "inline; filename=\"${filename}\""
        }

        response.contentType = 'text/calendar'
        response.characterEncoding = params.characterEncoding ?: 'UTF-8'
        response.outputStream << builder.toString()
        response.outputStream.flush()
    }

}
