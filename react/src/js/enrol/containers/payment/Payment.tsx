import React from "react";
import {connect, Dispatch} from "react-redux";
import {IshState} from "../../../services/IshState";
import {PaymentComp} from "./components/PaymentComp";
import PaymentForm from "./components/PaymentForm";
import {checkIfCorporatePassEnabled} from "./actions/Actions";
import {addCode, toggleRedeemVoucher, updateAmount} from "../../actions/Actions";

const PropsBy = (state: IshState): any => {
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    redeemVouchers: state.checkout.redeemVouchers,
    paymentForm: <PaymentForm/>,
    promotions:  Object.values(state.cart.promotions.entities),
  };
};

export const ActionsBy = (dispatch: Dispatch<any>): any => {
  return {
    onAddCode: (code: string): void => {
      dispatch(addCode(code));
    },
    onUpdatePayNow: (amount, val) => {
      dispatch(updateAmount({...amount, payNow: val}));
    },
    onToggleVoucher: (voucher, enabled) => {
      dispatch(toggleRedeemVoucher(voucher, enabled));
    },
    onInit: () => {
      dispatch(checkIfCorporatePassEnabled());
    },
  };
};

export const Payment = connect(PropsBy, ActionsBy)(PaymentComp);

