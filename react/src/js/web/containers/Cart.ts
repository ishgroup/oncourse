import Cart, {Props} from "../components/cart/Cart";
import {connect} from "react-redux";
import {CourseClassCart, IshState, ProductCart} from "../../services/IshState";
import {Actions} from "../actions/Actions";

export default connect(mapStateToProps, mapDispatchToProps)(Cart as any);

function mapStateToProps(state: IshState, ownProps: Props) {
  return {
    classes: state.cart.courses,
    products: state.cart.products,
    checkoutPath: state.checkoutPath,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    removeClass: (courseClass: CourseClassCart) => {
      dispatch({
        type: Actions.REMOVE_CLASS_FROM_CART,
        payload: courseClass,
      });
    },
    removeProduct: (product: ProductCart) => {
      dispatch({
        type: Actions.REMOVE_PRODUCT_FROM_CART,
        payload: product,
      });
    },
  };
}
