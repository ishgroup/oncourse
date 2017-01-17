import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import classnames from 'classnames';
import { addCourse as onEnrol } from '../actions/cart';

class EnrolButton extends React.Component {

    constructor() {
        super();
        this.add = () => this.props.onEnrol(this.props.id);
    }

    render() {
        let isAdded = this.props.isAdded;

        return (
            <a title="Enrol in this class." className={classnames('enrolAction abtn alpha', { 'enrol-added-class': isAdded })} onClick={isAdded ? null : this.add}>{isAdded ? 'Added' : 'Enrol In This Class'}</a>
        );
    }
}

EnrolButton.propTypes = {
    id: React.PropTypes.number,
    isAdded: React.PropTypes.bool,
    onEnrol: React.PropTypes.func
};

function isAdded(courses, courseId) {
    let course = courses.find((course) => {
        return course.id === courseId;
    });

    return !!course;
}

export default connect((state, ownProps) => {
    return {
        isAdded: isAdded(state.cart.courses, ownProps.id)
    };
}, (dispatch) => {
    return bindActionCreators({ onEnrol }, dispatch);
})(EnrolButton);