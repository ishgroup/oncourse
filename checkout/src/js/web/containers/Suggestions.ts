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

const mapStateToProps = (state: IshState) => ({
  phase: state.checkout.phase,
  suggestions: state.suggestions,
  isCartEmpty: CheckoutService.cartIsEmpty(state.cart)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSuggestions: () => {
    dispatch({
      type: Actions.REQUEST_SUGGESTION
    });
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Suggestions);
