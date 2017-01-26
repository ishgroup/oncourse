import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import BuyButton from '../components/buyButton/BuyButton';
import { addProduct as add } from '../actions/cart';

function isAdded(items, id) {
    let item = items.find((item) => {
        return item.id === id;
    });

    return !!item && !item.pending;
}

export default connect((state, ownProps) => {
    return {
        isAdded: isAdded(state.cart.products, ownProps.id)
    };
}, (dispatch) => {
    return bindActionCreators({ add }, dispatch);
})(BuyButton);