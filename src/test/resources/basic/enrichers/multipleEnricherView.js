
var enrich = function(dataString) {

    var data = JSON.parse(dataString);

    data.fruit = "Split";

    return JSON.stringify(data);
};