
var enrich = function(dataString) {

  var data = JSON.parse(dataString);

  data.apple = "green";

  force.stopEnriching();

  return JSON.stringify(data);
};