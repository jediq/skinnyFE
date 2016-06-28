#SkinnyFE - Skinny Frontend

[![Build Status](https://travis-ci.org/jediq/skinnyFE.svg?branch=master)](https://travis-ci.org/jediq/skinnyFE)
[![license](https://img.shields.io/github/license/jediq/skinnyFE.svg?maxAge=2592000)]()
[![Gitter](https://badges.gitter.im/jediq/skinnyFE.svg)](https://gitter.im/jediq/skinnyFE?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

SkinnyFE is a front end framework that removes the ability to embed business logic in a front end application.

The website [skinnyfe.jediq.com](http://skinnyfe.jediq.com) is the best place to find examples and documentation.  You don't need to check out the code to run a skinnyFE server.


##Building

SkinnyFE is built using gradle with the following command : 

    gradle clean build shadowJar
    
to execute with test coverage and local sonarqube integration use : 

    gradle clean test jacoco sonarqube -Dsonar.scm.disabled=true


##Executing

To start a SkinnyFE server use the following : 

    java -jar build/libs/skinnyFE-all.jar <config_json>
    
    
##Documentation

The documentation is built using [mkdocs](), this can be installed using PIP with : 

    pip install mkdocs
    
The source is located at `src/main/documentation`
