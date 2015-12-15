package ch.silviowangler.grails.icalender

import net.fortuna.ical4j.model.property.XProperty

/**
 * Created by Silvio Wangler on 15/12/15.
 */
class CustomProperty extends XProperty {

    CustomProperty(String aName, String aValue) {
        super(aName, aValue)
    }
}
