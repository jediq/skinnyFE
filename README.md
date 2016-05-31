# skinnyFE - Skinny Frontend playground


##Front end rendering
```
<!DOCTYPE html>
<html lang="en-US">

    <head>
        <meta property="car" resource="Vehicle" identifier="PATH.id"/>
        <meta property="user" resource="User" identifier="COOKIE.uuid" failOnError="true"/>
    </head>

    <body>
        <h1>{{car.registration}}</h1>

        <h2>{{user.displayName}}</h2>

        <form method="POST" action="https://dvsa.gov/vehicle">
            <input type="hidden" name="resource" value="Vehicle"/>
            <input type="hidden" name="identifier" value="PATH.id"/>

            <label for="registration">Registration</label>
            <input name="registration" value="car.registration"/>

            <input type="submit"/>
        </form>
    </body>
</html>
```
    
##Back end configuration
```
{
    "resources":[
        {
            name:"Vehicle",
            url:"https://vehicle.api.dvsa.gov/{identifier}",
            methods:"GET,POST,PUT,DELETE"
        },
        {
            name:"User",
            url:"https://user.api.dvsa.gov/{identifier}",
            methods:"GET"
        }
    ]
}
```
