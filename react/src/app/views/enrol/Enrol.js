import { render } from 'react-dom';
import student from 'app/api/student';
import PersonForm from 'app/components/personForm/PersonForm';
import StudentList from 'app/components/studentList/StudentList';
import renderCmp from './enrolTpl';

import 'css/views/enrol.css';

class Enrol extends React.Component {

    constructor() {
        super();
        this.state = {
            students: [],
            studentsHash: []
        };
    }

    addStudent = (student) => {
        let { students, studentsHash} = this.state;

        this.setState({
            students: [...students, student],
            studentsHash: [...studentsHash, student.id]
        });
    };

    validate = (student) => {
        return this.state.students.indexOf(student.id) === -1 ? null : 'User already is added in the list';
    };

    submit = (data) => {
        return student.find()
            .then((response) => {
                if(response.data === null) {
                    return student.create(data);
                } else {
                    return response;
                }
            })
            .then((response) => {
                this.addStudent(response.data);
                return response;
            });
    };

    render() {
        return renderCmp.apply({
            submit: this.submit,
            validate: this.validate,
            students: this.state.students,
            PersonForm, StudentList
        });
    }
}

render(<Enrol/>, document.getElementById('main'));
