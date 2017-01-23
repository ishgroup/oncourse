import Cart from '../components/cart/Cart';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { plural } from 'js/lib/utils';
import { removeClass, removeProduct } from '../actions/cart';

function getCourses(classes) {
    return classes.reduce((arr, classCourse) => {
        if(classCourse.pending || classCourse.error) {
            return arr;
        }

        arr.push(classCourse.data);
        return arr;
    }, []);
}

export default connect((state) => {
    return {
        classes: getCourses(state.cart.courses),
        products: state.cart.products
    };
}, (dispatch) => {
    return bindActionCreators({
        removeClass,
        removeProduct
    }, dispatch);
})(Cart);