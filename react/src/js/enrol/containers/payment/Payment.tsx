import React from "react";
import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {PaymentComp} from "./components/PaymentComp";
import CreditCardComp from "./components/CreditCardComp";
import {Container as CreditCartForm} from "./CreditCartForm"

const PropsBy = (state: IshState): any => {
  return {
    contacts: [state.checkout.payer.entity],
    amount: state.checkout.amount,
    paymentForm: <CreditCartForm/>
  }
};

export const ActionsBy = (dispatch: Dispatch<any>): any => {
  return {};
};

const Container = connect(PropsBy, ActionsBy)(PaymentComp);

export default Container