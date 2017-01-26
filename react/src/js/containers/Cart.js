import Cart from '../components/cart/Cart';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { removeClass, removeProduct } from '../actions/cart';

export default connect((state) => {
    return {
        classes: state.cart.courses,
        products: state.cart.products
    };
}, (dispatch) => {
    return bindActionCreators({
        removeClass,
        removeProduct
    }, dispatch);
})(Cart);