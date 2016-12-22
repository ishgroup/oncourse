import render from './studentListTpl';
import 'styles/enrol/components/studentList.css';

class StudentList extends React.Component {

    render() {
        return render.apply({
            students: this.props.students
        });
    }
}

export default StudentList;
