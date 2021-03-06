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


##Headers
If a resource requires headers passing into it, for example, an "X-Forwarded-For" header or an "Accept" header they can
 be added to the resource configuration as below:

```
{
  "name":"Todos",
  "url":"http://api.example.com/todos",

  "headers":{
    "X-Forwarded-For": "192.168.0.0/32",
    "Accept":"application/json"
  }
}
```

##Input Validation
Like all responsible web users, we should validate the data passed to us prior to sending it to other services.  SkinnyFE
achieves this with regular expression patterns that can be configured against each of the input fields.

```
{
  "name":"Todos",
  "url":"http://api.example.com/todos/{{PARAM.greeting}}",
  "inputValidators":{
     "greeting":"hi|hello"  
  }
}
```

The example above will validate the todo greeting that is passed to the url is either "hi" or "hello".


##Resource Protection

Sometimes you may with to protect your instance of SkinnyFE from talking to certain hosts, for instance calling back
into localhost or 127.0.0.1, or other known hosts on your internal network.  This can be managed using the 
`protectedHostsRegex` configuration value; for example : 

      "protectedHostsRegex":".*localhost.*",

would stop SkinnyFE from making resource calls to itself.


