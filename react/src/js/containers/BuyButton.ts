import {connect} from 'react-redux';
import {BuyButton, BuyButtonProps} from '../components/buyButton/BuyButton';
import {IshState, ProductCart, ProductCartState} from "../services/IshState";
import {IshActions} from "../constants/IshActions";
import {Product} from "../model/Product";

export default connect(mapStateToProps, mapDispatchToProps)(BuyButton);

function isAdded(items: ProductCartState, id: string) {
  const item = items.result.find(itemId => itemId === id);
  return !!item;
}

function mapDispatchToProps(dispatch) {
  return {
    addProduct: (product: Product) => {
      dispatch({
        type: IshActions.ADD_PRODUCT_TO_CART,
        payload: product
      });
    },
    requestProductById: (id: string) => {
      dispatch({
        type: IshActions.REQUEST_PRODUCT,
        payload: id
      });
    }
  };
}

function mapStateToProps(state: IshState, ownProps: BuyButtonProps) {
  return {
    product: state.products.entities[ownProps.id] || {},
    isAdded: isAdded(state.cart.products, ownProps.id)
  };
}
