import Cart, {Props} from "../components/cart/Cart";
import {connect} from "react-redux";
import {CourseClassCart, IshState, ProductCart} from "../../services/IshState";
import {Actions} from "../actions/Actions";
import {GABuilder} from "../../services/GoogleAnalyticsService";
import {Course} from "../../model";

export default connect<any, any, any, IshState>(mapStateToProps, mapDispatchToProps)(Cart as any);

function mapStateToProps(state: IshState) {
  return {
    classes: state.cart.courses,
    products: state.cart.products,
    waitingCourses: state.cart.waitingCourses,
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
          analytics: GABuilder.removeCourseClassFromCart('class', courseClass),
        },
      });
    },
    removeProduct: (product: ProductCart) => {
      dispatch({
        type: Actions.REMOVE_PRODUCT_FROM_CART,
        payload: product,
        meta: {
          analytics: GABuilder.removeProductFromCart(product),
        },
      });
    },
    removeWaitingCourse: (course: Course) => {
      dispatch({
        type: Actions.REMOVE_WAITING_COURSE_FROM_CART,
        payload: course,
        // meta: {
        //   analytics: GABuilder.removeProductFromCart('Product', product),
        // },
      });
    },
  };
}
