# Sproink

_Spring Boot with Kotlin_.  [Try it live!](http://sproingk-binkley.boxfuse.io:8080/).  Fork me.

## Update

This demo has been broken for much of 2017 as the technologies it relies on
have been moving targets (think of this code as a "canary in the coal mine"
for production readiness).  Things are much better now, and the full pipeline
to AWS is working as expected. --2018/01/06

It still fails on Java 9 as the JDK has deprecated an XML API needed by Spring.

## License

This software is in the Public Domain.  Please see [LICENSE.md](LICENSE.md).

[![CircleCI](https://img.shields.io/circleci/project/github/binkley/sproingk.svg)](https://circleci.com/gh/binkley/sproingk) [![unlicense](https://img.shields.io/badge/un-license-green.svg?style=flat)](http://unlicense.org)]

Would you like to contribute?  Ask me!  I'm happy to take on contributors or
pull requests.  Please see the [Story
Wall](https://github.com/binkley/sproingk/projects/2).

## Spring features

* Spring 5
* Actuator
* Boot with REST
* Springfox
* Junit 5 &mdash; unit and integration tests
* Example of REST batch round trip with status

## Agile features

* [Story wall](https://github.com/binkley/sproingk/projects/2)
* [CI](https://circleci.com/gh/binkley/sproingk)

## Other features

* Quieter tests and runtime (always in progress!)

## Things to think about

* Update to openjdk 9:
  - Boxfuse support
  - `module-info.java` for `java.activation` and `java.xml.bind`
  - JDK 9 deprecates those modules
  - `koblinc` does not like target bytecode `1.9`
* Update to [Kotlin coding conventions](http://kotlinlang.org/docs/reference/coding-conventions.html) when IntelliJ catches up
* Logging is still to noisy during build and tests
* Some "bleeding edge" difficulties, e.g., Spring REST docs + Spring 5 beta
* Replace nested tests with scenario tests ala [Introduce first-class support for scenario tests #48](https://github.com/junit-team/junit5/issues/48)
* Java 9 complains about "illegal reflective access" by Kotlin compiler
* Stuck on surefire 2.19 until 2.20+ does not break
* Restore HTTP access logging to console

## Some reading

* [Developing Spring Boot applications with Kotlin](https://spring.io/blog/2016/02/15/developing-spring-boot-applications-with-kotlin)
* [Dipping into Spring Boot with Kotlin](https://medium.com/@mchlstckl/dipping-into-spring-boot-with-kotlin-31881edd13c2#.h26gsle9y)
* [The Journey of a Spring Boot application from Java 8 to Kotlin: The Application Class](http://engineering.pivotal.io/post/spring-boot-application-with-kotlin/)
* [Kotlin extensions for Spring projects](https://github.com/sdeleuze/spring-kotlin)
* [JUnit 5 with Spring Boot (plus Kotlin)](https://objectpartners.com/2016/07/26/junit-5-with-spring-boot-plus-kotlin/)
* [Testing improvements in Spring Boot 1.4](https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4)
* [REST and long-running jobs](http://farazdagi.com/blog/2014/rest-long-running-jobs/)
