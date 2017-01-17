import Cart from '../components/cart/Cart';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { plural } from 'js/lib/utils';
import { removeCourse as onRemoveCourse } from '../actions/cart';

function getCourses(courses) {
    return courses.reduce((arr, course) => {
        if(course.pending || course.error) {
            return arr;
        }

        arr.push(course.data);
        return arr;
    }, []);
}

export default connect((state) => {
    return {
        courses: getCourses(state.cart.courses)
    };
}, (dispatch) => {
    return bindActionCreators({ onRemoveCourse }, dispatch);
})(Cart);