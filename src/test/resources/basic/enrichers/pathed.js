
var enrich = function(dataString) {

    var data = JSON.parse(dataString);
    force.responseCode(data.car._meta.code);

    return JSON.stringify(data);
};