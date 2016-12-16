import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {reduxForm} from 'redux-form';

import PersonForm from '../components/PersonForm';
import StudentEnrolList from '../components/StudentEnrolList';
import {addStudent} from '../actions/enrol';
import {create as createStudent} from '../actions/student';

const StudentForm = reduxForm({
    form: 'add_student'
})(PersonForm);

class Enrol extends React.Component {

    validateStudent = (student) => {
        let studentHash = student.first_name + '|' + student.last_name + '|' + student.email;

        return this.props.studentsHash.indexOf(studentHash) === -1;
    };

    onSubmit = (student) => {
        return this.props.createStudent(student)
            .then(() => {
                this.props.addStudent(student);
            });
    };

    render() {
        return (
            <div>
                <StudentForm onSubmit={this.onSubmit} onValidate={this.validateStudent}/>
                <StudentEnrolList students={this.props.students}/>
            </div>
        );
    }
}

export default connect((state) => {
    return {
        studentsHash: state.enrol.studentsHash,
        students: state.enrol.students
    };
}, (dispatch) => {
    return bindActionCreators({
        addStudent, createStudent
    }, dispatch);
})(Enrol);
