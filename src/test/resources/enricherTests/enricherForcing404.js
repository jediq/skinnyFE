
var enrich = function(dataString) {

  var data = JSON.parse(dataString);

  force.responseCode(404);

  return JSON.stringify(data);
};