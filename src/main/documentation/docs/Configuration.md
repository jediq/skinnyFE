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