import 'css/components/studentEnrolList.css';

class StudentEnrolList extends React.Component {

    render() {
        let props = this.props;

        if(!props.students.length) {
            return null;
        }

        return (
            <div className="enrol-list">
                <div className="enrol-list__header enrol-list__row">Student List</div>
                {props.students.map((item, index) => {
                    return <div key={index} className="enrol-list__row">{item.first_name} {item.last_name} ({item.email})</div>;
                })}
            </div>
        );
    }

}

export default StudentEnrolList;