
var enrich = function(dataString) {

  var data = JSON.parse(dataString);

  data.apple = "green";

  return JSON.stringify(data);
};