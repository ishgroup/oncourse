import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import {BuyButton, BuyButtonProps} from '../components/buyButton/BuyButton';
import { addProduct as add } from '../actions/cart';
import {Product} from "../services/IshState";

function isAdded(items: Product[], id: number) {
    const item = items.find((item) => item.id === id);
    return !!item && !item.pending;
}

export default connect((state, ownProps: BuyButtonProps) => {
    return {
        isAdded: isAdded(state.cart.products, ownProps.id)
    };
}, (dispatch) => {
    return bindActionCreators({ add }, dispatch);
})(BuyButton as any);
