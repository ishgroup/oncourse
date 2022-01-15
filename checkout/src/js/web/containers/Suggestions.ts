/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { IshState } from '../../services/IshState';
import Suggestions from '../components/suggestions/Suggestions';
import { addClassToCart, addProductToCart, requestSuggestion } from '../actions/Actions';
import CheckoutService from '../../enrol/services/CheckoutService';
import { CourseClass, Product } from '../../model';
import { getAllContactNodesFromBackend } from '../../enrol/containers/summary/actions/Actions';

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
    dispatch(requestSuggestion());
  },
  addProduct: (product: Product) => {
    dispatch(addProductToCart(product));
    dispatch(getAllContactNodesFromBackend());
  },
  addCourse: (courseClass: CourseClass) => {
    dispatch(addClassToCart(courseClass));
    dispatch(getAllContactNodesFromBackend());
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Suggestions);
