# Katalon server project
This is a small Spring Boot server which purpose is to run Katalon tests.
* Master branch build [![Build Status](https://travis-ci.com/EUSurvey/KatalonServer.svg?branch=master)](https://travis-ci.com/EUSurvey/KatalonServer)
* [Latest Sonar Cloud analysis](https://sonarcloud.io/dashboard?id=Katalon_Server_Sonar) ![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Katalon_Server_Sonar&metric=bugs) ![Code smells](https://sonarcloud.io/api/project_badges/measure?project=Katalon_Server_Sonar&metric=code_smells) ![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Katalon_Server_Sonar&metric=coverage)
* Organization's [Waffle io](https://waffle.io/EUSurvey/EUSurvey) [![Waffle.io - Columns and their card count](https://badge.waffle.io/EUSurvey/EUSurvey.svg?columns=all)](https://waffle.io/EUSurvey/EUSurvey) 

## Installation requirements
1. Katalon
1. Tomcat
1. Java 10
1. Create a katalon project and a test suite

## Getting started
1. Modify the spring properties to match your configuration.
1. Build the project, and deploy it on your servlet container.

## Usage
1. Call http://{yourDeploymentIP}:{yourDeploymentPort}/katalon/status, to see if the server is up and running;
1. Call http://{yourDeploymentIP}:{yourDeploymentPort}/katalon/start/{nameOfATestSuite}/{browserName}, to run a test suite, the server will answer with the test suite's JUnit report in XML. The browser name can be IE, Chrome, Edge, Firefox.
1. Call http://{yourDeploymentIP}:{yourDeploymentPort}/katalon/stop, to kill all the Katalon running processes (including children processes) initialy created using the application server.


