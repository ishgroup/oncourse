import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";
import {tryAnotherCard} from "./actions/Actions";
import {resetCheckoutState} from "../../actions/Actions";
import {resetPaymentState, resetPaymentStateOnDestroy} from "../payment/actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    response: state.checkout.payment.value,
    successLink: state.checkout.preferences.successLink,
    resetOnDestroy: state.checkout.payment.resetOnDestroy,
  };
};

export const ActionsBy = (dispatch: Dispatch<IshState>): any => {
  return {
    onCancel: () => {
      dispatch(resetCheckoutState());
      dispatch(resetPaymentStateOnDestroy());

      // default redirect to courses page
      document.location.href = '/courses';
    },
    onDestroy: () => {
      dispatch(resetPaymentState());
    },
    onAnotherCard: () => dispatch(tryAnotherCard()),
  };
};

export const Result = connect(PropsBy, ActionsBy)(ResultComp);

