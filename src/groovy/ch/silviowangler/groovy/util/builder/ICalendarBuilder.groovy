package ch.silviowangler.groovy.util.builder

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.component.VEvent
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

  private StringBuilder stringBuilder;
  private Log log = LogFactory.getLog(ICalendarBuilder.class)

  private Calendar cal
  private VEvent e

  /**
   * Declares a parent/child relations
   */
  public static final Map PARENT_CHILD_CONSTRAINTS = ['calendar': ['events'], 'events': ['event'], 'event': ['organizer']]

  public ICalendarBuilder() {
    super();
  }

  public void translate(Closure c) {
    c.call()
  }

  protected void setParent(Object parent, Object child) {

    log.debug "set parent $parent $child"
    if (parent == 'translate') return // skip it because its the special entry if the builder is not working in inline mode
    if (!PARENT_CHILD_CONSTRAINTS[parent]?.contains(child)) {
      if (!PARENT_CHILD_CONSTRAINTS[parent]) throw new IllegalArgumentException("Unkown element $parent")
      throw new IllegalArgumentException("Element $child not possible for parent element $parent. Parent element allows the following child elements ${PARENT_CHILD_CONSTRAINTS[parent].toListString()}")
    }
  }

  protected Object createNode(Object o) {
    return createNode(o, [:])
  }

  protected Object createNode(Object o, Object o1) {
    throw new RuntimeException('Unsupported mode')
  }

  protected Object createNode(Object nodeName, Map params) {
    if (nodeName == 'calendar') handleCalendarNode(params, nodeName)
    if (nodeName == 'event') handleEventNode(params, nodeName)

    if (nodeName == 'organizer') {
      if (params.name && params.email) {
        e.properties << new Organizer('http://www.silviowangler.ch')
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

    e = new VEvent(new net.fortuna.ical4j.model.Date(params.start.time),
            new net.fortuna.ical4j.model.Date(params.end.time), params.summary)
    e.properties << new UidGenerator('1').generateUid()
    if (params.location) e.properties << new Location(params.location)
    if (params.description) e.properties << new Description(params.describtion)
    if (params.classification) e.properties << getClazz(params.classification)
    this.cal.components << e
  }

  protected Object createNode(Object nodeName, Map params, Object o1) {
    throw new RuntimeException('Unsupported mode')
  }

  public String toString() {
    this.cal?.toString()
  }

  /**
   * Clears the internal iCalendar buffer
   */
  public void reset() {
    this.cal = null
  }

  private Clazz getClazz(String value) {
    if (value.toLowerCase() == 'public') return Clazz.PUBLIC
    if (value.toLowerCase() == 'private') return Clazz.PRIVATE
    if (value.toLowerCase() == 'confidential') return Clazz.CONFIDENTIAL
    return new Clazz(value)
  }

  public Calendar getCal() {
    this.cal
  }
}
