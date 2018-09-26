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
1. Call http://<yourDeploymentIP>:<yourDeploymentPort>/<nameOfATestSuite>/status
1. Call http://<yourDeploymentIP>:<yourDeploymentPort>/<nameOfATestSuite>/start
