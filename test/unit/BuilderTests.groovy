import ch.silviowangler.groovy.util.builder.ICalendarBuilder
import grails.test.GrailsUnitTestCase

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

class BuilderTests extends GrailsUnitTestCase {

  ICalendarBuilder builder

  protected void setUp() {
    super.setUp()
    this.builder = new ICalendarBuilder()
  }

  protected void tearDown() {
    super.tearDown()
    this.builder = null
  }

  void testBuilderWritesCalendar() {
    builder.calender()
    assertNull builder.cal
  }

  void testSimpleTwoEvents() {

    builder.calendar {
      events {
        event(start: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 14:00'), end: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 15:00'), description: 'Events description', summary: 'Short info1') {
          organizer(name: 'Silvio Wangler', email: 'silvio.wangler@amail.com')
        }
        event(start: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 14:00'), end: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 15:00'), description: 'hell yes', summary: 'Short info2', location: '@home', classification: 'private') {
          organizer(name: 'Silvio Wangler', email: 'silvio.wangler@mail.com')
        }
      }
    }
    println builder.cal
    builder.cal.validate(true) // throws an exception if its invalid    
    assertEquals 2, builder.cal.components.size()
  }
}
