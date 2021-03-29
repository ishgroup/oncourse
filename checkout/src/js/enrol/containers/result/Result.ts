import {connect} from "react-redux";
import {Dispatch} from "redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";
import {tryAnotherCard} from "./actions/Actions";
import {changePhase, resetCheckoutState} from "../../actions/Actions";
import {resetPaymentState, resetPaymentStateOnInit} from "../payment/actions/Actions";
import {Phase} from "../../reducers/State";
import {IshAction} from "../../../actions/IshAction";

const PropsBy = (state: IshState): any => {
  return {
    response: state.checkout.payment.value,
    successLink: state.config.paymentSuccessURL,
    resetOnInit: state.checkout.payment.resetOnInit,
    result: state.checkout.payment.result,
  };
};

export const ActionsBy = (dispatch: Dispatch<IshAction<any>>): any => {
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

export const Result = connect<any,any,any>(PropsBy, ActionsBy)(ResultComp);

