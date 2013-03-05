package ch.silviowangler.groovy.util.builder

import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.util.UidGenerator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.property.*

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
 * iCalendar builder
 *
 * Attention: This class requires Groovy 1.5.7 or greater.
 *
 * @author Silvio Wangler
 * @since 0.1
 */
public class ICalendarBuilder extends BuilderSupport {

    private static final String CLOSURE_NAME_EVENT = 'event'
    private static final String CLOSURE_NAME_ORGANIZER = 'organizer'
    private static final String CLOSURE_NAME_REMINDER = 'reminder'
    private static final String CLOSURE_NAME_EVENTS = 'events'
    private Log log = LogFactory.getLog(ICalendarBuilder.class)
    private Calendar cal
    private VEvent currentEvent

    /**
     * Declares a parent/child relations
     */
    public static final Map PARENT_CHILD_CONSTRAINTS = ['calendar': [CLOSURE_NAME_EVENTS], 'events': [CLOSURE_NAME_EVENT], 'event': [CLOSURE_NAME_ORGANIZER, CLOSURE_NAME_REMINDER]]

    /**
     * Default constructor
     * @since 0.1
     */
    public ICalendarBuilder() {
        super();
        reset()
    }

    /**
     * Entry point for external (not inline) closures such as
     *
     * <pre>
     * def c = {*   calender {*         events {*                 ....
     *}*}*}* builder.invokeMethod('translate', c)
     * </pre>
     * @since 0.2
     */
    public void translate(Closure c) {
        log.debug("Translating ${c.toString()}")
        c.call()
    }

    @Override
    protected void setParent(Object parent, Object child) {

        log.debug "set parent '$parent' on child '$child'"
        if (parent == 'translate') return // skip it because its the special entry if the builder is not working in inline mode

        if (!PARENT_CHILD_CONSTRAINTS[parent]?.contains(child)) {
            if (!PARENT_CHILD_CONSTRAINTS[parent]) throw new IllegalArgumentException("Unkown element $parent")
            throw new IllegalArgumentException("Element '$child' not possible for parent element '$parent'. Parent element allows the following child elements ${PARENT_CHILD_CONSTRAINTS[parent].toListString()}")
        }
    }

    @Override
    protected Object createNode(Object o) {
        return createNode(o, [:])
    }

    @Override
    protected Object createNode(Object o, Object o1) {
        throw new RuntimeException('Unsupported mode')
    }

    @Override
    protected Object createNode(Object nodeName, Map params) {
        if (nodeName == 'calendar') handleCalendarNode(params, nodeName)
        if (nodeName == CLOSURE_NAME_EVENT) handleEventNode(params, nodeName)


        if (nodeName == CLOSURE_NAME_ORGANIZER) {

            Organizer _organizer = currentEvent.getProperty(Property.ORGANIZER)


            if (params.email) {
                _organizer.value = "mailto:${params.email}"
            }
            /*
            // If you uncomment this it will brake all test. No idea what is going wrong
            if (params.name) {
                _organizer.parameters << new Cn(params.name)
            }*/
        }

        if (nodeName == CLOSURE_NAME_REMINDER && params.minutesBefore && params.description) {
            VAlarm alarm = new VAlarm(new Dur(0, 0, params.minutesBefore as Integer, 0))

            alarm.properties << new Description(params.description)
            alarm.properties << Action.DISPLAY
            currentEvent.alarms << alarm
        }


        log.debug "createNode $nodeName, $params"
        return nodeName
    }

    private void handleCalendarNode(Map params, nodeName) {
        this.cal = new Calendar()
        this.cal.properties << new ProdId(params.prodid ?: '-//Grails iCalendar plugin//NONSGML Grails iCalendar plugin//EN')
        this.cal.properties << Version.VERSION_2_0
        this.cal.properties << CalScale.GREGORIAN
        this.cal.properties << Method.PUBLISH
    }

    private void handleEventNode(Map params, nodeName) {

        TimeZoneRegistry registry = TimeZoneRegistryFactory.instance.createRegistry()
        TimeZone timezone = params.timezone ? registry.getTimeZone(params.timezone) : registry.getTimeZone('Europe/Zurich')
        if (!timezone) {
            log.debug("Time zone ${params.timezone} is not known by iCal4j")
            throw new IllegalArgumentException("Unknown time zone ${params.timezone}")
        }
        VTimeZone tz = timezone.vTimeZone

        currentEvent = new VEvent(new DateTime(params.start), new DateTime(params.end), params.summary)
        currentEvent.startDate.timeZone = timezone
        currentEvent.endDate.timeZone = timezone

        /*
       set internet address to null otherwise it takes awful lots of time to resolve a hostname or ip address
        */
        currentEvent.properties << new UidGenerator(null, 'iCalPlugin-Grails').generateUid()
        currentEvent.properties << tz.timeZoneId
        if (params.location) currentEvent.properties << new Location(params.location)
        if (params.description) currentEvent.properties << new Description(params.description)
        if (params.classification) currentEvent.properties << getClazz(params.classification)
        currentEvent.properties << new Organizer()
        this.cal.components << currentEvent
    }

    protected Object createNode(Object nodeName, Map params, Object o1) {
        throw new RuntimeException('Unsupported mode')
    }

    /**
     * Returns the calendar as iCalendar format
     * @since 0.1
     */
    public String toString() {
        this.cal?.toString()
    }

    /**
     * Clears the internal iCalendar buffer
     * @since 0.2
     */
    public void reset() {
        this.cal = null
        this.currentEvent = null
    }

    private Clazz getClazz(String value) {
        if (value.toLowerCase() == 'public') return Clazz.PUBLIC
        if (value.toLowerCase() == 'private') return Clazz.PRIVATE
        if (value.toLowerCase() == 'confidential') return Clazz.CONFIDENTIAL
        return new Clazz(value)
    }

    /**
     * Returns the ical4j calendar instance
     * @since 0.2
     */
    public Calendar getCal() {
        this.cal
    }
}
