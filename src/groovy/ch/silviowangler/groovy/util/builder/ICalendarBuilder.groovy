package ch.silviowangler.groovy.util.builder

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


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

  /**
   * Declares a parent/child relations
   */
  public static final Map PARENT_CHILD_CONSTRAINTS = ['calendar': ['events'], 'events': ['event'], 'event': ['organizer']]

  public ICalendarBuilder() {
    super();
    this.stringBuilder = new StringBuilder()
  }

  protected void setParent(Object parent, Object child) {

    log.debug "set parent $parent $child"
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
        stringBuilder << "ORGANIZER;CN=${params.name}:MAILTO:${params.email}\n"
      }
    }
    log.debug "createNode $nodeName, $params"
    return nodeName
  }

  private void handleCalendarNode(Map params, nodeName) {

      stringBuilder << 'BEGIN:VCALENDAR\n'
      stringBuilder << 'CALSCALE:GREGORIAN\n'
      stringBuilder << 'VERSION:2.0\n'
      stringBuilder << "PRODID:${params.prodid ?: '-//Grails iCalendar event builder//NONSGML Grails Events V0.1//EN'}\n"
      stringBuilder << 'METHOD:PUBLISH\n'

  }

  private void handleEventNode(Map params, nodeName) {

      stringBuilder << 'BEGIN:VEVENT\n'
      if (params.start) handleDateField(params.start, 'DTSTART')
      if (params.end) handleDateField(params.end, 'DTEND')
      if (params.summary) stringBuilder << "SUMMARY:${params.summary}\n"
      if (params.description) stringBuilder << "DESCRIPTION:${params.description}\n"
      handleDateField(new Date(), 'CREATED')
      stringBuilder << 'SEQUENCE:1\n'
      stringBuilder << "UID:${params.uid ?: "${params.summary.hashCode()}@localhost"}\n"
      if (params.location) stringBuilder << "LOCATION:${params.location}\n"
      stringBuilder << "CLASS:${params.classification ? params.classification.toUpperCase() : 'PUBLIC'}\n"

  }

  private void handleDateField(Date date, String fieldName) {
    stringBuilder << "$fieldName:${date.format('yyyyMMdd')}T${date.format('HH:mm')}Z\n"
  }

  protected Object createNode(Object nodeName, Map params, Object o1) {
    throw new RuntimeException('Unsupported mode')
  }

  protected void nodeCompleted(Object parent, Object node) {
    log.debug "nodeCompleted $parent, $node"
    super.nodeCompleted(parent, node);
    if (node == 'calendar') stringBuilder << 'END:VCALENDAR'
    if (node == 'event') stringBuilder << 'END:VEVENT\n'
  }

  public String toString() { stringBuilder.toString() }
}