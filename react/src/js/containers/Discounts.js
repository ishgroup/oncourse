import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import Discounts from '../components/discounts/Discounts';
import { addDiscount as add } from '../actions/cart';

export default connect((state) => {
    return {
        discounts: state.cart.discounts
    };
}, (dispatch) => {
    return bindActionCreators({ add }, dispatch);
})(Discounts);