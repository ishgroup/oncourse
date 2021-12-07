/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {IshState} from "../../services/IshState";
import {connect} from "react-redux";
import Suggestions from "../components/suggestions/Suggestions";
import {Dispatch} from "redux";
import {Actions} from "../actions/Actions";
import CheckoutService from "../../enrol/services/CheckoutService";
import { CourseClass, Product } from '../../model';
import { GABuilder } from '../../services/GoogleAnalyticsService';

const mapStateToProps = (state: IshState) => ({
  phase: state.checkout.phase,
  suggestions: state.suggestions,
  products: state.products?.entities,
  courses: state.courses?.entities,
  cart: state.cart,
  isCartEmpty: CheckoutService.cartIsEmpty(state.cart)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSuggestions: () => {
    dispatch({
      type: Actions.REQUEST_SUGGESTION
    });
  },
  addProduct: (product: Product) => {
    dispatch({
      type: Actions.ADD_PRODUCT_TO_CART,
      payload: product,
      meta: {
        analytics: GABuilder.addProductToCart(product),
      },
    });
  },
  addCourse: (courseClass: CourseClass) => {
    dispatch({
      type: Actions.ADD_CLASS_TO_CART,
      payload: courseClass,
      meta: {
        analytics: GABuilder.addCourseClassToCart('class', courseClass),
      },
    });
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Suggestions);
