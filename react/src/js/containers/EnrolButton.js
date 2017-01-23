import EnrolButton from '../components/enrolButton/EnrolButton';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { addClass } from '../actions/cart';



function isAdded(classes, courseClassId) {
    let courseClass = classes.find((courseClass) => {
        return courseClass.id === courseClassId;
    });

    return !!courseClass;
}

export default connect((state, ownProps) => {
    return {
        isAdded: isAdded(state.cart.courses, ownProps.classId)
    };
}, (dispatch) => {
    return bindActionCreators({ addClass }, dispatch);
})(EnrolButton);
