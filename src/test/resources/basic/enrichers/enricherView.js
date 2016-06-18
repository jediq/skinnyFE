
var enrich = function(dataString) {

    var data = JSON.parse(dataString)

    data.fruit = "Banana"

    return JSON.stringify(data);
}