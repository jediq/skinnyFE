#Enrichers

Enrichers are Javascript functions that can be used to transform the output of [Resources](Resources) so that they
can be more easily consumed by [Templates](Templates).

SkinnyFE executes Enrichers on the Server Side after it has made the call(s) to resources but before it renders templates.

An enricher has to conform to the following javascript interface :

```
var enrich = function(dataString) { return dataString; }
```

The `dataString` parameter is a string representation of a valid [JSON](http://json.org) object.  Once this object has
been enriched then another string representation of a valid JSON object needs to be passed back.

An example enricher (from [example 4](/Examples#example-4-creating-to-a-resource)) is shown below using the ECMA standard JSON global objects
 `parse` method to deserialise the `dataString` parameter into a JSON object so that it can be manipulated and then 
 serialised back using the `stringify` method prior to returning the string representation.

```
var enrich = function(dataString) {
  var data = JSON.parse(dataString);

  data.posts.array.forEach(function(post) {
    post.title = post.title ? post.title.toUpperCase() : "";
    post.author = post.author ? post.author.toUpperCase() : "";
  });

  return JSON.stringify(data);
};
```

##Resource Data

The response to each Resource request is embedded within the dataString, each one is namespaced using the name that
is declared within the Template for that Resource.  So for example, a template with the resource defined as 
`<meta property="car" resource="Vehicle" identifier="{{PATH.2}}"/>` will have the response from `Vehicle` placed in a
variable called `car`.

Some metadata about the Resource response, including the response code is also added to the variable under a child
 called `_meta` so our car example would look something like:

```
{
    "car":{
        "make":"FORD",
        "model":"FOCUS",
        "reg":"DN54TRE",
        "_meta":{
            "code":200,
            "reason":""
        }
    }
}
```

##Forcing

Enrichers have a number of functions that they can call to modify the result of the SkinnyFE call.  These are available
through the FORCE global object.


###Response Code
`FORCE.responseCode(number)` changes the response code that is passed back to the caller to be the number parameter.
For example, you may wish to return the same response code as the Resource you are calling serves to you.

```
    force.responseCode(data.car._meta.code);
```

###Template
`FORCE.template(pathToTemplate)` will cause SkinnyFE to load a different Template to render the results to.  

!!!NOTE
    This does not load any references to Resources that the new Template will have so you'll need to be careful not
     to force the template to one which requires specific not-loaded values.

The path is relative to the `baseLocation` (by default the startup directory) that SkinnyFE has defined.

