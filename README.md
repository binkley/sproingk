# Sproink

Spring Boot with Kotlin

This software is in the Public Domain.  Please see [LICENSE.md](LICENSE.md).

[![CircleCI](https://img.shields.io/circleci/project/github/binkley/sproink.svg)](https://circleci.com/gh/binkley/sproingk) [![unlicense](https://img.shields.io/badge/un-license-green.svg?style=flat)](http://unlicense.org)]

Would you like to contribute?  Ask me!  I'm happy to take on contributors or
pull requests.  Please see the [Story
Wall](https://github.com/binkley/sproingk/projects/2).

## Spring features

* Spring 5 M4
* Actuator
* Boot with REST
* Sleuth
* Unit test with Boot 1.4 features
* Integration test with Boot 1.4 features
* Shows git details with actuator `/info` endpoint
* Example of REST batch round trip with status

## Agile features

* [Story wall](https://github.com/binkley/sproingk/projects/2)
* [CI](https://circleci.com/gh/binkley/sproingk)

## Other features

* Latest JUnit 5
* Access logging ala CLF style
* Quieter tests and runtime (always in progress!)

## Things to think about

* Update Spring Boot to 1.5 RC when Sleuth catches up
* Logging is still to noisy during build and tests
* Some "bleeding edge" difficulties, e.g., Spring REST docs + Spring 5 beta
* Replace nested tests with scenario tests (see [Introduce first-class support for scenario tests #48](https://github.com/junit-team/junit5/issues/48), pending [5.0 M5](https://github.com/junit-team/junit5/milestone/8) drop of JUnit)

## Some reading

* [Developing Spring Boot applications with Kotlin](https://spring.io/blog/2016/02/15/developing-spring-boot-applications-with-kotlin)
* [Dipping into Spring Boot with Kotlin](https://medium.com/@mchlstckl/dipping-into-spring-boot-with-kotlin-31881edd13c2#.h26gsle9y)
* [The Journey of a Spring Boot application from Java 8 to Kotlin: The Application Class](http://engineering.pivotal.io/post/spring-boot-application-with-kotlin/)
* [Kotlin extensions for Spring projects](https://github.com/sdeleuze/spring-kotlin)
* [JUnit 5 with Spring Boot (plus Kotlin)](https://objectpartners.com/2016/07/26/junit-5-with-spring-boot-plus-kotlin/)
* [Testing improvements in Spring Boot 1.4](https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4)
* [REST and long-running jobs](http://farazdagi.com/blog/2014/rest-long-running-jobs/)
