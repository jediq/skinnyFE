#Welcome

Welcome to SkinnyFE, a Front End framework that removes the ability to embed business logic in a front end application.

The easiest way to learn to use SkinnyFE within your projects is to follow the [Getting Started](GettingStarted) guide,
which provides simple steps to configuring and starting your first server and embedding API calls.

More advanced configuration options can be found in the [Configuration](Configuration) section of the documentation.

The two main components of a SkinnyFE deployment are [Resources](#resources) and [Templates](#templates).  

##Resources
Resources are external http(s) endpoints that serve the JSON data that SkinnyFE requires.  A basic resource configuration
in the `resources` section of the `config.json` file looks like : 

```
{
    "name":"Todos",
    "url":"http://api.example.com/todos"
}   
```

The name attribute is used within [templates](#templates) to reference the resource.

The url attribute is the addressable endpoint of the resources.

If this was as addressable as resources got, we wouldn't get very far.  So [moustache](https://mustache.github.io/)
interpolation can be used within the urls, for example `http://api.example.com/todos/{{PATH.1}}` would add the first
element of the requested URLs path into the resource URL.  The templating values made available for urls are :

* `PATH` - An addressable array of the requested URL's path elements
* `COOKIE` - The name of a cookie sent from the client whos value should be embedded
* `URL` - The full requested URL
* `HEADER` - The name of a header sent from the client whos value should be embedded
* `PARAM` - The name of a requested URL query parameter
* `identifier` - The meta identifier from the template

##Templates

Templates are [moustache](https://mustache.github.io/) html documents that are used to serve and render.  
In its simplest form, SkinnyFE will look in the current working directory for files that match the path of the URL,
so browsing `http://localhost:8070/furniture/tables` will look for a file called `tables.moustache` in a directory 
called `furniture` in the current directory.

The default location (of `.`) that SkinnyFE looks in can be overridden with the `defaultTemplates` configuration value.
See [Configuration](Configuration).

To reference a particular [resource](#resources) within a template it must be defined within a `meta` attribute in the 
documents `head` section.

    <meta property="todos" resource="Todos" identifier=""/>
    
Once the template is requested and resolved it will load the resource defined in `resource`, in this example `Todos`,
and add it to the moustache context for rendering within the page as the property `todos`.  This can then be referenced
in the markup as `{{todos}}`.


