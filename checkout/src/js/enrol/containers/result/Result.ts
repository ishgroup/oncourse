import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";
import {tryAnotherCard} from "./actions/Actions";
import {changePhase, resetCheckoutState} from "../../actions/Actions";
import {resetPaymentState, resetPaymentStateOnInit} from "../payment/actions/Actions";
import {Phase} from "../../reducers/State";

const PropsBy = (state: IshState): any => {
  return {
    response: state.checkout.payment.value,
    successLink: state.checkout.preferences.successLink,
    resetOnInit: state.checkout.payment.resetOnInit,
  };
};

export const ActionsBy = (dispatch: Dispatch<IshState>): any => {
  return {
    onCancel: () => {
      dispatch(resetCheckoutState());
      dispatch(resetPaymentStateOnInit());

      // default redirect to courses page
      document.location.href = '/courses';
    },
    onInit: () => {
      dispatch(resetPaymentState());
      dispatch(changePhase(Phase.Init));
    },
    onAnotherCard: () => dispatch(tryAnotherCard()),
  };
};

export const Result = connect(PropsBy, ActionsBy)(ResultComp);

