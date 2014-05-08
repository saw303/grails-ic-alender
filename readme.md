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

This plugin uses ical4j api and is therefore iCal RFC compliant. The output has been tested against the Google calendar importer and Mozilla Sunbird.

The plugin is at the current stage of development limited to events only. That means that you currently can only export VEVENTS.
