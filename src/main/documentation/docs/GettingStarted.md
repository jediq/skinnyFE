#Getting Started

SkinnyFE comes pre-packaged as a Java executable JAR file, the latest release of this can be downloaded from [here](http://skinnyfe.jediq.com/skinnyFE-all.jar).

##Example 1 : A basic server

The most basic installation requires only 2 files, a json configuration file and an html index file.  

#####config.json
```
{
    "port":8027
}
```

#####index.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
```

Download the JAR and copy the two files above into an empty directory and execute the following command line :

    java -jar skinnyFE-all.jar config.json
    
You should see (something like) the following output on the console:
```
08:37:40.248 [main] INFO  com.jediq.skinnyfe.SkinnyFE [42] - Starting SkinnyFE server on port : 8027
08:37:40.275 [main] INFO  org.eclipse.jetty.util.log [186] - Logging initialized @718ms
08:37:40.681 [main] INFO  o.eclipse.jetty.server.Server [345] - jetty-9.3.z-SNAPSHOT
08:37:40.713 [main] INFO  o.e.j.server.ServerConnector [270] - Started ServerConnector@610f7aa{HTTP/1.1,[http/1.1]}{0.0.0.0:8027}
08:37:40.714 [main] INFO  o.eclipse.jetty.server.Server [397] - Started @1160ms
08:37:40.714 [main] INFO  com.jediq.skinnyfe.SkinnyFE [46] - Started SkinnyFE server on port : 8027
```

You can navigate your browser to `http://localhost:8027` and you should see a simple Hello World! page.

That's it, you're now running SkinnyFE, but admittedly, it's not very useful, the next example adds a resource and you can start
to see how powerful SkinnyFE can be.

##Example 2 : Using a resource

As we progress through the examples, we'll need a back-end resource to interact with, for this we'll be using a local
instance of the open-sourced [json-server](https://github.com/typicode/json-server).  Follow the installation instructions
in the previous link (it's dead easy) and pull down the SkinnyFE examples [db.json](../db.json).  It can then be started using :

```
json-server --port 8009 --watch db.json
```

You should now be able to navigate to `http://localhost:3000` and get the json-server status page.  We'll be using the 
`posts` endpoint which has the following fields, `title`, `author` and `content`.

To render json-server posts with SkinnyFE our files needs to look like :

#####config.json
```
{
  "port":8027,
  "resources":[
    {
      "name":"Posts",
      "url":"http://jsonplaceholder.typicode.com/posts"
    }
  ]
}
```

#####index.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta property="posts" resource="Posts" identifier=""/>
    </head>
    <body>
        <h1>Posts</h1>
        <small>Random lorem ipsum posts for demo purposes.</small>
        {{#each posts}}
            <h2>{{title}}</h2>
            <p>{{body}}</p>
        {{/each}}
    </body>
</html>
```

Again, running the `java -jar skinnyFE-all.jar config.json` command from the directory should give you similar output
in the console as above, and navigating to `http://localhost:8027` and you should see a some 
[Lorem Ipsum](http://www.lipsum.com/) post titles  rendered in the page.


##Example 3 : Passing values to a resource

If this was as addressable as resources got, we wouldn't get very far.  So [moustache](https://mustache.github.io/)
interpolation can be used within the urls, for example `http://api.example.com/todos/{{PATH.1}}` would add the first
element of the requested URLs path into the resource URL.  The templating values made available for urls are :

* `PATH` - An addressable array of the requested URL's path elements
* `COOKIE` - The name of a cookie sent from the client whos value should be embedded
* `URL` - The full requested URL
* `HEADER` - The name of a header sent from the client whos value should be embedded
* `PARAM` - The name of a requested URL query parameter
* `identifier` - The meta identifier from the template

To select and display a particular post in our example, we would use the following files:

#####config.json
```
{
  "port":8027,
  "resources":[
    {
      "name":"Posts",
      "url":"http://jsonplaceholder.typicode.com/posts"
    },
    {
      "name":"Post",
      "url":"http://jsonplaceholder.typicode.com/posts/{{PARAM.id}}"
    },
    {
      "name":"Comments",
      "url":"http://jsonplaceholder.typicode.com/posts/{{PARAM.id}}/comments"
    }
  ],
  "assetsPath":"/assets",
  "assetsFolder":"static/"
}
```

#####index.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta property="posts" resource="Posts"/>   
         <link href="/assets/example.css" media="screen" rel="stylesheet" type="text/css">
    </head>
    <body>
        <h1>Posts</h1>
        <small>Random lorem ipsum posts for demo purposes.</small>
        {{#each posts}}
            <h2><a href="post?id={{id}}">{{title}}</a></h2>
            <p>{{body}}</p>
        {{/each}}
    </body>
</html>
```

#####post.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta property="post" resource="Post"/>
        <meta property="comments" resource="Comments"/>       
         <link href="/assets/example.css" media="screen" rel="stylesheet" type="text/css">
    </head>
    <body>
        <h1>Post : {{post.id}}</h1>
        <h2>{{post.title}}</h2>
        <p>{{post.body}}</p>
        <br>
        <hr>
        <h2>Comments</h2>
        {{#each comments}}
            <h3>{{name}} ({{email}})</h3>
            <p>{{body}}</p>
            <hr>
        {{/each}}
    </body>
</html>
```

#####static/example.css
```
body { font-family:sans-serif; }
```

Running this example shows a couple of extra features as well, using multiple resources within a single template and
serving static content.  

The static content is achieved by the configuration values `assetsPath` and `assetsFolder`.  Any urls starting with the
`assetsPath` value will automatically be transposed into the `assetsFolder` value and served as static content from there.
This allows us to serve local static content without requiring a second server or CDN.


##Example 4 : Creating to a resource

So far in our examples we've been reading resource entities rather than creating new instances, this will change with
example 4.  We create elements by posting values in html `<form>`s.

The files required to perform the simplest of Posts are :

#####config.json
```
{
  "port":8027,
  "resources":[
    {
      "name":"Posts",
      "url":"http://localhost:8009/posts"
    },
    {
      "name":"Post",
      "url":"http://localhost:8009/posts/{{PARAM.id}}"
    }
  ],
  "assetsPath":"/assets",
  "assetsFolder":"static/"
}
```

####index.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta property="posts" resource="Posts"/>
        <link href="/assets/example.css" media="screen" rel="stylesheet" type="text/css">
    </head>
    <body>
        <h1>Posts</h1>
        {{#each posts}}
            <h2><a href="post?id={{id}}">{{title}} <small> by </small> {{author}}</a></h2>
            <p>{{body}}</p>
        {{/each}}

        <hr/>
        <h1>New post</h1>
        <form method="post">
            <input type="text" placeholder="Title" name="posts.title">
            <input type="text" placeholder="Author" name="posts.author">
            <textarea name="posts.content" placeholder="Content"></textarea>
            <button>post</button>
        </form>
    </body>
</html>
```

####post.moustache
```
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta property="post" resource="Post"/>
        <meta property="posts" resource="Posts"/>
        <link href="/assets/example.css" media="screen" rel="stylesheet" type="text/css">
    </head>
    <body>

        <h1>{{post.title}}</h1>
        <p>{{post.content}}</p>
        <br>
        <hr>
        <h2><a href="/">All posts</a></h2>
        {{#each posts}}
            <h3><a href="/post?id={{id}}">{{title}}</a></h3>
            <hr>
        {{/each}}
    </body>
</html>
```

SkinnyFE uses the [POST/REDIRECT/GET](https://en.wikipedia.org/wiki/Post/Redirect/Get) design pattern for creating
elements, this allows us to render the values without breaking the back or refresh buttons.

The names of the form values should correspond to the Resource fields that need updating prefixed with the resource
property name, in our example, `posts.title`.  

SkinnyFE will look for all of the fields POSTed in and will send any
values that it finds to the Resources url, so it will send the `title` value to the `posts` resource.  All values for
the same resource are POSTed at the same time.

