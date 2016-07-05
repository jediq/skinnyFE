#Templates

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


