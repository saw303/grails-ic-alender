import ch.silviowangler.groovy.util.builder.ICalendarBuilder
import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import org.junit.After
import org.junit.Before
import org.junit.Test

import static junit.framework.Assert.assertEquals
import static net.fortuna.ical4j.model.Component.VEVENT
import static net.fortuna.ical4j.model.Property.ATTENDEE
import static net.fortuna.ical4j.model.parameter.CuType.INDIVIDUAL
import static net.fortuna.ical4j.model.parameter.PartStat.NEEDS_ACTION
import static net.fortuna.ical4j.model.parameter.Role.REQ_PARTICIPANT
import static net.fortuna.ical4j.model.parameter.Rsvp.FALSE
import static net.fortuna.ical4j.model.parameter.Rsvp.TRUE

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
 * @author Silvio Wangler
 */

class BuilderTests {

    private ICalendarBuilder builder

    @Before
    void setUp() {
        this.builder = new ICalendarBuilder()
    }

    @After
    void tearDown() {
        this.builder = null
    }

    @Test
    void testBuilderWritesCalendar() {
        builder.calender()
        assert builder.cal == null
    }
    
    @Test
    void testWithOutExplicitOrganizerDeclaration() {
        builder.calendar {
            events {
                event(start: new Date(), end: new Date(), description: 'Hi all', summary: 'Short info1')
            }
        }
        builder.cal.validate(true)
        
        assert builder.cal.getComponents(VEVENT)[0].getProperty(Property.ORGANIZER) != null
    }

    @Test
    void testSimpleTwoEvents() {

        final eventDescription1 = 'Events description'
        final eventDescription2 = 'hell yes'

        builder.calendar {
            events {
                event(start: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 14:00'), end: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 15:00'), description: eventDescription1, summary: 'Short info1') {
                    organizer(name: 'Silvio Wangler', email: 'silvio.wangler@amail.com')
                }
                event(start: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 14:00'), end: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 15:00'), description: eventDescription2, summary: 'Short info2', location: '@home', classification: 'private') {
                    organizer(name: 'Silvio Wangler', email: 'silvio.wangler@mail.com')
                }
            }
        }
        println builder.cal
        builder.cal.validate(true) // throws an exception if its invalid

        def events = builder.cal.getComponents(VEVENT)

        assert 2 == events.size()

        assertEquals 'wrong summary', 'Short info1', events[0].summary.value
        assertEquals 'wrong description', eventDescription1, events[0].description.value
        assertEquals 'wrong summary', 'Short info2', events[1].summary.value
        assertEquals 'wrong description', eventDescription2, events[1].description.value
        
        events.each { VEvent event ->
            assert event.getProperty(Property.TZID).value == 'Europe/Zurich'
            assert event.getProperty(Property.ORGANIZER).value =~ 'silvio\\.wangler@[a]{0,1}mail.com'
        }
    }

    @Test
    void testReminder() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Alarm 123')
                }
            }
        }
        
        builder.cal.validate()
        
        def events = builder.cal.getComponents(VEVENT)
        
        assert 1 == events.size()
        VEvent event = events[0]
        assert event.alarms.size() == 1
        assert event.alarms[0].description.value == 'Alarm 123'
        assert event.alarms[0].trigger.duration.days == 0
        assert event.alarms[0].trigger.duration.hours == 0
        assert event.alarms[0].trigger.duration.minutes == 5
        assert event.alarms[0].trigger.duration.seconds == 0
        assert event.getProperty(Property.TZID).value == 'Europe/Zurich'
        
        println builder.cal.toString()
    }

    @Test
    void testSetDifferentTimeZoneLondon() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text', timezone: 'Europe/London') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Alarm 123')
                }
            }
        }

        builder.cal.validate()

        def events = builder.cal.getComponents(VEVENT)

        assert 1 == events.size()
        VEvent event = events[0]
        assert event.getProperty(Property.TZID).value == 'Europe/London'

        println builder.cal.toString()
    }

    @Test
    void testSetDifferentTimeZoneMontreal() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text', timezone: 'America/Montreal') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Alarm 123')
                }
            }
        }

        builder.cal.validate()

        def events = builder.cal.getComponents(VEVENT)

        assert 1 == events.size()
        VEvent event = events[0]
        assert event.getProperty(Property.TZID).value == 'America/Montreal'

        println builder.cal.toString()
    }

    @Test
    void testAddCategories() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text', categories: 'icehockey, sports') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                }
            }
        }

        builder.cal.validate()

        def events = builder.cal.getComponents(VEVENT)

        assert 1 == events.size()
        VEvent event = events[0]
        assert event.getProperty(Property.CATEGORIES).value == 'icehockey, sports'

        println builder.cal.toString()
    }

    @Test(expected = IllegalArgumentException.class)
    void testSetDifferentTimeZoneUS() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text', timezone: 'US-Eastern:20110928T110000') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Alarm 123')
                }
            }
        }
    }

    @Test
    void testSupportAttendee() {
        builder.calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'Text') {
                    organizer(name:'Silvio', email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Alarm 123')
                    attendees {
                        attendee(email:'a@b.ch', role: REQ_PARTICIPANT, partstat: NEEDS_ACTION, cutype: INDIVIDUAL, rsvp: TRUE)
                        attendee(email:'a@b.it', role: REQ_PARTICIPANT, partstat: NEEDS_ACTION, cutype: INDIVIDUAL, rsvp: FALSE)
                    }
                }
            }
        }
        def events = builder.cal.getComponents(VEVENT)

        assert 1 == events.size()
        VEvent event = events[0]
        assert event.getProperties(ATTENDEE).size() == 2

        for (Attendee attendee : event.getProperties(ATTENDEE)) {
            assert attendee.calAddress.toASCIIString() =~ /mailto:a@b\.(ch|it)/
            assert attendee.getParameter('ROLE') == REQ_PARTICIPANT
            assert attendee.getParameter('PARTSTAT') == NEEDS_ACTION
            assert attendee.getParameter('CUTYPE') == INDIVIDUAL
            assert attendee.getParameter('RSVP') != null
        }
        println builder.cal.toString()
    }
}
