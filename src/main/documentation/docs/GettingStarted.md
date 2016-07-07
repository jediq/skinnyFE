#Getting Started

SkinnyFE comes pre-packaged as a Java executable JAR file, the latest release of this can be downloaded from [here](http://skinnyfe.jediq.com/skinnyFE-all.jar).

The most basic installation requires only 2 files, a json configuration file and an html index file.  

#####config.json
```
${example1_configjson}
```

#####index.moustache
```
${example1_indexmoustache}
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

That's it, you're now running SkinnyFE, but admittedly, it's not very useful, the [next example](Examples) adds a resource and you can start
to see how powerful SkinnyFE can be.

