#Resources

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

We can also pass values into the url to select a specific resource see 
[Getting Started example 3](GettingStarted#example-3-passing-values-to-a-resource).