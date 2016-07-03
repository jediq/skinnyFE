
var enrich = function(dataString) {

  var data = JSON.parse(dataString);

  data.posts.array.forEach(function(post) {
    post.title = post.title ? post.title.toUpperCase() : "";
    post.author = post.author ? post.author.toUpperCase() : "";
  });

  return JSON.stringify(data);
};