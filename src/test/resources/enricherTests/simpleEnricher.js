
var enrich = function(dataString) {

  var data = JSON.parse(dataString);

  data.banana = "yellow";

  return JSON.stringify(data);
};