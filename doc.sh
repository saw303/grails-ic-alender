#!/bin/bash

grails doc --pdf --non-interactive

git clone https://saw303@github.com/saw303/grails-ic-alender.git --branch gh-pages gh-pages --single-branch > /dev/null
cd gh-pages
git rm -rf .
cp -r ../docs/guide/. ./
git add *
git commit -a -m "Updating docs"
git push origin HEAD
cd ..
rm -rf gh-pages