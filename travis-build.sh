#!/bin/bash
set -e
./grailsw refresh-dependencies --non-interactive
./grailsw test-app --non-interactive
./grailsw doc --pdf --non-interactive