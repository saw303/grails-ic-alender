# Grails iCalendar Plugin
[![Build Status](https://travis-ci.org/saw303/grails-ic-alender.svg?branch=master)](https://travis-ci.org/saw303/grails-ic-alender)

## Grails Version Support
### Grails 2.x Support
Use version 0.4.4 in Grails 2.x projects by adding it as a plugin dependency in `BuildConfig.groovy`.

    plugins {
        ...
        compile ":ic-alendar:0.4.5"
    }
    
### Grails 3.x
Use version 0.5.0 or above for Grails 3.x projects by adding it as a dependency in `build.gradle`.

    dependencies {
        ...
        compile "org.grails.plugins:iCalendar:0.5.1"
    }

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

This plugin uses the [ical4j][ical4j] API and is therefore [iCalendar RFC](https://tools.ietf.org/html/rfc5545) compliant. The output has been tested against the Google Calendar importer, Microsoft Outlook and Mozilla Sunbird.

The plugin is at the current stage of development limited to events only. That means that you currently can only export `VEVENTS`.

## What else can you do?

This documentation does not claim to cover all the features that are implemented in the iCalendar plugin.
But there is a [Unit Test Suite][unittest] that covers the feature set of this plugin and therefore a very good entry point if you
are looking for an overview.


### Disable the plugin per controller

If you do not want to have this feature injected into every controller you can specify which controllers to be excluded in Config.groovy

    grails.plugins.ical.controllers.exclude = ['excludedTest']

This Configuration parameter has to be a list of controller names!

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
                event(
                    start: new Date(), 
                    end: new Date(), 
                    description: 'Some large text', 
                    summary: 'Project stand up meeting', 
                    utc: true // optional parameter (default = false)
                )
            }
        }
    }

[ical4j]: http://wiki.modularity.net.au/ical4j/index.php?title=Main_Page
[unittest]: https://github.com/saw303/grails-ic-alender/blob/master/test/unit/BuilderTests.groovy

### All day events

Version `0.4.1` introduces DSL support for all day events. You can use a String by defining a date like `12.10.2014`.


    calendar {
        events {
            allDayEvent(date: '12.04.2013', summary: 'Text') {
                organizer(name: 'Silvio', email: 'abc@ch.ch')
                reminder(minutesBefore: 5, description: 'Alarm 123')
            }
        }
    }

Currently there is a limitation that the date format has to be `DD.MM.YYYY`. The fix of this limitation will be address in a future release.

Another way is to simply provide a Date instance

    calendar {
        events {
            allDayEvent(date: new java.util.Date(), summary: 'Text') {
                organizer(name: 'Silvio', email: 'abc@ch.ch')
                reminder(minutesBefore: 5, description: 'Alarm 123')
            }
        }
    }

### Adding X-Properties to a calendar

For some calendar clients such as Microsoft Outlook it can be useful to set vendor specific properties such as [`X-PRIMARY-CALENDAR`](https://msdn.microsoft.com/en-us/library/ee219226(v=exchg.80).aspx).
Since `version 0.4.5` (for Grails 2.x) and `version 0.5.1` (for Grails 3.x) you can set custom properties like this.

    calendar(xproperties: ['X-WR-RELCALID': '1234', 'X-PRIMARY-CALENDAR': 'TRUE']) {
        events {
            allDayEvent(date: new java.util.Date(), summary: 'Text') {
                organizer(name: 'Silvio', email: 'abc@ch.ch')
                reminder(minutesBefore: 5, description: 'Alarm 123')
            }
        }
    }