import Cart, {CartProps} from "../components/cart/Cart";
import {connect} from "react-redux";
import {CourseClassCart, IshState, ProductCart} from "../../services/IshState";
import {IshActions} from "../../constants/IshActions";

export default connect(mapStateToProps, mapDispatchToProps)(Cart as any);

function mapStateToProps(state: IshState, ownProps: CartProps) {
  return {
    classes: state.cart.courses,
    products: state.cart.products
  };
}

function mapDispatchToProps(dispatch) {
  return {
    removeClass: (courseClass: CourseClassCart) => {
      dispatch({
        type: IshActions.REMOVE_CLASS_FROM_CART,
        payload: courseClass
      });
    },
    removeProduct: (product: ProductCart) => {
      dispatch({
        type: IshActions.REMOVE_PRODUCT_FROM_CART,
        payload: product
      });
    },
  };
}
