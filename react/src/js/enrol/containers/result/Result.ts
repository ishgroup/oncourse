import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {ResultComp} from "./components/ResultComp";
import {cancelCheckoutProcess, tryAnotherCard} from "./actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    response: state.checkout.payment.value,
    successLink: state.checkout.preferences.successLink,
  };
};

export const ActionsBy = (dispatch: Dispatch<IshState>): any => {
  return {
    onCancel: () => dispatch(cancelCheckoutProcess()),
    onAnotherCard: () => dispatch(tryAnotherCard()),
  };
};

export const Result = connect(PropsBy, ActionsBy)(ResultComp);

