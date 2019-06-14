# Welcome

Welcome to [SkinnyFE](../), a Front End framework that removes the ability to embed business logic in a front end application.

The easiest way to learn to use SkinnyFE within your projects is to follow the [Getting Started](GettingStarted) guide,
which provides simple steps to configuring and starting your first server and embedding API calls.

More advanced configuration options can be found in the [Configuration](Configuration) section of the documentation.

The three main components of a SkinnyFE deployment are [Resources](Resources), [Templates](Templates) and [Enrichers](Enrichers).  

## Resources
Resources are external http(s) endpoints that serve the JSON data that SkinnyFE requires. [(more...)](Resources)

## Templates
Templates are [moustache](https://mustache.github.io/) html documents that are used to serve and render. [(more...)](Templates)

## Enrichers
Enrichers are Javascript functions that can be used to transform the output of [Resources](Resources) so that they
can be more easily consumed by [Templates](Templates). [(more...)](Enrichers)
