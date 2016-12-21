let express = require('express'),
    bodyParser = require('body-parser'),
    app = express(),
    shortid = require('shortid');

app.use(express.static(__dirname + '/dist'));
app.use(bodyParser.json());

app.get('/api/students/find', function (req, res) {
    res.json(null);
});

app.post('/api/students', function (req, res) {
    req.body.id = shortid.generate();
    res.send(req.body);
});

app.listen(process.env.PORT || 8080);
