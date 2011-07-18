package ch.silviowangler.groovy.util.builder

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.util.UidGenerator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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

  private Log log = LogFactory.getLog(ICalendarBuilder.class)
  private Calendar cal
  private VEvent e

  /**
   * Declares a parent/child relations
   */
  public static final Map PARENT_CHILD_CONSTRAINTS = ['calendar': ['events'], 'events': ['event'], 'event': ['organizer']]

  /**
   * Default constructor
   * @since 0.1
   */
  public ICalendarBuilder() {
    super();
  }

  /**
   * Entry point for external (not inline) closures such as
   *
   * <code>
   * def c = {*  calender {*      events {*        ....
   *}*}*}* builder.invokeMethod('translate', c)
   * </code>
   * @since 0.2
   */
  public void translate(Closure c) {
    c.call()
  }

  @Override
  protected void setParent(Object parent, Object child) {

    log.debug "set parent $parent $child"
    if (parent == 'translate') return // skip it because its the special entry if the builder is not working in inline mode
    if (!PARENT_CHILD_CONSTRAINTS[parent]?.contains(child)) {
      if (!PARENT_CHILD_CONSTRAINTS[parent]) throw new IllegalArgumentException("Unkown element $parent")
      throw new IllegalArgumentException("Element $child not possible for parent element $parent. Parent element allows the following child elements ${PARENT_CHILD_CONSTRAINTS[parent].toListString()}")
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
    if (nodeName == 'event') handleEventNode(params, nodeName)

    if (nodeName == 'organizer') {
      if (params.name && params.email) {
        e.properties << new Organizer('mailto:organizer@email.com')
      }
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
    TimeZone timezone = registry.getTimeZone("Europe/Zurich")
    VTimeZone tz = timezone.vTimeZone

    e = new VEvent(new DateTime(params.start),
            new DateTime(params.end), params.summary)
    /*
    set inetadress to null otherwise it takes awful lots of time to resolve a hostname or ip adress
     */
    e.properties << new UidGenerator(null, 'iCalPlugin').generateUid()
    e.properties << tz.timeZoneId
    if (params.location) e.properties << new Location(params.location)
    if (params.description) e.properties << new Description(params.description)
    if (params.classification) e.properties << getClazz(params.classification)
    this.cal.components << e
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
    this.e = null
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
