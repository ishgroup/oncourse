import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";
import {tryAnotherCard} from "./actions/Actions";
import {resetCheckoutState} from "../../actions/Actions";
import {resetPaymentState} from "../payment/actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    response: state.checkout.payment.value,
    successLink: state.checkout.preferences.successLink,
  };
};

export const ActionsBy = (dispatch: Dispatch<IshState>): any => {
  return {
    onCancel: () => {
      dispatch(resetCheckoutState());

      // default redirect to courses page
      document.location.href = '/courses';
    },
    onAnotherCard: () => dispatch(tryAnotherCard()),
  };
};

export const Result = connect(PropsBy, ActionsBy)(ResultComp);

