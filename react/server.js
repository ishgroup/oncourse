let staticServer = require('node-static'),
    fileServer = new staticServer.Server('./dist');

require('http').createServer(function (request, response) {
    request.addListener('end', function () {
        fileServer.serve(request, response);
    }).resume();
}).listen(8080);
