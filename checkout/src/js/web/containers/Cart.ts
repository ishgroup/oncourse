import Cart, {Props} from "../components/cart/Cart";
import {connect} from "react-redux";
import {CourseClassCart, IshState, ProductCart} from "../../services/IshState";
import {Actions} from "../actions/Actions";
import {GABuilder} from "../../services/GoogleAnalyticsService";

export default connect(mapStateToProps, mapDispatchToProps)(Cart as any);

function mapStateToProps(state: IshState, ownProps: Props) {
  return {
    classes: state.cart.courses,
    products: state.cart.products,
    checkoutPath: state.config.checkoutPath,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    removeClass: (courseClass: CourseClassCart) => {
      dispatch({
        type: Actions.REMOVE_CLASS_FROM_CART,
        payload: courseClass,
        meta: {
          analytics: GABuilder.removeItemFromCart('CourseClass', courseClass.id)
        },
      });
    },
    removeProduct: (product: ProductCart) => {
      dispatch({
        type: Actions.REMOVE_PRODUCT_FROM_CART,
        payload: product,
        meta: {
          analytics: GABuilder.removeItemFromCart('Product', product.id)
        },
      });
    },
  };
}
