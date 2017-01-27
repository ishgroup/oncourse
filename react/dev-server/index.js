let path = require('path'),
    express = require('express'),
    bodyParser = require('body-parser'),
    app = express();

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, '/templates'));

app.use(express.static(path.join(__dirname, '../dist')));
app.use(bodyParser.json());

app.get('/', (req, res) => {
    res.render('index');
});

app.put('/api/v1/cart/courses/:id', (req, res) => {
    res.status(200).send(require('./mock/addCourseClass'));
});

app.delete('/api/v1/cart/courses/:id', (req, res) => {
    res.status(204).send();
});

app.put('/api/v1/cart/products/:id', (req, res) => {
    res.status(200).send(require('./mock/addProduct'));
});

app.delete('/api/v1/cart/products/:id', (req, res) => {
    res.status(204).send();
});


app.listen(process.env.PORT || 8080);
