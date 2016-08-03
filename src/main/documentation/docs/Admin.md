#Administration

SkinnyFE has an admin interface where it exposes various metrics to do with it's current performance.

This admin interface is enabled by adding an admin port to the config file, for example, `{ "adminPort":8002 }`.  With this configuration `http://localhost:8002/healthcheck` will give a quick response showing that the application is up and running, useful for load balancers etc.

The metrics generated via the admin interface are not persisted and do not exist beyond the lifecycle of the current process.

##Requests
When a request is received the total execution time is recorded in the metrics.  A counter of the number of requests is also recorded for each URL.

##Resources
When SkinnyFE makes a call out to a Resource, the call time is recorded in the metrics.  A counter of the number of Resource requests is also recorded for each Resource.

##Static Resources
When a static resource request is received the total execution time is recorded in the metrics.  A counter of the number of requests is also recorded for each URL.

##Healthcheck
A healthcheck is exposed for use by load-balancers, uptime monitoring etc.
