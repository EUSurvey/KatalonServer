# Katalon server project
This is a small Spring Boot server which purpose is to run Katalon tests.

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
1. Call http://{yourDeploymentIP}:{yourDeploymentPort}/katalon/stop, to stop the Katalon running process and all its children process.


