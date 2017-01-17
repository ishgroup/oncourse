let path = require('path'),
    express = require('express'),
    bodyParser = require('body-parser'),
    app = express();

const COURSES = require('./mock/courses.json');

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, '/templates'));

app.use(express.static(path.join(__dirname, '../dist')));
app.use(bodyParser.json());

app.get('/', (req, res) => {
    res.render('college');
});

app.put('/api/v1/cart/courses/:id', (req, res) => {
    let course = COURSES.find((course) => {
        return course.id === +req.params.id;
    });

    if(!course) {
        res.send(404);
        return;
    }

    res.status(200).send(course);
});

app.delete('/api/v1/cart/courses/:id', (req, res) => {
    let course = COURSES.find((course) => {
        return course.id === +req.params.id;
    });

    if(!course) {
        res.status(404).send();
        return;
    }

    res.status(204).send();
});

app.listen(process.env.PORT || 8080);
