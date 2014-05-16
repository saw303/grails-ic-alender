# Grails iCalendar Plugin
[![Build Status](https://travis-ci.org/saw303/grails-ic-alender.svg?branch=master)](https://travis-ci.org/saw303/grails-ic-alender)

## Introduction

    class TestController {
      def index = {

        render(contentType: 'text/calendar', filename: '<optional filename>') {
          calendar {
            events {
              event(start: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 14:00'),
                       end: Date.parse('dd.MM.yyyy HH:mm', '31.10.2009 15:00'),
                       description: 'Events description',
                       summary: 'Short info1') {
                organizer(name: 'Silvio Wangler', email: 'a@b.com')
              }
              event(start: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 14:00'),
                      end: Date.parse('dd.MM.yyyy HH:mm', '01.11.2009 15:00'),
                      description: 'hell yes',
                      summary: 'Short info2',
                      location: '@home',
                      classification: 'private'){
                organizer(name: 'Silvio Wangler', email: 'b.c@d.com')
              }
            }
          }
        }
      }
    }

This plugin uses the [ical4j][ical4j] API and is therefore iCal RFC compliant. The output has been tested against the Google calendar importer, Microsoft Outlook and Mozilla Sunbird.

The plugin is at the current stage of development limited to events only. That means that you currently can only export `VEVENTS`.

## What else can you do?

This documentation does not claim to cover all the features that are implemented in the iCalendar plugin.
But there is a [Unit Test Suite][unittest] that covers the feature set of this plugin and therefore a very good entry point if you
are looking for an overview.

### Invite attendees

    render(contentType: 'text/calendar') {
        calendar {
            events {
                event(start: new Date(), end: (new Date()).next(), summary: 'We need to talk') {
                    organizer(name:"Peter O'Brien", email:'abc@ch.ch')
                    reminder(minutesBefore: 5, description: 'Your meeting starts in 5 minutes!')
                    attendees {
                        attendee(email:'bill.gates@microsoft.com', role: REQ_PARTICIPANT, partstat: NEEDS_ACTION, cutype: INDIVIDUAL, rsvp: TRUE)
                        attendee(email:'carmen.breeze@google.com', role: REQ_PARTICIPANT, partstat: NEEDS_ACTION, cutype: INDIVIDUAL, rsvp: FALSE)
                    }
                }
            }
        }
    }
    
### Use UTC dates

Since `version 0.4.0` the plugin supports UTC dates. Each event accepts an optional parameter called `utc`. If the parameter is missing
UTC is set to `false`.

    render(contentType: 'text/calendar') {
        calendar {
            events {
                event(start: new Date(), end: new Date(), description: 'Some large text', summary: 'Project stand up meeting', utc: true)
            }
        }
    }

[ical4j]: http://wiki.modularity.net.au/ical4j/index.php?title=Main_Page
[unittest]: https://github.com/saw303/grails-ic-alender/blob/master/test/unit/BuilderTests.groovy