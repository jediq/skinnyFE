# skinnyFE - Skinny Frontend playground

SkinnyFE is a front end framework that removes the ability to embed business logic in a front end application.

At it's core, SkinnyFE declares within the HTML template the RESTful API resources that are required to render the page.  These resource references are then looked up in configuration and HTTP calls are made.  Finally, the response data from the resources is made available to the HTML template to embed in the output HTML.

##HTML Example
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

        <form method="POST">
            <input type="hidden" name="car.id" value="PATH.id"/>

            <label for="registration">Registration</label>
            <input name="registration" value="car.registration"/>

            <input type="submit"/>
        </form>
    </body>
</html>
```
    
##Configuration example
```
{
    "resources":[
        {
            name:"Vehicle",
            url:"https://vehicle.api.jediq.com/{identifier}",
            methods:"GET,POST,PUT,DELETE"
        },
        {
            name:"User",
            url:"https://user.api.jediq.com/{identifier}",
            methods:"GET"
        }
    ],
    "templates":"~/skinnyFE/templates"
}
```


##Building

SkinnyFE is built using gradle with the following command : 

    gradle clean build
    
to execute with test coverage and local sonarqube integration use : 

    gradle clean test jacoco sonarqube -Dsonar.scm.disabled=true

  