import EnrolButton from '../components/enrolButton/EnrolButton';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { addCourse as onEnrol } from '../actions/cart';



function isAdded(courses, courseId) {
    let course = courses.find((course) => {
        return course.id === courseId;
    });

    return !!course;
}

export default connect((state, ownProps) => {
    return {
        isAdded: isAdded(state.cart.courses, ownProps.classId)
    };
}, (dispatch) => {
    return bindActionCreators({ onEnrol }, dispatch);
})(EnrolButton);
