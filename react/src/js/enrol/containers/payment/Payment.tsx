import React from "react";
import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {PaymentComp} from "./components/PaymentComp";
import {Container as CreditCartForm} from "./CreditCartForm";
import {addCode} from "../../actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    contacts: [state.checkout.payer.entity],
    amount: state.checkout.amount,
    paymentForm: <CreditCartForm/>,
    promotions:  Object.values(state.cart.promotions.entities),
  };
};

export const ActionsBy = (dispatch: Dispatch<any>): any => {
  return {
    onAddCode: (code: string): void => {
      dispatch(addCode(code));
    },
  };
};

export const Payment = connect(PropsBy, ActionsBy)(PaymentComp);

