#Configuration
All configuration is performed in the configuration json file that is passed through the java commmand, by convention
this is `config.json` and that's how we'll describe it in these files.

SkinnyFE tries to follow convention over configuration, in it's simplest form it only requires a single value, the port
to serve on.

```
{
    "port":8020
}
```

##Changing the default port
You guessed it, `port` is a root level config value with an integer denoting the port to serve from.  To serve from port
8050 use...

```
{
    "port":8050
}
```
Too simple.

##Enabling Admin services

SkinnyFE has a number of admin metrics, measures and healthchecks that are made accessible as JSON through an http
 endpoint.  This is configured using:
 
 ```
 {
    "adminPort":8051
 }
 ```
 
!!! note "Note:"
    If this value is not set the admin services are not exposed.   


##Serving static content

We can serve static content by using the configuration values `assetsPath` and `assetsFolder`.  Any urls starting with the
`assetsPath` value will automatically be transposed into the `assetsFolder` value and served as static content from there.
This allows us to serve local static content without requiring a second server or CDN.
```
{
  "assetsPath":"/assets",
  "assetsFolder":"static/"
}
```

##Caching templates

Templates can be cached for a configurable period, once that period has expired they will be re-read from the disk.
The default cache time is 0 and therefore no caching.  The `millisToCacheTemplates` root level numeric value is
used to configure this.  The below example would cache templates for 1 second.
```
{
  "millisToCacheTemplates":1000
}
```

##Changing default locations

The default locations that SkinnyFE looks in to load templates and fragments can be modified through the configuration
file.  The values used are relative to the directory that the process was started in, unless they start with `/`.
```
{
  "defaultTemplates":"myTemplates",
  "defaultFragments":"myFragments"
}
```


##Protecting resources

Sometimes you may with to protect your instance of SkinnyFE from talking to certain hosts, for instance calling back
into localhost or 127.0.0.1, or other known hosts on your internal network.  This can be managed using the 
`protectedHostsRegex` configuration value; for example : 
```
{
  "protectedHostsRegex":".*localhost.*"
}
```
would stop SkinnyFE from making resource calls to itself.


##Virtual Hosts

Multiple SkinnyFE configs can be registered on the same port, these can be distinguished by naming the Virtual Host
that is pointing at the port.
```
{
  "virtualHost":"myhost.com"
}
```
